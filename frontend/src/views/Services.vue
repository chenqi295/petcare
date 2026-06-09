<template>
  <div class="services-page">
    <div class="container">
      <h2 class="page-title">服务预约</h2>
      
      <!-- 服务分类 -->
      <el-tabs v-model="activeCategory" @tab-click="handleCategoryChange">
        <el-tab-pane
          v-for="category in categories"
          :key="category.id"
          :label="category.name"
          :name="category.id"
        >
          <!-- 服务列表 -->
          <el-row :gutter="20" v-loading="loading">
            <el-col :span="8" v-for="service in services" :key="service.id">
              <el-card class="service-card" shadow="hover">
                <h3>{{ service.name }}</h3>
                <p class="desc">{{ service.description }}</p>
                <div class="info">
                  <span>时长：{{ service.duration }}分钟</span>
                  <span class="price">¥{{ service.price }}</span>
                </div>
                <el-button type="primary" @click="showBookingDialog(service)">
                  立即预约
                </el-button>
              </el-card>
            </el-col>
          </el-row>
          
          <el-empty v-if="!loading && services.length === 0" description="暂无服务" />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 预约对话框 -->
    <el-dialog v-model="dialogVisible" title="预约服务" width="500px">
      <el-form :model="bookingForm" label-width="100px">
        <el-form-item label="服务项目">
          <el-input v-model="selectedService.name" disabled />
        </el-form-item>
        <el-form-item label="选择宠物" required>
          <el-select v-model="bookingForm.petId" placeholder="请选择宠物" style="width: 100%">
            <el-option
              v-for="pet in pets"
              :key="pet.id"
              :label="pet.name"
              :value="pet.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="选择技师" required>
          <el-select v-model="bookingForm.technicianId" placeholder="请选择技师" style="width: 100%">
            <el-option
              v-for="tech in technicians"
              :key="tech.id"
              :label="tech.name"
              :value="tech.id"
            >
              <span style="float: left">{{ tech.name }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                ⭐ {{ tech.rating && tech.rating > 0 ? tech.rating : 5.0 }} | 服务 {{ tech.serviceCount || 0 }} 次
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="预约时间" required>
          <el-date-picker
            v-model="bookingForm.appointmentTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DD HH:mm"
            :disabled-date="disabledDate"
            :disabled-hours="disabledHours"
            :disabled-minutes="disabledMinutes"
            :now-text="false"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="bookingForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBooking" :loading="bookingLoading">
          确认预约
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategories, getServiceItems, getTechnicians } from '@/api/service'
import { getMyPets } from '@/api/pet'
import { createAppointment } from '@/api/appointment'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const categories = ref([])
const services = ref([])
const activeCategory = ref(null)
const pets = ref([])
const technicians = ref([])

const dialogVisible = ref(false)
const bookingLoading = ref(false)
const selectedService = ref({})
const bookingForm = ref({
  petId: null,
  technicianId: null,
  appointmentTime: '',
  remark: ''
})

onMounted(() => {
  loadCategories()
  loadPets()
  loadTechnicians()
})

const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
    if (categories.value.length > 0) {
      activeCategory.value = categories.value[0].id
      loadServices(activeCategory.value)
    }
  } catch (error) {
    console.error(error)
  }
}

