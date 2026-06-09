<template>
  <div class="technician-dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon orders">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.pendingOrders }}</div>
              <div class="stat-label">待处理订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon appointments">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.todayAppointments }}</div>
              <div class="stat-label">今日预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.completedCount }}</div>
              <div class="stat-label">已完成服务</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon rating">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.rating && stats.rating > 0 ? stats.rating : 5.0 }}</div>
              <div class="stat-label">评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 快捷操作 -->
    <el-card class="quick-actions" style="margin-top: 20px;">
      <h3>快捷操作</h3>
      <div class="action-buttons">
        <el-button type="primary" @click="router.push('/technician/orders')">
          <el-icon><ShoppingCart /></el-icon>
          查看我的订单
        </el-button>
        <el-button type="success" @click="router.push('/technician/appointments')">
          <el-icon><Calendar /></el-icon>
          处理预约
        </el-button>
        <el-button type="info" @click="router.push('/technician/history')">
          <el-icon><Document /></el-icon>
          服务记录
        </el-button>
        <el-button type="warning" @click="router.push('/technician/reviews')">
          <el-icon><Star /></el-icon>
          查看评价
        </el-button>
        <el-button @click="toggleStatus">
          <el-icon><SwitchButton /></el-icon>
          {{ technicianInfo.status === 1 ? '设为休息' : '开始工作' }}
        </el-button>
      </div>
    </el-card>
    
    <!-- 今日预约列表 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3>今日预约</h3>
          <el-button text @click="router.push('/technician/appointments')">查看全部</el-button>
        </div>
      </template>
      <el-table :data="todayAppointments" v-loading="loading">
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="petName" label="宠物信息" width="150" />
        <el-table-column prop="serviceName" label="服务项目" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleAccept(row)" 
                       v-if="row.status === 0">接受</el-button>
            <el-button size="small" type="danger" @click="handleReject(row)" 
                       v-if="row.status === 0">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart, Calendar, CircleCheck, Star, SwitchButton, Document } from '@element-plus/icons-vue'
import { getDashboard, updateStatus } from '@/api/technician'

const router = useRouter()
const loading = ref(false)

const stats = ref({
  pendingOrders: 0,
  todayAppointments: 0,
  completedCount: 0,
  rating: 0
})

const technicianInfo = ref({
  status: 1 // 1-工作中
})

const todayAppointments = ref([])

const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getMonth() + 1}/${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info'
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    0: '待确认',
    1: '已确认',
    2: '已取消',
    3: '已完成'
  }
  return texts[status] || '-'
}

const toggleStatus = async () => {
  const newStatus = technicianInfo.value.status === 1 ? 0 : 1
  try {
    await updateStatus(newStatus)
    technicianInfo.value.status = newStatus
    ElMessage.success(newStatus === 1 ? '已开始工作' : '已设为休息')
  } catch (error) {
    console.error(error)
  }
}

const handleAccept = (appointment) => {
  ElMessageBox.confirm('确定接受此预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    appointment.status = 1
    ElMessage.success('已接受预约')
  }).catch(() => {})
}

const handleReject = (appointment) => {
  ElMessageBox.prompt('请输入拒绝原因', '拒绝预约', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入拒绝原因'
  }).then(({ value }) => {
    appointment.status = 2
    ElMessage.success('已拒绝预约')
  }).catch(() => {})
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDashboard()
    console.log('Dashboard数据:', res.data)
    stats.value = {
      pendingOrders: res.data.pendingOrders || 0,
      todayAppointments: res.data.todayAppointments || 0,
      completedCount: res.data.completedCount || 0,
      rating: res.data.rating && res.data.rating > 0 ? res.data.rating : 5.0
    }
    console.log('处理后的stats:', stats.value)
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.technician-dashboard {
  padding: 10px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px !important;
  overflow: hidden;
  position: relative;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15) !important;
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 10px;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-icon::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover .stat-icon {
  transform: rotate(10deg) scale(1.1);
}

.stat-card:hover .stat-icon::after {
  opacity: 1;
}

.stat-icon.orders {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.appointments {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rating {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.quick-actions {
  margin-top: 20px;
}

.quick-actions h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #1e293b;
  font-weight: 700;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.action-buttons :deep(.el-button) {
  font-weight: 500;
  transition: all 0.3s;
  border-radius: 10px !important;
}

.action-buttons :deep(.el-button:hover) {
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
  font-weight: 700;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stat-number {
    font-size: 24px;
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    font-size: 28px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-buttons :deep(.el-button) {
    width: 100%;
  }
}
</style>
