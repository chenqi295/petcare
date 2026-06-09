<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="220px" class="sidebar">
        <div class="logo">
          <h2>🐾 管理后台</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          class="admin-menu"
        >
          <el-menu-item index="/admin">
            <el-icon><DataAnalysis /></el-icon>
            <span>主控台</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/technicians">
            <el-icon><UserFilled /></el-icon>
            <span>服务人员管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/appointments">
            <el-icon><Calendar /></el-icon>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/orders">
            <el-icon><ShoppingCart /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/services">
            <el-icon><Service /></el-icon>
            <span>服务管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="admin-header">
          <div class="header-left">
            <span class="page-title">{{ pageTitle }}</span>
          </div>
          <div class="header-right">
            <el-button text @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-button>
          </div>
        </el-header>
        
        <!-- 内容区域 -->
        <el-main class="admin-main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import {
  DataAnalysis,
  User,
  Calendar,
  ShoppingCart,
  Service,
  UserFilled,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titles = {
    '/admin': '主控台',
    '/admin/users': '用户管理',
    '/admin/appointments': '预约管理',
    '/admin/orders': '订单管理',
    '/admin/services': '服务管理',
    '/admin/technicians': '服务人员管理'
  }
  return titles[route.path] || '管理后台'
})

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f8fafc;
}

.el-container {
  min-height: 100vh;
}

.sidebar {
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  overflow-y: auto;
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.1);
}

.logo {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h2 {
  font-size: 20px;
  margin: 0;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.admin-menu {
  border-right: none;
  background: transparent;
  padding: 10px 0;
}

:deep(.el-menu-item) {
  color: #cbd5e1;
  margin: 5px 10px;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-menu-item:hover) {
  background: rgba(102, 126, 234, 0.15) !important;
  color: #667eea !important;
  transform: translateX(5px);
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.2) 100%) !important;
  color: #667eea !important;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 8px;
}

.admin-header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.05);
  padding: 0 30px;
  height: 70px;
}

.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-right {
  display: flex;
  gap: 12px;
}

.header-right :deep(.el-button) {
  font-weight: 500;
  transition: all 0.3s;
}

.header-right :deep(.el-button:hover) {
  transform: translateY(-2px);
}

.admin-main {
  background: #f8fafc;
  padding: 25px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 180px !important;
  }
  
  .admin-header {
    padding: 0 15px;
  }
  
  .admin-main {
    padding: 15px;
  }
}
</style>