const loadServices = async (categoryId) => {
  loading.value = true
  try {
    const res = await getServiceItems(categoryId)
    services.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = (tab) => {
  loadServices(tab.props.name)
}

const loadPets = async () => {
  if (!userStore.token) {
    pets.value = []
    return
  }
  try {
    const res = await getMyPets()
    pets.value = res.data || []
  } catch (error) {
    console.error('加载宠物列表失败:', error)
    // 静默失败，不影响用户选择服务
    pets.value = []
  }
}

const loadTechnicians = async () => {
  try {
    const res = await getTechnicians()
    technicians.value = res.data || []
  } catch (error) {
    console.error('加载技师列表失败:', error)
    technicians.value = []
  }
}

const showBookingDialog = (service) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  if (pets.value.length === 0) {
    ElMessage.warning('请先添加宠物')
    router.push('/pets')
    return
  }
  if (technicians.value.length === 0) {
    ElMessage.warning('当前没有可用的技师')
    return
  }
  selectedService.value = service
  bookingForm.value = {
    petId: pets.value[0]?.id || null,
    technicianId: technicians.value[0]?.id || null,
    appointmentTime: '',
    remark: ''
  }
  dialogVisible.value = true
}

// 禁用日期：只能选择当前时间1小时以后，且在9:30-22:00之间的时间
const disabledDate = (time) => {
  const now = new Date()
  const oneHourLater = new Date(now.getTime() + 60 * 60 * 1000)
  
  // 获取今天的日期（只比较年月日）
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const selectedDate = new Date(time.getFullYear(), time.getMonth(), time.getDate())
  
  // 禁用今天之前的日期
  if (selectedDate < today) {
    return true
  }
  
  // 如果是今天，检查是否有可选的时间段
  if (selectedDate.getTime() === today.getTime()) {
    // 获取1小时后的时间
    const oneHourLaterDate = new Date(oneHourLater.getFullYear(), oneHourLater.getMonth(), oneHourLater.getDate())
    
    // 如果1小时后的时间不在今天，说明今天已经没有可选时间了
    if (oneHourLaterDate.getTime() !== today.getTime()) {
      return true
    }
    
    // 检查1小时后是否在营业时间内（9:30-22:00）
    const hour = oneHourLater.getHours()
    const minute = oneHourLater.getMinutes()
    
    // 如果1小时后已经过了22:00，或在9:30之前，则今天不可选
    if (hour >= 22 || (hour < 9) || (hour === 9 && minute >= 30)) {
      return false // 今天还有可选时间
    }
    return true // 今天没有可选时间
  }
  
  // 未来日期都可选
  return false
}

// 禁用小时：动态禁用，基于当前时间
const disabledHours = () => {
  const now = new Date()
  const oneHourLater = new Date(now.getTime() + 60 * 60 * 1000)
  const hours = []
  
  // 如果是今天，禁用当前时间1小时以内的小时
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const selectedDate = new Date(bookingForm.value.appointmentTime || now)
  const isSelectedToday = selectedDate.getFullYear() === today.getFullYear() && 
                          selectedDate.getMonth() === today.getMonth() && 
                          selectedDate.getDate() === today.getDate()
  
  if (isSelectedToday) {
    // 今天是特殊处理：禁用0到（当前小时+1）之前的小时
    const minHour = oneHourLater.getHours()
    for (let i = 0; i < minHour; i++) {
      hours.push(i)
    }
  }
  
  // 所有日期：禁用0-8点和22-23点（非营业时间）
  for (let i = 0; i <= 8; i++) {
    if (!hours.includes(i)) hours.push(i)
  }
  for (let i = 22; i <= 23; i++) {
    if (!hours.includes(i)) hours.push(i)
  }
  
  return hours
}

// 禁用分钟：动态禁用，基于当前时间和选中的小时
const disabledMinutes = (hour) => {
  const now = new Date()
  const oneHourLater = new Date(now.getTime() + 60 * 60 * 1000)
  const minutes = []
  
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
  const selectedDate = new Date(bookingForm.value.appointmentTime || now)
  const isSelectedToday = selectedDate.getFullYear() === today.getFullYear() && 
                          selectedDate.getMonth() === today.getMonth() && 
                          selectedDate.getDate() === today.getDate()
  
  // 如果是今天，且选中的小时等于1小时后的小时，需要禁用分钟
  if (isSelectedToday && hour === oneHourLater.getHours()) {
    const minMinute = oneHourLater.getMinutes()
    for (let i = 0; i < minMinute; i++) {
      minutes.push(i)
    }
  }
  
  // 9点时，禁用0-29分
  if (hour === 9) {
    for (let i = 0; i <= 29; i++) {
      if (!minutes.includes(i)) minutes.push(i)
    }
  }
  
  return minutes
}

const handleBooking = async () => {
  if (!bookingForm.value.petId) {
    ElMessage.warning('请选择宠物')
    return
  }
  if (!bookingForm.value.technicianId) {
    ElMessage.warning('请选择技师')
    return
  }
  if (!bookingForm.value.appointmentTime) {
    ElMessage.warning('请选择预约时间')
    return
  }
  
  // 验证时间段：只能选择当前时间1小时以后，且在9:30-22:00之间
  const selectedTime = new Date(bookingForm.value.appointmentTime)
  const now = new Date()
  
  // 将时间都向下取整到分钟（忽略秒），然后比较
  const selectedToMinute = new Date(selectedTime.getFullYear(), selectedTime.getMonth(), selectedTime.getDate(),
                                     selectedTime.getHours(), selectedTime.getMinutes(), 0)
  const nowToMinute = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 
                                now.getHours(), now.getMinutes(), 0)
  // 计算1小时后的时间（精确到分钟）
  const oneHourLater = new Date(nowToMinute.getTime() + 60 * 60 * 1000)
  
  // 验证必须在当前时间1小时以后（精确到分钟比较，忽略秒的差异）
  if (selectedToMinute < oneHourLater) {
    ElMessage.warning('请选择1小时以后的时间')
    return
  }
  
  // 验证必须在营业时间内（9:30-22:00）
  const hour = selectedTime.getHours()
  const minute = selectedTime.getMinutes()
  
  if (hour >= 22 || hour < 9 || (hour === 9 && minute < 30)) {
    ElMessage.warning('请选择9:30-22:00之间的时间')
    return
  }
  
  bookingLoading.value = true
  try {
    // 格式化日期时间为 YYYY-MM-DD HH:mm:00（秒固定为00）
    const date = new Date(bookingForm.value.appointmentTime)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const formattedTime = `${year}-${month}-${day} ${hours}:${minutes}:00`
    
    const data = {
      petId: bookingForm.value.petId,
      technicianId: bookingForm.value.technicianId,
      serviceId: selectedService.value.id,
      appointmentTime: formattedTime,
      remark: bookingForm.value.remark
    }
    console.log('提交预约数据:', data)
    await createAppointment(data)
    ElMessage.success('预约成功')
    dialogVisible.value = false
    router.push('/appointments')
  } catch (error) {
    console.error('预约失败:', error)
    console.error('错误详情:', error.response?.data)
    const errorMsg = error.response?.data?.message || error.message || '预约失败，请稍后重试'
    ElMessage.error(errorMsg)
  } finally {
    bookingLoading.value = false
  }
}
</script>

