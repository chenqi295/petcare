<template>
  <div class="technician-orders">
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="全部订单" name="all" />
        <el-tab-pane label="待开始" name="pending" />
        <el-tab-pane label="进行中" name="processing" />
        <el-tab-pane label="已完成" name="completed" />
      </el-tabs>
      
      <el-table :data="orders" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="serviceName" label="服务项目" min-width="150" />
        <el-table-column prop="amount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleStart(row)" 
                       v-if="row.appointmentStatus === 2">开始服务</el-button>
            <el-button size="small" type="success" @click="handleComplete(row)" 
                       v-else-if="row.appointmentStatus === 3">完成服务</el-button>
            <span v-else-if="row.appointmentStatus === 4" class="completed-text">已完成</span>
            <span v-else-if="row.appointmentStatus === 1" class="pending-text">待确认</span>
            <span v-else-if="row.status === 1 && (!row.appointmentStatus || row.appointmentStatus === 0)" class="pending-text">待支付</span>
            <span v-else-if="row.status === 0" class="cancelled-text">已取消</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders } from '@/api/technician'
import { startService, completeService } from '@/api/appointment'

const loading = ref(false)
const activeTab = ref('all')
const orders = ref([])

const formatTime = (time) => {
  if (!time) return '-'
  const dateStr = time.toString()
  const date = new Date(dateStr.includes('T') ? dateStr : dateStr.replace(' ', 'T'))
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getStatusType = (status) => {
  const types = {
    0: 'danger',    // 已取消
    1: 'warning',   // 待支付
    2: 'primary',   // 已支付
    3: 'success'    // 已完成
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    0: '已取消',
    1: '待支付',
    2: '已支付',
    3: '已完成'
  }
  return texts[status] || '-'
}

const handleStart = async (order) => {
  try {
    await ElMessageBox.confirm('确定开始此订单的服务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    
    // 通过预约ID调用开始服务API
    if (!order.appointmentId) {
      ElMessage.error('订单没有关联的预约')
      return
    }
    
    await startService(order.appointmentId)
    ElMessage.success('已开始服务')
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const handleComplete = async (order) => {
  try {
    await ElMessageBox.confirm('确定完成此订单的服务吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    })
    
    // 通过预约ID调用完成服务API
    if (!order.appointmentId) {
      ElMessage.error('订单没有关联的预约')
      return
    }
    
    await completeService(order.appointmentId)
    ElMessage.success('已完成服务')
    await loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(error.response?.data?.message || '操作失败')
    }
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrders(null) // 获取所有订单
    let allOrders = res.data || []
    
    console.log('加载的订单数据:', allOrders)
    
    // 根据Tab过滤订单
    switch (activeTab.value) {
      case 'pending':
        // 待开始：预约状态为已确认(2)
        orders.value = allOrders.filter(order => 
          order.appointmentStatus === 2
        )
        console.log('待开始订单:', orders.value)
        break
      case 'processing':
        // 进行中：预约状态为服务中(3)
        orders.value = allOrders.filter(order => 
          order.appointmentStatus === 3
        )
        break
      case 'completed':
        // 已完成：预约状态为已完成(4)
        orders.value = allOrders.filter(order => 
          order.appointmentStatus === 4
        )
        break
      default:
        // 全部订单：显示所有订单（包括待开始、进行中、已完成等）
        orders.value = allOrders.filter(order => 
          order.appointmentStatus === 2 ||  // 待开始（已确认）
          order.appointmentStatus === 3 ||  // 进行中（服务中）
          order.appointmentStatus === 4 ||  // 已完成
          order.status >= 2                 // 已支付的订单
        )
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
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
.price {
  color: #f56c6c;
  font-weight: bold;
}

.completed-text {
  color: #67c23a;
  font-weight: bold;
}

.pending-text {
  color: #e6a23c;
}

.cancelled-text {
  color: #909399;
}
</style>
