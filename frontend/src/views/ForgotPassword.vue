<template>
  <div class="forgot-password-container">
    <!-- 背景图片 -->
    <div class="background-image"></div>
    
    <!-- 动态背景装饰 -->
    <div class="background-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>
    
    <el-card class="forgot-password-card fade-in-up">
      <div class="card-header">
        <div class="logo-icon">🐾</div>
        <h2 class="title">忘记密码</h2>
        <p class="subtitle">通过手机号重置您的密码</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="resetFormRef" class="reset-form">
        <el-form-item prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入注册手机号" 
            prefix-icon="Phone"
            size="large"
            @blur="trimPhone"
          />
        </el-form-item>
        
        <el-form-item prop="newPassword">
          <el-input 
            v-model="form.newPassword" 
            type="password" 
            placeholder="请输入新密码" 
            prefix-icon="Lock" 
            show-password
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码" 
            prefix-icon="Lock" 
            show-password
            size="large"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleReset" 
            :loading="loading"
            size="large"
            class="reset-btn"
          >
            重置密码
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <div class="back-to-login">
            <el-link type="primary" @click="goToLogin">返回登录</el-link>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { resetPassword } from '@/api/user'

const router = useRouter()
const resetFormRef = ref(null)
const loading = ref(false)

const form = reactive({
  phone: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 验证密码格式
const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码长度必须在6-20位之间'))
  } else if (!/[a-zA-Z]/.test(value)) {
    callback(new Error('密码必须包含至少一个字母'))
  } else if (!/[0-9]/.test(value)) {
    callback(new Error('密码必须包含至少一个数字'))
  } else {
    callback()
  }
}

// 验证手机号格式
const validatePhone = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^\d+$/.test(value)) {
    callback(new Error('手机号只能包含数字'))
  } else if (value.length !== 11) {
    callback(new Error('手机号必须是11位数字'))
  } else {
    callback()
  }
}

const rules = {
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  newPassword: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

// 监听密码变化，重新验证确认密码
watch(() => form.newPassword, () => {
  if (form.confirmPassword) {
    resetFormRef.value?.validateField('confirmPassword')
  }
})

const handleReset = async () => {
  const valid = await resetFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await resetPassword({
      phone: form.phone,
      newPassword: form.newPassword
    })
    ElMessage.success('密码重置成功')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}

// 去除手机号前后空格
const trimPhone = () => {
  if (form.phone) {
    form.phone = form.phone.trim()
  }
}
</script>

<style scoped>
.forgot-password-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('/puppies-background.jpg');
  background-size: cover;
  background-position: center;
  opacity: 0.3;
  z-index: 0;
}

.background-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.forgot-password-card {
  width: 450px;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  z-index: 2;
  padding: 40px;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.title {
  font-size: 28px;
  color: #333;
  margin: 10px 0;
  font-weight: 600;
}

.subtitle {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.reset-form {
  margin-top: 20px;
}

.reset-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.back-to-login {
  text-align: center;
  width: 100%;
}

.fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}
</style>
