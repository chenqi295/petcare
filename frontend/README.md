# PetCare 前端项目

基于 Vue 3 + Vite + Element Plus 的宠物服务预约平台前端应用。

## 技术栈

- Vue 3 - 渐进式 JavaScript 框架
- Vite - 下一代前端构建工具
- Element Plus - 基于 Vue 3 的组件库（中文）
- Pinia - Vue 状态管理
- Vue Router - 官方路由管理器
- Axios - HTTP 客户端

## 快速开始

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

## 项目结构

```
frontend/
├── src/
│   ├── api/          # API接口
│   ├── components/   # 公共组件
│   ├── router/       # 路由配置
│   ├── stores/       # 状态管理
│   ├── utils/        # 工具函数
│   ├── views/        # 页面组件
│   ├── App.vue       # 根组件
│   └── main.js       # 入口文件
├── index.html
├── package.json
└── vite.config.js
```

## 功能模块

### 已完成 ✅
- [x] 用户登录/注册
- [x] 首页展示
- [x] 导航栏组件
- [x] 路由配置
- [x] 状态管理
- [x] API请求封装

### 待完善 ⏳
以下页面需要您根据实际需求进一步完善：

1. **Register.vue** - 注册页面（参考Login.vue）
2. **Services.vue** - 服务列表和预约页面
3. **Pets.vue** - 宠物管理页面
4. **Appointments.vue** - 预约管理页面
5. **Orders.vue** - 订单管理页面
6. **Profile.vue** - 个人中心页面

## 创建示例页面

### 注册页面 (Register.vue)

```vue
<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="title">用户注册</h2>
      <el-form :model="form" :rules="rules" ref="registerFormRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" style="width: 100%">注册</el-button>
        </el-form-item>
        <div class="links">
          <router-link to="/login">已有账号？立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()
const registerFormRef = ref(null)

const form = reactive({
  username: '',
  password: '',
  phone: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    await register(form)
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-card { width: 400px; padding: 20px; }
.title { text-align: center; margin-bottom: 30px; }
.links { text-align: center; margin-top: 10px; }
.links a { color: #409EFF; text-decoration: none; }
</style>
```

## API代理配置

在 `vite.config.js` 中已配置API代理：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

确保后端服务运行在 8080 端口。

## 注意事项

1. **中文界面**: 所有页面均使用中文
2. **Element Plus**: 已配置中文语言包
3. **响应式设计**: 支持移动端和PC端
4. **路由守卫**: 未登录自动跳转登录页

## 下一步

1. 安装依赖：`npm install`
2. 创建缺失的页面组件
3. 实现具体的业务逻辑
4. 测试前后端联调

---

祝您开发愉快！🐾
