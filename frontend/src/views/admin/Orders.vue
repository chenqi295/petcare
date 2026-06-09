<template>
  <div class="orders-page">
    <el-card>
      <el-table :data="orders" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="serviceName" label="服务项目" min-width="150" />
        <el-table-column prop="amount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 1 ? 'success' : 'info'">
              {{ row.payStatus === 1 ? '已支付' : '未支付' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.status)">
              {{ getOrderStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-empty v-if="!loading && orders.length === 0" description="暂无订单数据" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllOrders } from '@/api/order'

const loading = ref(false)
const orders = ref([])

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getAllOrders()
    orders.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const getOrderStatusText = (status) => {
  const map = {
    0: '已取消',
    1: '待支付',
    2: '已支付',
    3: '已完成'
  }
  return map[status] || '未知'
}

const getOrderStatusType = (status) => {
  const map = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'success'
  }
  return map[status] || ''
}
</script>

<style scoped>
.price {
  color: #f56c6c;
  font-weight: bold;
}
</style>
