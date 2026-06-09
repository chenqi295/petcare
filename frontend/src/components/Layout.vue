<template>
  <div class="layout">
    <div class="navbar">
      <div class="logo">🐾 宠物服务平台</div>
      <!-- 只有普通用户和管理员才能看到前台导航 -->
      <el-menu 
        v-if="userStore.userInfo?.role !== 'TECHNICIAN'" 
        mode="horizontal" 
        :router="true" 
        :default-active="activeMenu"
        class="nav-menu"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/services">服务预约</el-menu-item>
        <el-menu-item index="/technicians">服务人员</el-menu-item>
        <el-menu-item index="/pets" v-if="userStore.token">我的宠物</el-menu-item>
        <el-menu-item index="/appointments" v-if="userStore.token">我的预约</el-menu-item>
        <el-menu-item index="/orders" v-if="userStore.token">我的订单</el-menu-item>
        <el-menu-item index="/reviews" v-if="userStore.token">我的评价</el-menu-item>
      </el-menu>
      
      <div class="right-menu">
        <template v-if="userStore.token">
          <el-button v-if="userStore.userInfo?.role === 'ADMIN'" text @click="router.push('/admin')">
            <el-icon><Setting /></el-icon>
            管理后台
          </el-button>
          <el-button v-if="userStore.userInfo?.role === 'TECHNICIAN'" text @click="router.push('/technician')">
            <el-icon><Tools /></el-icon>
            技师工作台
          </el-button>
          <!-- 普通用户显示个人中心，技师和管理员不显示 -->
          <el-button v-if="userStore.userInfo?.role === 'USER'" text @click="router.push('/profile')">
            <el-icon><User /></el-icon>
            {{ userStore.userInfo?.username || '个人中心' }}
          </el-button>
          <el-button type="danger" text @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出
          </el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="router.push('/login')">登录</el-button>
          <el-button @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
    
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { User, SwitchButton, Setting, Tools } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 计算当前激活的菜单项
const activeMenu = computed(() => {
  return route.path
})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/')
  }).catch(() => {})
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  padding: 0 30px;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.98) !important;
  backdrop-filter: blur(10px);
  height: 70px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-right: 30px;
  flex-shrink: 0;
  cursor: pointer;
  transition: transform 0.3s;
}

.logo:hover {
  transform: scale(1.05);
}

.nav-menu {
  flex: 1;
  border: none !important;
  background: transparent !important;
}

.nav-menu :deep(.el-menu-item) {
  font-weight: 500;
  color: #475569 !important;
  transition: all 0.3s;
  position: relative;
}

.nav-menu :deep(.el-menu-item:hover) {
  color: #667eea !important;
  background: rgba(102, 126, 234, 0.08) !important;
}

.nav-menu :deep(.el-menu-item.is-active) {
  color: #667eea !important;
  background: rgba(102, 126, 234, 0.1) !important;
}



.right-menu {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  margin-left: 20px;
}

.right-menu :deep(.el-button) {
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.right-menu :deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
}

.right-menu :deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(102, 126, 234, 0.4);
}

.main-content {
  min-height: calc(100vh - 70px);
  background: #f8fafc;
  margin-top: 70px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 15px;
    height: 60px;
  }
  
  .logo {
    font-size: 18px;
    margin-right: 15px;
  }
  
  .nav-menu {
    display: none;
  }
  
  .main-content {
    margin-top: 60px;
  }
}
</style>
