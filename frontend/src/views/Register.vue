<template>
  <div class="register-container">
    <!-- 背景图片 -->
    <div class="background-image"></div>
    
    <!-- 动态背景装饰 -->
    <div class="background-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
    </div>
    
    <el-card class="register-card fade-in-up">
      <div class="card-header">
        <div class="logo-icon">🐾</div>
        <h2 class="title">用户注册</h2>
        <p class="subtitle">创建您的账号，开启宠物服务之旅</p>
      </div>
      
      <el-form :model="form" :rules="rules" ref="registerFormRef" class="register-form">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名" 
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
        
        <el-form-item prop="confirmPassword">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            prefix-icon="Lock" 
            show-password
            size="large"
          />
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input 
            v-model="form.phone" 
            placeholder="请输入手机号" 
            prefix-icon="Phone"
            size="large"
            @blur="trimPhone"
          />
        </el-form-item>
        
        <el-form-item prop="role">
          <el-select 
            v-model="form.role" 
            placeholder="请选择注册类型" 
            size="large"
            style="width: 100%"
            :teleported="false"
          >
            <el-option label="普通用户" value="USER">
              <div class="role-option">
                <span class="role-icon">👤</span>
                <div class="role-info">
                  <div class="role-title">普通用户</div>
                  <div class="role-desc">预约服务</div>
                </div>
              </div>
            </el-option>
            <el-option label="服务人员" value="TECHNICIAN">
              <div class="role-option">
                <span class="role-icon">🔧</span>
                <div class="role-info">
                  <div class="role-title">服务人员</div>
                  <div class="role-desc">提供服务</div>
                </div>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        
        <!-- 只有服务人员需要填写真实姓名 -->
        <el-form-item v-if="form.role === 'TECHNICIAN'" prop="realName">
          <el-input 
            v-model="form.realName" 
            placeholder="请输入真实姓名（用于客户查看）" 
            prefix-icon="Avatar"
            size="large"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister" 
            :loading="loading" 
            size="large"
            class="register-button"
          >
            {{ loading ? '注册中...' : '注 册' }}
          </el-button>
        </el-form-item>
        
        <div class="links">
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  realName: '',
  role: 'USER' // 默认普通用户
})

// 动态验证规则：只有服务人员需要填写真实姓名
const validateRealName = (rule, value, callback) => {
  if (form.role === 'TECHNICIAN' && !value) {
    callback(new Error('请输入真实姓名'))
  } else {
    callback()
  }
}

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
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
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 16, message: '用户名长度在2-16位之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/, message: '用户名只能包含字母、数字、下划线和中文', trigger: 'blur' }
  ],
  password: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  role: [{ required: true, message: '请选择注册类型', trigger: 'change' }],
  realName: [{ validator: validateRealName, trigger: 'blur' }]
}

// 监听密码变化，重新验证确认密码
watch(() => form.password, () => {
  if (form.confirmPassword) {
    registerFormRef.value?.validateField('confirmPassword')
  }
})

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 去除用户名前后空格
const trimUsername = () => {
  if (form.username) {
    form.username = form.username.trim()
  }
}

// 去除手机号前后空格
const trimPhone = () => {
  if (form.phone) {
    form.phone = form.phone.trim()
  }
}
</script>

<style scoped>
.register-container {
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

.register-card {
  position: relative;
  width: 480px;
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

.register-form {
  margin-top: 25px;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.register-form :deep(.el-input__wrapper) {
  padding: 12px 15px;
}

.role-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  width: 100%;
}

.role-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.role-info {
  flex: 1;
  min-width: 0;
}

.role-title {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
  line-height: 1.4;
}

.role-desc {
  color: #64748b;
  font-size: 12px;
  margin-top: 2px;
  line-height: 1.4;
}

/* 下拉框选项样式优化 */
.register-form :deep(.el-select-dropdown__item) {
  height: auto !important;
  line-height: normal !important;
  padding: 10px 20px !important;
}

.register-form :deep(.el-select-dropdown) {
  border-radius: 12px !important;
  overflow: hidden !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12) !important;
}

.register-button {
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

/* 响应式设计 */
@media (max-width: 768px) {
  .register-card {
    width: 90%;
    max-width: 400px;
    padding: 30px 25px;
  }
  
  .logo-icon {
    font-size: 50px;
    margin-bottom: 12px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .subtitle {
    font-size: 13px;
  }
  
  .card-header {
    margin-bottom: 25px;
  }
  
  .register-form :deep(.el-form-item) {
    margin-bottom: 18px;
  }
  
  .register-form :deep(.el-input__wrapper) {
    padding: 10px 12px;
  }
  
  /* 下拉框选项优化 */
  .role-option {
    gap: 10px;
  }
  
  .role-icon {
    font-size: 20px;
  }
  
  .role-title {
    font-size: 13px;
  }
  
  .role-desc {
    font-size: 11px;
  }
  
  .register-button {
    height: 44px;
    font-size: 15px;
  }
  
  .links {
    margin-top: 15px;
  }
  
  .links a {
    font-size: 13px;
  }
  
  /* 隐藏背景装饰以提升性能 */
  .shape-1,
  .shape-3 {
    display: none;
  }
  
  .shape-2 {
    width: 200px;
    height: 200px;
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) {
  .register-card {
    width: 95%;
    padding: 25px 20px;
  }
  
  .title {
    font-size: 20px;
  }
  
  .logo-icon {
    font-size: 45px;
  }
}
</style>
