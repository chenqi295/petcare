<template>
  <div class="technician-history">
    <el-card>
      <template #header>
        <h3>服务历史记录</h3>
      </template>
      
      <el-table :data="history" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="petName" label="宠物信息" width="150" />
        <el-table-column prop="serviceName" label="服务项目" />
        <el-table-column prop="amount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="completeTime" label="完成时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.completeTime) }}
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHistory } from '@/api/technician'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const history = ref([])

const formatTime = (time) => {
  if (!time) return '-'
  const dateStr = time.toString()
  const date = new Date(dateStr.includes('T') ? dateStr : dateStr.replace(' ', 'T'))
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await getHistory()
    history.value = res.data || []
    total.value = history.value.length
  } catch (error) {
    console.error('加载服务历史失败:', error)
    ElMessage.error('加载服务历史失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.price {
  color: #f56c6c;
  font-weight: bold;
}
</style>
