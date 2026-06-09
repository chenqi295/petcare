<template>
  <div class="appointments-page">
    <el-card>
      <el-table :data="appointments" v-loading="loading">
        <el-table-column prop="appointmentNo" label="预约编号" width="180" />
        <el-table-column prop="serviceName" label="服务项目" min-width="150" />
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
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              size="small"
              type="success"
              @click="handleConfirm(row.id)"
            >
              确认
            </el-button>
            <el-button
              v-if="row.status === 1"
              size="small"
              type="danger"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-empty v-if="!loading && appointments.length === 0" description="暂无预约数据" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllAppointments, confirmAppointment, cancelAppointment } from '@/api/appointment'

const loading = ref(false)
const appointments = ref([])

onMounted(() => {
  loadAppointments()
})

const loadAppointments = async () => {
  loading.value = true
  try {
    const res = await getAllAppointments()
    appointments.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleConfirm = async (id) => {
  try {
    await confirmAppointment(id)
    ElMessage.success('确认成功')
    loadAppointments()
  } catch (error) {
    console.error(error)
  }
}

const handleCancel = async (appointment) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelAppointment(appointment.id, '管理员取消')
    ElMessage.success('取消成功')
    loadAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
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
</script>
