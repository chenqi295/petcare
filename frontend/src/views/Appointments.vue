<template>
  <div class="appointments-page">
    <div class="container">
      <h2 class="page-title">我的预约</h2>
      
      <!-- 预约列表 - 桌面端表格 -->
      <el-table :data="appointments" v-loading="loading" style="width: 100%" class="desktop-table">
        <el-table-column prop="appointmentNo" label="预约编号" width="180" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              size="small"
              type="danger"
              @click="handleCancel(row)"
            >
              取消预约
            </el-button>
            <el-button
              v-else-if="row.status >= 2 && row.status <= 4"
              size="small"
              disabled
            >
              {{ getActionText(row.status) }}
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 预约列表 - 移动端卡片 -->
      <div class="mobile-appointments" v-if="!loading">
        <div v-for="appointment in appointments" :key="appointment.id" class="appointment-card">
          <div class="appointment-header">
            <span class="appointment-no">{{ appointment.appointmentNo }}</span>
            <el-tag :type="getStatusType(appointment.status)" size="small">
              {{ getStatusText(appointment.status) }}
            </el-tag>
          </div>
          
          <div class="appointment-body">
            <div class="appointment-info-item">
              <span class="info-label">预约时间</span>
              <span class="info-value">{{ formatTime(appointment.appointmentTime) }}</span>
            </div>
            <div class="appointment-info-item" v-if="appointment.remark">
              <span class="info-label">备注</span>
              <span class="info-value">{{ appointment.remark }}</span>
            </div>
          </div>
          
          <div class="appointment-footer">
            <el-button
              v-if="appointment.status === 1"
              type="danger"
              @click="handleCancel(appointment)"
            >
              取消预约
            </el-button>
            <el-button
              v-else-if="appointment.status >= 2 && appointment.status <= 4"
              disabled
            >
              {{ getActionText(appointment.status) }}
            </el-button>
            <span v-else style="color: #909399;">-</span>
          </div>
        </div>
      </div>
      
      <el-empty v-if="!loading && appointments.length === 0" description="暂无预约记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyAppointments, cancelAppointment } from '@/api/appointment'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const appointments = ref([])

onMounted(() => {
  loadAppointments()
})

const loadAppointments = async () => {
  if (!userStore.token) {
    appointments.value = []
    return
  }
  loading.value = true
  try {
    const res = await getMyAppointments()
    appointments.value = res.data || []
  } catch (error) {
    console.error('加载预约列表失败:', error)
    // 静默失败，不影响页面展示
    appointments.value = []
  } finally {
    loading.value = false
  }
}

const handleCancel = async (appointment) => {
  console.log('准备取消预约:', appointment)
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    console.log('取消预约ID:', appointment.id, '状态:', appointment.status)
    await cancelAppointment(appointment.id, '用户取消')
    ElMessage.success('取消成功')
    loadAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消预约失败:', error)
      ElMessage.error(error.response?.data?.message || error.message || '取消失败')
    }
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const getStatusText = (status) => {
  const map = {
    0: '已取消',
    1: '待确认',
    2: '已确认',
    3: '服务中',
    4: '已完成',
    5: '已过期'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'success',
    5: 'danger'
  }
  return map[status] || ''
}

const getActionText = (status) => {
  const map = {
    2: '已确认',
    3: '服务中',
    4: '已完成'
  }
  return map[status] || ''
}
</script>

<style scoped>
.appointments-page {
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

/* 响应式设计 - 移动端卡片布局 */
@media (max-width: 768px) {
  .container {
    padding: 20px 15px;
  }
  
  .page-title {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  /* 隐藏桌面端表格 */
  :deep(.el-table) {
    display: none;
  }
  
  /* 移动端预约卡片 */
  .appointment-card {
    background: white;
    border-radius: 12px;
    padding: 15px;
    margin-bottom: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
  
  .appointment-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .appointment-no {
    font-size: 14px;
    color: #606266;
    word-break: break-all;
  }
  
  .appointment-body {
    margin-bottom: 12px;
  }
  
  .appointment-info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    font-size: 14px;
  }
  
  .appointment-info-item:last-child {
    margin-bottom: 0;
  }
  
  .info-label {
    color: #909399;
  }
  
  .info-value {
    color: #303133;
    font-weight: 500;
  }
  
  .appointment-footer {
    padding-top: 12px;
    border-top: 1px solid #ebeef5;
  }
  
  .appointment-footer .el-button {
    width: 100%;
    height: 40px;
    font-size: 14px;
  }
}
</style>
