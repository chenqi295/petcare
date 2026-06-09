<template>
  <div class="layout">
    <div class="navbar">
      <!-- 移动端汉堡菜单按钮 -->
      <el-button 
        v-if="userStore.userInfo?.role !== 'TECHNICIAN'"
        class="hamburger-btn" 
        text 
        @click="mobileMenuVisible = true"
      >
        <el-icon :size="24"><Fold /></el-icon>
      </el-button>
      
      <div class="logo">🐾 宠物服务平台</div>
      
      <!-- 桌面端导航菜单 -->
      <el-menu 
        v-if="userStore.userInfo?.role !== 'TECHNICIAN'" 
        mode="horizontal" 
        :router="true" 
        :default-active="activeMenu"
        class="nav-menu desktop-menu"
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
            <span class="btn-text">管理后台</span>
          </el-button>
          <el-button v-if="userStore.userInfo?.role === 'TECHNICIAN'" text @click="router.push('/technician')">
            <el-icon><Tools /></el-icon>
            <span class="btn-text">技师工作台</span>
          </el-button>
          <!-- 普通用户显示个人中心，技师和管理员不显示 -->
          <el-button v-if="userStore.userInfo?.role === 'USER'" text @click="router.push('/profile')">
            <el-icon><User /></el-icon>
            <span class="btn-text">{{ userStore.userInfo?.username || '个人中心' }}</span>
          </el-button>
          <el-button type="danger" text @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span class="btn-text">退出</span>
          </el-button>
        </template>
        <template v-else>
          <el-button type="primary" @click="router.push('/login')">登录</el-button>
          <el-button @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
    
    <!-- 移动端抽屉菜单 -->
    <el-drawer
      v-model="mobileMenuVisible"
      direction="ltr"
      size="280px"
      :with-header="false"
      class="mobile-drawer"
    >
      <div class="drawer-content">
        <div class="drawer-header">
          <div class="drawer-logo">🐾 宠物服务平台</div>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="drawer-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/services">
            <el-icon><Service /></el-icon>
            <span>服务预约</span>
          </el-menu-item>
          <el-menu-item index="/technicians">
            <el-icon><UserFilled /></el-icon>
            <span>服务人员</span>
          </el-menu-item>
          <el-menu-item index="/pets" v-if="userStore.token">
            <el-icon><UserFilled /></el-icon>
            <span>我的宠物</span>
          </el-menu-item>
          <el-menu-item index="/appointments" v-if="userStore.token">
            <el-icon><Calendar /></el-icon>
            <span>我的预约</span>
          </el-menu-item>
          <el-menu-item index="/orders" v-if="userStore.token">
            <el-icon><List /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/reviews" v-if="userStore.token">
            <el-icon><Star /></el-icon>
            <span>我的评价</span>
          </el-menu-item>
        </el-menu>
      </div>
    </el-drawer>
    
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { User, SwitchButton, Setting, Tools, Fold, HomeFilled, Service, UserFilled, Calendar, List, Star } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()

// 移动端菜单显示状态
const mobileMenuVisible = ref(false)

// 计算当前激活的菜单项
const activeMenu = computed(() => {
  return route.path
})

// 处理移动端菜单选择
const handleMenuSelect = (index) => {
  router.push(index)
  mobileMenuVisible.value = false
}

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

/* 汉堡菜单按钮 */
.hamburger-btn {
  display: none;
  margin-right: 15px;
  padding: 8px;
}

.hamburger-btn :deep(.el-icon) {
  color: #667eea;
}

.nav-menu {
  flex: 1;
  border: none !important;
  background: transparent !important;
}

.desktop-menu {
  display: flex;
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

.btn-text {
  display: inline;
}

.main-content {
  min-height: calc(100vh - 70px);
  background: #f8fafc;
  margin-top: 70px;
}

/* 移动端抽屉样式 */
.drawer-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.drawer-header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.drawer-logo {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.drawer-menu {
  flex: 1;
  border: none;
}

.drawer-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  font-size: 15px;
}

.drawer-menu :deep(.el-menu-item .el-icon) {
  margin-right: 12px;
  font-size: 18px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 15px;
    height: 60px;
  }
  
  .logo {
    font-size: 18px;
    margin-right: 10px;
  }
  
  /* 显示汉堡菜单按钮 */
  .hamburger-btn {
    display: flex;
  }
  
  /* 隐藏桌面端菜单 */
  .desktop-menu {
    display: none !important;
  }
  
  /* 隐藏按钮文字，只显示图标 */
  .btn-text {
    display: none;
  }
  
  .right-menu {
    gap: 8px;
  }
  
  .right-menu :deep(.el-button) {
    padding: 8px;
  }
  
  .main-content {
    margin-top: 60px;
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) {
  .logo {
    font-size: 16px;
  }
  
  .right-menu {
    gap: 5px;
  }
}
</style>
