<template>
  <div class="profile-page">
    <div class="container">
      <h2 class="page-title">个人中心</h2>
      
      <el-row :gutter="20">
        <!-- 用户信息 -->
        <el-col :span="8">
          <el-card>
            <div class="user-info">
              <div class="avatar-container">
                <el-avatar :size="80" :src="userInfo.avatar" />
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
              <h3>{{ userInfo.username }}</h3>
              <p>{{ userInfo.realName || '未设置姓名' }}</p>
              <p>{{ userInfo.phone || '未设置手机' }}</p>
              <p>{{ userInfo.email || '未设置邮箱' }}</p>
            </div>
          </el-card>
        </el-col>
        
        <!-- 编辑表单 -->
        <el-col :span="16">
          <el-card>
            <h3 class="card-title">基本信息</h3>
            <el-form :model="form" label-width="100px">
              <el-form-item label="真实姓名">
                <el-input v-model="form.realName" placeholder="请输入真实姓名" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="form.phone" placeholder="请输入手机号" disabled />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="form.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdate" :loading="loading">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
          
          <el-card style="margin-top: 20px">
            <h3 class="card-title">修改密码</h3>
            <el-form :model="passwordForm" label-width="100px">
              <el-form-item label="原密码">
                <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
              </el-form-item>
              <el-form-item label="新密码">
                <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
              </el-form-item>
              <el-form-item label="确认密码">
                <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleChangePassword" :loading="passwordLoading">
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { getUserInfo, updateUserInfo, changePassword, uploadAvatar } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const passwordLoading = ref(false)

const userInfo = ref({})
const form = reactive({
  realName: '',
  phone: '',
  email: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = async () => {
  if (!userStore.token) {
    return
  }
  try {
    const res = await getUserInfo()
    userInfo.value = res.data
    Object.assign(form, res.data)
    // 拼接完整的头像URL
    if (res.data.avatar) {
      userInfo.value.avatar = 'http://localhost:8080/api' + res.data.avatar
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    // 静默失败
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

  const uploadLoading = ElLoading.service({
    lock: true,
    text: '上传中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    const res = await uploadAvatar(formData)
    if (res.code === 200) {
      // 拼接完整的图片URL
      const avatarUrl = 'http://localhost:8080/api' + res.data.url
      userInfo.value.avatar = avatarUrl
      
      // 更新用户信息中的头像字段
      form.avatar = res.data.url
      await updateUserInfo(form)
      
      ElMessage.success('头像上传成功')
      loadUserInfo()
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploadLoading.close()
  }
}

const handleUpdate = async () => {
  loading.value = true
  try {
    await updateUserInfo(form)
    ElMessage.success('更新成功')
    loadUserInfo()
    userStore.setUserInfo(form)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleChangePassword = async () => {
  if (!passwordForm.oldPassword) {
    ElMessage.warning('请输入原密码')
    return
  }
  if (!passwordForm.newPassword) {
    ElMessage.warning('请输入新密码')
    return
  }
  
  // 验证新密码格式
  if (passwordForm.newPassword.length < 6 || passwordForm.newPassword.length > 20) {
    ElMessage.warning('密码长度必须在6-20位之间')
    return
  }
  if (!/[a-zA-Z]/.test(passwordForm.newPassword)) {
    ElMessage.warning('密码必须包含至少一个字母')
    return
  }
  if (!/[0-9]/.test(passwordForm.newPassword)) {
    ElMessage.warning('密码必须包含至少一个数字')
    return
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  passwordLoading.value = true
  try {
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    userStore.logout()
    setTimeout(() => {
      window.location.href = '/login'
    }, 1500)
  } catch (error) {
    console.error(error)
  } finally {
    passwordLoading.value = false
  }
}
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.page-title {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
}

.user-info {
  text-align: center;
}

.avatar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar-uploader {
  margin-top: 10px;
}

.user-info h3 {
  margin: 15px 0 10px;
  color: #333;
}

.user-info p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.card-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
}
</style>
