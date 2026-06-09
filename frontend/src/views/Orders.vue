<template>
  <div class="orders-page">
    <div class="container">
      <h2 class="page-title">我的订单</h2>
      
      <!-- 订单列表 -->
      <el-table :data="orders" v-loading="loading" style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="serviceName" label="服务项目" min-width="120" />
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
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.payStatus === 0 && (row.status === 1 || row.status === 2 || row.status === 3)"
              size="small"
              type="primary"
              @click="handlePay(row)"
            >
              支付
            </el-button>
            <el-button
              v-if="row.status === 1 && row.payStatus === 0"
              size="small"
              type="danger"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button
              v-if="row.status === 3 && row.payStatus === 1 && !row.hasReviewed"
              size="small"
              type="success"
              @click="handleReview(row)"
            >
              评价
            </el-button>
            <el-tag
              v-else-if="row.status === 3 && row.payStatus === 1 && row.hasReviewed"
              size="small"
              type="info"
            >
              已评价
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && orders.length === 0" description="暂无订单记录" />
    </div>
    
    <!-- 支付对话框 -->
    <el-dialog v-model="payDialogVisible" title="选择支付方式" width="400px">
      <div style="text-align: center;">
        <p style="margin-bottom: 20px;">
          订单金额：<span style="color: #f56c6c; font-weight: bold; font-size: 20px;">¥{{ currentOrder?.amount }}</span>
        </p>
        <p style="color: #909399; font-size: 13px; margin-bottom: 15px;">请选择支付方式（模拟支付）</p>
        <el-select v-model="selectedPayMethod" placeholder="请选择支付方式" style="width: 100%;">
          <el-option label="支付宝" value="ALIPAY" />
          <el-option label="微信支付" value="WECHAT" />
          <el-option label="银行卡" value="BANK_CARD" />
        </el-select>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPay" :loading="paying">确认支付</el-button>
      </template>
    </el-dialog>
    
    <!-- 评价对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="服务评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" show-text :texts="['极差', '失望', '一般', '满意', '超赞']" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请分享您对本次服务的感受和建议..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview" :loading="reviewing">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyOrders, payOrder, cancelOrder } from '@/api/order'
import { createReview } from '@/api/review'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const paying = ref(false)
const orders = ref([])
const selectedPayMethod = ref('ALIPAY')
const payDialogVisible = ref(false)
const currentOrder = ref(null)
const reviewDialogVisible = ref(false)
const reviewing = ref(false)
const reviewForm = ref({
  orderId: null,
  serviceId: null,
  technicianId: null,
  rating: 5,
  content: ''
})

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  if (!userStore.token) {
    orders.value = []
    return
  }
  loading.value = true
  try {
    const res = await getMyOrders()
    orders.value = res.data || []
    
    // 获取已评价的订单ID列表
    try {
      const { getMyReviews } = await import('@/api/review')
      const reviewRes = await getMyReviews()
      const reviewedOrderIds = (reviewRes.data || []).map(r => r.orderId)
      
      // 为订单添加已评价标记
      orders.value = orders.value.map(order => ({
        ...order,
        hasReviewed: reviewedOrderIds.includes(order.id)
      }))
    } catch (e) {
      console.error('获取评价列表失败:', e)
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    // 静默失败，不影响页面展示
    orders.value = []
  } finally {
    loading.value = false
  }
}

const handlePay = async (order) => {
  currentOrder.value = order
  selectedPayMethod.value = 'ALIPAY' // 重置为默认值
  payDialogVisible.value = true
}

const confirmPay = async () => {
  if (!selectedPayMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }
  
  paying.value = true
  try {
    await payOrder(currentOrder.value.id, selectedPayMethod.value)
    ElMessage.success('支付成功')
    payDialogVisible.value = false
    loadOrders()
  } catch (error) {
    console.error(error)
  } finally {
    paying.value = false
  }
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrder(order.id)
    ElMessage.success('取消成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleReview = (order) => {
  reviewForm.value = {
    orderId: order.id,
    serviceId: order.serviceId,
    technicianId: order.technicianId,
    rating: 5,
    content: ''
  }
  reviewDialogVisible.value = true
}

const submitReview = async () => {
  if (!reviewForm.value.rating) {
    ElMessage.warning('请选择评分')
    return
  }
  if (!reviewForm.value.content || reviewForm.value.content.trim() === '') {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  reviewing.value = true
  try {
    await createReview(reviewForm.value)
    ElMessage.success('评价成功')
    reviewDialogVisible.value = false
    loadOrders()
  } catch (error) {
    console.error('评价失败:', error)
  } finally {
    reviewing.value = false
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
.orders-page {
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

.price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}
</style>
