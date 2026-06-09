<template>
  <div class="pets-page">
    <div class="container">
      <div class="header">
        <h2 class="page-title">我的宠物</h2>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon> 添加宠物
        </el-button>
      </div>
      
      <!-- 宠物列表 -->
      <el-row :gutter="20" v-loading="loading">
        <el-col :span="8" v-for="pet in pets" :key="pet.id">
          <el-card class="pet-card" shadow="hover">
            <div class="pet-header">
              <el-avatar :size="60" :src="pet.photo ? 'http://localhost:8080/api' + pet.photo : ''">
                <el-icon :size="30"><Van /></el-icon>
              </el-avatar>
              <div class="pet-info">
                <h3>{{ pet.name }}</h3>
                <p>{{ getTypeText(pet.type) }} · {{ pet.breed }}</p>
              </div>
            </div>
            <div class="pet-details">
              <div class="detail-item">
                <span class="label">年龄：</span>
                <span>{{ pet.age }}岁</span>
              </div>
              <div class="detail-item">
                <span class="label">性别：</span>
                <span>{{ getGenderText(pet.gender) }}</span>
              </div>
              <div class="detail-item">
                <span class="label">体重：</span>
                <span>{{ pet.weight }}kg</span>
              </div>
              <div class="detail-item">
                <span class="label">颜色：</span>
                <span>{{ pet.color }}</span>
              </div>
            </div>
            <div class="actions">
              <el-button size="small" @click="showEditDialog(pet)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(pet.id)">删除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-empty v-if="!loading && pets.length === 0" description="还没有宠物，快去添加吧" />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑宠物' : '添加宠物'" width="500px">
      <el-form :model="petForm" :rules="rules" ref="petFormRef" label-width="80px">
        <el-form-item label="宠物名称" prop="name">
          <el-input v-model="petForm.name" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-form-item label="宠物类型" prop="type">
          <el-select v-model="petForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="狗" value="DOG" />
            <el-option label="猫" value="CAT" />
            <el-option label="鸟" value="BIRD" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="petForm.breed" placeholder="请输入品种" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="petForm.age" :min="0" :max="30" style="width: 100%">
            <template #suffix>
              <span style="color: #909399; font-size: 14px;">岁</span>
            </template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="petForm.gender">
            <el-radio label="MALE">公</el-radio>
            <el-radio label="FEMALE">母</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="体重(kg)" prop="weight">
          <el-input-number v-model="petForm.weight" :min="0" :max="100" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="颜色" prop="color">
          <el-input v-model="petForm.color" placeholder="请输入颜色" />
        </el-form-item>
        <el-form-item label="宠物照片">
          <div class="photo-upload">
            <el-avatar :size="100" :src="tempPhotoUrl || ''">
              <el-icon :size="40"><Picture /></el-icon>
            </el-avatar>
            <el-upload
              class="photo-uploader"
              :show-file-list="false"
              :before-upload="beforePhotoUpload"
              :http-request="handlePhotoUpload"
              accept="image/*"
            >
              <el-button type="primary" size="small" style="margin-top: 10px;">
                {{ tempPhotoUrl ? '更换照片' : '上传照片' }}
              </el-button>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item label="健康备注">
          <el-input v-model="petForm.healthNote" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Plus, Van, Picture } from '@element-plus/icons-vue'
import { getMyPets, addPet, updatePet, deletePet, uploadPetPhoto } from '@/api/pet'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const pets = ref([])
const dialogVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const petFormRef = ref(null)
const tempPhotoUrl = ref('')

const petForm = reactive({
  id: null,
  name: '',
  type: '',
  breed: '',
  age: 0,
  gender: 'MALE',
  weight: 0,
  color: '',
  healthNote: ''
})

const rules = {
  name: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择宠物类型', trigger: 'change' }],
  breed: [{ required: true, message: '请输入品种', trigger: 'blur' }]
}

onMounted(() => {
  loadPets()
})

