<template>
  <div class="login-container">
    <!-- 背景图片 -->
    <div class="background-image"></div>
    
    <!-- 动态背景装饰 -->
    <div class="background-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>
    
    <el-card class="login-card fade-in-up">
      <div class="card-header">
        <div class="logo-icon">🐾</div>
        <h2 class="title">宠物服务预约平台</h2>
        <p class="subtitle">欢迎回来，请登录您的账号</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名或手机号" 
            prefix-icon="User"
            size="large"
            @blur="trimUsername"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码" 
            prefix-icon="Lock" 
            show-password
            size="large"
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleLogin" 
            :loading="loading" 
            size="large"
            class="login-button"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
        
        <div class="links">
          <router-link to="/register">还没有账号？立即注册</router-link>
          <span class="divider">|</span>
          <router-link to="/forgot-password">忘记密码？</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, getUserInfo } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 16, message: '用户名长度在2-16位之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含字母、数字、下划线和中文', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' }
  ]
}

// 页面加载时，尝试从 localStorage 读取保存的账号密码
const loadSavedCredentials = () => {
  try {
    const savedUsername = localStorage.getItem('remembered_username')
    const savedPassword = localStorage.getItem('remembered_password')
    
    if (savedUsername && savedPassword) {
      form.username = savedUsername
      form.password = savedPassword
      rememberMe.value = true
    }
  } catch (error) {
    console.error('读取保存的账号密码失败:', error)
  }
}

// 保存账号密码到 localStorage
const saveCredentials = () => {
  try {
    if (rememberMe.value) {
      localStorage.setItem('remembered_username', form.username)
      localStorage.setItem('remembered_password', form.password)
    } else {
      localStorage.removeItem('remembered_username')
      localStorage.removeItem('remembered_password')
    }
  } catch (error) {
    console.error('保存账号密码失败:', error)
  }
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const res = await login(form)
    
    // 计算Token过期时间（当前时间 + 24小时）
    const expireTime = Date.now() + 24 * 60 * 60 * 1000
    userStore.setToken(res.data.token, expireTime)
    userStore.setUserInfo(res.data.userInfo)
    ElMessage.success('登录成功')
    
    // 根据是否勾选记住密码来保存或删除账号信息
    saveCredentials()
    
    // 根据角色跳转到不同页面
    const role = res.data.userInfo?.role
    console.log('用户角色:', role) // 调试信息
    console.log('用户信息:', res.data.userInfo) // 调试信息
    console.log('Token过期时间:', new Date(expireTime).toLocaleString()) // 调试信息
    
    if (role === 'ADMIN') {
      router.push('/admin')
    } else if (role === 'TECHNICIAN') {
      router.push('/technician')
    } else {
      router.push('/')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载保存的账号密码
loadSavedCredentials()

// 去除用户名/手机号前后空格
const trimUsername = () => {
  if (form.username) {
    form.username = form.username.trim()
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  overflow: hidden;
}

/* 背景图片 */
.background-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('/puppies-background.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 0;
  animation: slowZoom 30s ease-in-out infinite;
  filter: brightness(1.1) contrast(1.05);
}

/* 缓慢缩放动画 */
@keyframes slowZoom {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

/* 动态背景装饰 */
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
  filter: blur(80px);
  opacity: 0.3;
  animation: float 20s infinite ease-in-out;
}

.shape-1 {
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.5);
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.shape-2 {
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.4);
  bottom: -50px;
  right: -50px;
  animation-delay: 5s;
}

.shape-3 {
  width: 250px;
  height: 250px;
  background: rgba(255, 255, 255, 0.3);
  top: 50%;
  left: 50%;
  animation-delay: 10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -50px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

.login-card {
  position: relative;
  width: 440px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.6) !important;
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3) !important;
  z-index: 2;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 60px;
  margin-bottom: 15px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.title {
  color: #1e293b;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 0;
}

.login-form {
  margin-top: 25px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.login-form :deep(.el-checkbox) {
  color: #64748b;
  font-size: 14px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 12px 15px;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  margin-top: 10px;
}

.links {
  text-align: center;
  margin-top: 20px;
}

.links a {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
  font-weight: 500;
}

.links a:hover {
  color: #764ba2;
  text-decoration: underline;
}

.links .divider {
  margin: 0 10px;
  color: #cbd5e1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    padding: 30px 20px;
  }
  
  .title {
    font-size: 24px;
  }
}
</style>
