<template>
  <div class="reviews-page">
    <div class="container">
      <h2 class="page-title">我的评价</h2>
      
      <!-- 评价列表 -->
      <el-card v-for="review in reviews" :key="review.id" class="review-card" shadow="hover">
        <div class="review-header">
          <div class="review-info">
            <span class="order-no">订单号：{{ review.orderNo || '-' }}</span>
            <span class="service-name">{{ review.serviceName || '服务' }}</span>
            <el-rate v-model="review.rating" disabled show-score text-color="#ff9900" />
          </div>
          <span class="review-time">{{ formatTime(review.createTime) }}</span>
        </div>
        <div class="review-content">
          {{ review.content }}
        </div>
        <div v-if="review.images" class="review-images">
          <el-image
            v-for="(img, index) in parseImages(review.images)"
            :key="index"
            :src="'http://localhost:8080/api' + img"
            :preview-src-list="parseImages(review.images).map(i => 'http://localhost:8080/api' + i)"
            fit="cover"
            style="width: 100px; height: 100px; margin-right: 10px;"
          />
        </div>
      </el-card>
      
      <el-empty v-if="!loading && reviews.length === 0" description="还没有评价记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyReviews } from '@/api/review'

const loading = ref(false)
const reviews = ref([])

onMounted(() => {
  loadReviews()
})

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await getMyReviews()
    reviews.value = res.data || []
  } catch (error) {
    console.error('加载评价失败:', error)
    ElMessage.error('加载评价失败')
  } finally {
    loading.value = false
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const parseImages = (images) => {
  if (!images) return []
  try {
    return JSON.parse(images)
  } catch (e) {
    return []
  }
}
</script>

<style scoped>
.reviews-page {
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

.review-card {
  margin-bottom: 20px;
  transition: all 0.3s;
}

.review-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.review-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-no {
  font-size: 13px;
  color: #909399;
  font-family: monospace;
}

.service-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.review-time {
  font-size: 14px;
  color: #999;
}

.review-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}
</style>