const loadPets = async () => {
  if (!userStore.token) {
    pets.value = []
    return
  }
  loading.value = true
  try {
    const res = await getMyPets()
    pets.value = res.data || []
  } catch (error) {
    console.error('加载宠物列表失败:', error)
    // 静默失败，不影响页面展示
    pets.value = []
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const showEditDialog = (pet) => {
  isEdit.value = true
  Object.assign(petForm, pet)
  // 如果有照片，设置临时URL
  if (pet.photo) {
    tempPhotoUrl.value = 'http://localhost:8080/api' + pet.photo
  } else {
    tempPhotoUrl.value = ''
  }
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(petForm, {
    id: null,
    name: '',
    type: '',
    breed: '',
    age: 0,
    gender: 'MALE',
    weight: 0,
    color: '',
    photo: '',
    healthNote: ''
  })
  tempPhotoUrl.value = ''
}

const handleSubmit = async () => {
  const valid = await petFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updatePet(petForm)
      ElMessage.success('更新成功')
    } else {
      await addPet(petForm)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadPets()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这个宠物吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePet(id)
    ElMessage.success('删除成功')
    loadPets()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

// 照片上传前验证
const beforePhotoUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
  }
  return isImage && isLt5M
}

// 自定义上传照片
const handlePhotoUpload = async (options) => {
  const { file } = options
  const formData = new FormData()
  formData.append('file', file)

  const loading = ElLoading.service({
    lock: true,
    text: '上传中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    const res = await uploadPetPhoto(formData)
    if (res.code === 200) {
      tempPhotoUrl.value = 'http://localhost:8080/api' + res.data.url
      petForm.photo = res.data.url
      ElMessage.success('照片上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    loading.close()
  }
}

const getTypeText = (type) => {
  const map = { DOG: '狗', CAT: '猫', BIRD: '鸟', OTHER: '其他' }
  return map[type] || type
}

const getGenderText = (gender) => {
  return gender === 'MALE' ? '公' : '母'
}
</script>

<style scoped>
.pets-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  color: #333;
}

.pet-card {
  margin-bottom: 20px;
}

.pet-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.pet-info h3 {
  margin: 0 0 5px 0;
  color: #333;
}

.pet-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.pet-details {
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  color: #666;
  width: 60px;
}

.actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.photo-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.photo-uploader {
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 20px 15px;
  }
  
  /* 页面头部优化 */
  .header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
    margin-bottom: 20px;
  }
  
  .page-title {
    font-size: 24px;
    text-align: center;
  }
  
  .header :deep(.el-button) {
    width: 100%;
  }
  
  /* 宠物卡片单列显示 */
  :deep(.el-col) {
    width: 100% !important;
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }
  
  .pet-card {
    margin-bottom: 20px;
  }
  
  /* 宠物头部垂直布局 */
  .pet-header {
    flex-direction: column;
    text-align: center;
    gap: 12px;
    margin-bottom: 15px;
  }
  
  .pet-header :deep(.el-avatar) {
    width: 70px !important;
    height: 70px !important;
  }
  
  .pet-info h3 {
    font-size: 20px;
    margin: 0 0 8px 0;
  }
  
  .pet-info p {
    font-size: 14px;
  }
  
  /* 详细信息优化 */
  .pet-details {
    margin-bottom: 15px;
    padding: 12px;
    background: #f8fafc;
    border-radius: 8px;
  }
  
  .detail-item {
    font-size: 14px;
    margin-bottom: 10px;
    display: flex;
    justify-content: space-between;
  }
  
  .label {
    color: #64748b;
    font-weight: 500;
    flex-shrink: 0;
  }
  
  /* 操作按钮全宽显示 */
  .actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .actions :deep(.el-button) {
    width: 100%;
  }
  
  /* 添加/编辑对话框优化 */
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
  
  /* 数字输入框优化 */
  :deep(.el-input-number) {
    width: 100% !important;
  }
  
  :deep(.el-input-number .el-input__inner) {
    text-align: left;
  }
  
  /* 单选组优化 */
  :deep(.el-radio-group) {
    display: flex;
    gap: 15px;
    width: 100%;
  }
  
  :deep(.el-radio) {
    margin-right: 0;
  }
  
  /* 照片上传优化 */
  .photo-upload {
    padding: 10px 0;
  }
  
  .photo-upload :deep(.el-avatar) {
    width: 90px !important;
    height: 90px !important;
  }
  
  /* 对话框底部按钮优化 */
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
  .container {
    padding: 15px 10px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .pet-header :deep(.el-avatar) {
    width: 60px !important;
    height: 60px !important;
  }
  
  .pet-info h3 {
    font-size: 18px;
  }
  
  .pet-info p {
    font-size: 13px;
  }
  
  .detail-item {
    font-size: 13px;
  }
}
</style>
