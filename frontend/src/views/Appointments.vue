<template>
  <div class="appointments-page">
    <div class="container">
      <h2 class="page-title">我的预约</h2>
      
      <!-- 预约列表 -->
      <el-table :data="appointments" v-loading="loading" style="width: 100%">
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
</style>
