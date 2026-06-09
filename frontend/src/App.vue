<template>
  <router-view />
</template>

<script setup>
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}

#app {
  width: 100%;
  min-height: 100vh;
}

/* Element Plus 主题定制 */
:root {
  --el-color-primary: #667eea;
  --el-color-success: #10b981;
  --el-color-warning: #f59e0b;
  --el-color-danger: #ef4444;
  --el-color-info: #3b82f6;
}

/* 卡片样式优化 */
.el-card {
  border-radius: 16px !important;
  border: none !important;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
  background: white;
}

.el-card:hover {
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.12) !important;
  transform: translateY(-2px);
}

/* 按钮样式优化 */
.el-button {
  border-radius: 10px !important;
  font-weight: 600 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.el-button--success {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
  border: none !important;
}

.el-button--success:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4);
}

.el-button--warning {
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%) !important;
  border: none !important;
}

.el-button--warning:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.4);
}

/* 输入框样式优化 */
.el-input__wrapper {
  border-radius: 10px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04) !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.el-input__wrapper:hover,
.el-input__wrapper.is-focus {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15) !important;
}

/* 表格样式优化 */
.el-table {
  border-radius: 12px !important;
  overflow: hidden !important;
}

.el-table th {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%) !important;
  font-weight: 700 !important;
  color: #1e293b;
}

.el-table tr:hover > td {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%) !important;
}

/* 标签样式优化 */
.el-tag {
  border-radius: 8px !important;
  border: none !important;
  font-weight: 600 !important;
}

/* 对话框样式优化 */
.el-dialog {
  border-radius: 20px !important;
  overflow: hidden !important;
}

/* Tabs 样式优化 */
.el-tabs__active-bar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  height: 3px !important;
}

.el-tabs__item.is-active {
  color: #667eea !important;
  font-weight: 700 !important;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.fade-in-up {
  animation: fadeInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 滚动条美化 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(135deg, #cbd5e1 0%, #94a3b8 100%);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%);
}
</style>
