<template>
  <div class="technician-appointments">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部预约" name="all" />
        <el-tab-pane label="待确认" name="pending" />
        <el-tab-pane label="已确认" name="confirmed" />
        <el-tab-pane label="已完成" name="completed" />
      </el-tabs>
      
      <el-table :data="appointments" v-loading="loading">
        <el-table-column prop="appointmentNo" label="预约编号" width="180" />
        <el-table-column prop="petBreed" label="宠物品种" width="120" />
        <el-table-column prop="serviceName" label="服务项目" min-width="150" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" size="small" type="primary" @click="handleAccept(row)">确认</el-button>
            <el-button v-if="row.status === 3" size="small" type="primary" @click="handleCompleteService(row)">完成服务</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAppointments } from '@/api/technician'
import { confirmAppointment, rejectAppointment, startService, completeService } from '@/api/appointment'

const loading = ref(false)
const activeTab = ref('all')
const appointments = ref([])

const formatTime = (time) => {
  if (!time) return '-'
  const dateStr = time.toString()
  const date = new Date(dateStr.includes('T') ? dateStr : dateStr.replace(' ', 'T'))
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusType = (status) => {
  const types = {
    0: 'danger',   // 已取消
    1: 'warning',  // 待确认
    2: 'success',  // 已确认
    3: 'primary',  // 服务中
    4: 'info',     // 已完成
    5: 'danger'    // 已过期
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    0: '已取消',
    1: '待确认',
    2: '已确认',
    3: '服务中',
    4: '已完成',
    5: '已过期'
  }
  return texts[status] || '-'
}

const handleAccept = async (appointment) => {
  ElMessageBox.confirm('确定确认此预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await confirmAppointment(appointment.id)
      ElMessage.success('已确认预约')
      await loadData()
    } catch (error) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }).catch(() => {})
}



const handleStartService = async (appointment) => {
  ElMessageBox.confirm('确定开始此服务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await startService(appointment.id)
      ElMessage.success('已开始服务')
      await loadData()
    } catch (error) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }).catch(() => {})
}

const handleCompleteService = async (appointment) => {
  ElMessageBox.confirm('确定完成此服务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await completeService(appointment.id)
      ElMessage.success('服务已完成')
      await loadData()
    } catch (error) {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }).catch(() => {})
}

const loadData = async () => {
  loading.value = true
  try {
    // 对于已确认和已完成标签，获取所有数据后在客户端过滤
    const shouldFilterClientSide = activeTab.value === 'confirmed' || activeTab.value === 'completed'
    
    const statusMap = {
      'all': null,
      'pending': 1,      // 待确认
      'confirmed': null, // 已确认标签获取全部，客户端过滤状态2、3
      'completed': null  // 已完成标签获取全部，客户端过滤状态4
    }
    const status = statusMap[activeTab.value]
    console.log('请求预约数据 - 当前tab:', activeTab.value, '状态筛选:', status)
    const res = await getAppointments(status)
    console.log('返回的预约数据:', res.data)
    
    let allAppointments = res.data || []
    
    // 如果是已确认标签，在客户端过滤状态为2、3的数据
    if (shouldFilterClientSide) {
      if (activeTab.value === 'confirmed') {
        appointments.value = allAppointments.filter(a => [2, 3].includes(a.status))
      } else if (activeTab.value === 'completed') {
        appointments.value = allAppointments.filter(a => a.status === 4)
      }
    } else {
      appointments.value = allAppointments
    }
    
    console.log('显示的预约数量:', appointments.value.length)
  } catch (error) {
    console.error('加载预约失败:', error)
    ElMessage.error('加载预约失败')
  } finally {
    loading.value = false
  }
}

// 监听tab切换
watch(activeTab, () => {
  loadData()
})

onMounted(() => {
  loadData()
})
</script>

<style scoped>
</style>
