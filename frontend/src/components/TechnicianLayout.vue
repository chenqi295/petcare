<template>
  <div class="technician-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="220px" class="sidebar">
        <div class="logo">
          <h2>🔧 技师工作台</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          class="technician-menu"
        >
          <el-menu-item index="/technician">
            <el-icon><DataAnalysis /></el-icon>
            <span>工作台</span>
          </el-menu-item>
          <el-menu-item index="/technician/orders">
            <el-icon><ShoppingCart /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/technician/appointments">
            <el-icon><Calendar /></el-icon>
            <span>预约处理</span>
          </el-menu-item>
          <el-menu-item index="/technician/history">
            <el-icon><Document /></el-icon>
            <span>服务记录</span>
          </el-menu-item>
          <el-menu-item index="/technician/reviews">
            <el-icon><Star /></el-icon>
            <span>客户评价</span>
          </el-menu-item>
          <el-menu-item index="/technician/profile">
            <el-icon><User /></el-icon>
            <span>个人资料</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="technician-header">
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
        <el-main class="technician-main">
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
  ShoppingCart,
  Calendar,
  Document,
  Star,
  User,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  const titles = {
    '/technician': '工作台',
    '/technician/orders': '我的订单',
    '/technician/appointments': '预约处理',
    '/technician/history': '服务记录',
    '/technician/reviews': '客户评价',
    '/technician/profile': '个人资料'
  }
  return titles[route.path] || '技师工作台'
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
.technician-layout {
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
  background: rgba(0, 0, 0, 0.15);
  color: white;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo h2 {
  font-size: 20px;
  margin: 0;
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.technician-menu {
  border-right: none;
  background: transparent;
  padding: 10px 0;
}

:deep(.el-menu-item) {
  color: #ecfdf5;
  margin: 5px 10px;
  border-radius: 10px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.15) !important;
  color: #ffffff !important;
  transform: translateX(5px);
}

:deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #ffffff !important;
  box-shadow: 0 4px 15px rgba(255, 255, 255, 0.15);
  font-weight: 600;
}

:deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 8px;
}

.technician-header {
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

.technician-main {
  background: #f8fafc;
  padding: 25px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 180px !important;
  }
  
  .technician-header {
    padding: 0 15px;
  }
  
  .technician-main {
    padding: 15px;
  }
}
</style>
