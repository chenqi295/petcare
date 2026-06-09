<template>
  <div class="technician-profile">
    <el-row :gutter="20">
      <!-- 基本信息 -->
      <el-col :span="16">
        <el-card>
          <template #header>
            <h3>基本信息</h3>
          </template>
          <el-form :model="form" label-width="100px">
            <el-form-item label="技师姓名">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item label="工作年限">
              <el-input-number v-model="form.workYears" :min="0" :max="50" />
            </el-form-item>
            <el-form-item label="个人简介">
              <el-input v-model="form.introduction" type="textarea" :rows="4" />
            </el-form-item>
            <el-form-item label="擅长技能">
              <el-input v-model="form.skills" placeholder="多个技能用逗号分隔，如：美容,洗澡,修剪" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      
      <!-- 工作统计 -->
      <el-col :span="8">
        <el-card>
          <template #header>
            <h3>工作统计</h3>
          </template>
          <div class="stats">
            <div class="stat-item">
              <div class="label">服务次数</div>
              <div class="value">{{ stats.serviceCount }}</div>
            </div>
            <div class="stat-item">
              <div class="label">评分</div>
              <div class="value">
                <el-rate v-model="stats.rating" disabled />
                <span class="rating-text">{{ stats.rating.toFixed(1) }} 分</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="label">工作状态</div>
              <div class="value">
                <el-tag :type="form.status === 1 ? 'success' : 'info'">
                  {{ form.status === 1 ? '工作中' : '休息中' }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card style="margin-top: 20px;">
          <template #header>
            <h3>头像设置</h3>
          </template>
          <div class="avatar-upload">
            <el-avatar :size="120" :src="form.avatar || ''" />
            <el-upload
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="beforeAvatarUpload"
              :http-request="handleAvatarUpload"
              accept="image/*"
            >
              <el-button type="primary" size="small" style="margin-top: 10px;">
                更换头像
              </el-button>
            </el-upload>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { getTechnicianInfo, updateTechnicianInfo, uploadAvatar } from '@/api/technician'

const form = ref({
  name: '',
  phone: '',
  workYears: 0,
  introduction: '',
  skills: '',
  avatar: '',
  status: 1
})

const stats = ref({
  serviceCount: 0,
  rating: 5.0
})

const handleSave = async () => {
  try {
    await updateTechnicianInfo(form.value)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error(error)
    ElMessage.error('保存失败')
  }
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
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

// 自定义上传头像
const handleAvatarUpload = async (options) => {
  const { file } = options
  const formData = new FormData()
  formData.append('file', file)

  const loading = ElLoading.service({
    lock: true,
    text: '上传中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    const res = await uploadAvatar(formData)
    if (res.code === 200) {
      // 拼接完整的图片URL
      form.value.avatar = 'http://localhost:8080/api' + res.data.url
      ElMessage.success('头像上传成功')
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

onMounted(async () => {
  try {
    const res = await getTechnicianInfo()
    form.value = {
      name: res.data.name || '',
      phone: res.data.phone || '',
      workYears: res.data.workYears || 0,
      introduction: res.data.introduction || '',
      skills: res.data.skills || '',
      // 拼接完整的头像URL
      avatar: res.data.avatar ? 'http://localhost:8080/api' + res.data.avatar : '',
      status: res.data.status !== undefined ? res.data.status : 1
    }
    
    stats.value = {
      serviceCount: res.data.serviceCount || 0,
      // 如果没有评分，默认为5分
      rating: res.data.rating && res.data.rating > 0 ? res.data.rating : 5.0
    }
  } catch (error) {
    console.error('获取技师信息失败:', error)
    ElMessage.error('获取技师信息失败')
  }
})
</script>

<style scoped>
.stats {
  padding: 10px 0;
}

.stat-item {
  margin-bottom: 20px;
}

.stat-item .label {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-item .value {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.rating-text {
  font-size: 14px;
  margin-left: 10px;
  color: #666;
}

.avatar-upload {
  text-align: center;
  padding: 20px 0;
}
</style>