<style scoped>
.services-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-title {
  font-size: 36px;
  color: #1e293b;
  margin-bottom: 40px;
  font-weight: 700;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-title::before {
  content: '✨';
  font-size: 32px;
}

/* Tabs 样式优化 */
:deep(.el-tabs__header) {
  margin-bottom: 30px !important;
  background: white;
  border-radius: 16px;
  padding: 10px 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

:deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 600;
  color: #64748b;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-tabs__item:hover) {
  color: #667eea;
  transform: translateY(-2px);
}

:deep(.el-tabs__item.is-active) {
  color: #667eea;
  font-weight: 700;
}

:deep(.el-tabs__active-bar) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  height: 3px !important;
}

.service-card {
  margin-bottom: 25px;
  text-align: center;
  padding: 15px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px !important;
  background: white;
  position: relative;
  overflow: hidden;
}

.service-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.service-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.15) !important;
}

.service-card:hover::before {
  opacity: 1;
}

.service-card h3 {
  margin-bottom: 15px;
  color: #1e293b;
  font-size: 20px;
  font-weight: 700;
  position: relative;
  z-index: 1;
}

.desc {
  color: #64748b;
  margin-bottom: 20px;
  min-height: 45px;
  line-height: 1.6;
  font-size: 14px;
  position: relative;
  z-index: 1;
}

.info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  position: relative;
  z-index: 1;
}

.info span {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.price {
  font-size: 28px;
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 800;
}

.service-card :deep(.el-button--primary) {
  width: 100%;
  border-radius: 12px !important;
  font-weight: 600 !important;
  padding: 14px !important;
  font-size: 16px !important;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  position: relative;
  z-index: 1;
}

.service-card :deep(.el-button--primary:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4) !important;
}

/* 空状态优化 */
:deep(.el-empty) {
  padding: 60px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .container {
    padding: 20px 15px;
  }
  
  /* Tabs 优化 */
  :deep(.el-tabs__header) {
    padding: 8px 10px;
    margin-bottom: 20px !important;
  }
  
  :deep(.el-tabs__item) {
    font-size: 14px;
    padding: 0 15px;
  }
  
  /* 服务卡片单列显示 */
  :deep(.el-col) {
    width: 100% !important;
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }
  
  .service-card {
    margin-bottom: 15px;
    padding: 15px;
  }
  
  .service-card h3 {
    font-size: 18px;
    margin-bottom: 12px;
  }
  
  .desc {
    font-size: 14px;
    min-height: auto;
    margin-bottom: 15px;
    line-height: 1.6;
  }
  
  .info {
    flex-direction: column;
    gap: 12px;
    padding: 12px;
    align-items: flex-start;
    margin-bottom: 15px;
  }
  
  .info span:first-child {
    font-size: 14px;
  }
  
  .price {
    font-size: 28px;
  }
  
  .service-card :deep(.el-button--primary) {
    padding: 14px !important;
    font-size: 16px !important;
    height: auto !important;
    min-height: 48px;
  }
  
  /* 预约对话框优化 */
  :deep(.el-dialog) {
    width: 95% !important;
    max-width: 450px;
    margin-top: 5vh !important;
  }
  
  :deep(.el-dialog__body) {
    padding: 20px 15px;
    max-height: 70vh;
    overflow-y: auto;
  }
  
  :deep(.el-form-item__label) {
    font-size: 14px;
    float: none !important;
    text-align: left !important;
    width: 100% !important;
    margin-bottom: 8px;
  }
  
  :deep(.el-form-item__content) {
    margin-left: 0 !important;
    width: 100%;
  }
  
  :deep(.el-input__inner),
  :deep(.el-textarea__inner),
  :deep(.el-select) {
    width: 100% !important;
  }
  
  :deep(.el-input__inner),
  :deep(.el-textarea__inner) {
    font-size: 14px;
    padding: 10px 12px;
  }
  
  :deep(.el-date-editor) {
    width: 100% !important;
  }
  
  :deep(.el-dialog__footer) {
    padding: 15px;
    display: flex;
    gap: 10px;
  }
  
  :deep(.el-dialog__footer .el-button) {
    flex: 1;
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) {
  .page-title {
    font-size: 20px;
  }
  
  .container {
    padding: 15px 10px;
  }
  
  .service-card {
    padding: 12px;
  }
  
  .service-card h3 {
    font-size: 16px;
  }
  
  .desc {
    font-size: 13px;
  }
  
  .price {
    font-size: 24px;
  }
}
</style>

<style>
/* 隐藏日期选择器中的"此刻"按钮 */
.el-picker-panel__footer .el-picker-panel__link-btn {
  display: none !important;
}
</style>
