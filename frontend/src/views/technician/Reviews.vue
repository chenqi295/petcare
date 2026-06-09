<template>
  <div class="technician-reviews">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>客户评价</h3>
          <div class="stats">
            <span class="stat-item">
              平均评分：<el-rate v-model="averageRating" disabled show-score text-color="#ff9900" />
            </span>
            <span class="stat-item">评价总数：{{ reviews.length }}</span>
          </div>
        </div>
      </template>
      
      <!-- 评价列表 -->
      <div v-loading="loading">
        <el-card 
          v-for="review in reviews" 
          :key="review.id" 
          class="review-card" 
          shadow="hover"
        >
          <div class="review-header">
            <div class="review-info">
              <div class="user-name">{{ review.userName || '匿名用户' }}</div>
              <div class="order-info">
                <span class="order-no">订单号：{{ review.orderNo || '-' }}</span>
                <span class="service-name">{{ review.serviceName || '服务' }}</span>
              </div>
              <el-rate v-model="review.rating" disabled show-text :texts="['极差', '失望', '一般', '满意', '超赞']" />
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTechnicianReviews } from '@/api/technician'

const loading = ref(false)
const reviews = ref([])

// 计算平均评分
const averageRating = computed(() => {
  if (reviews.value.length === 0) return 5.0
  const sum = reviews.value.reduce((acc, r) => acc + (r.rating || 0), 0)
  return Math.round((sum / reviews.value.length) * 10) / 10
})

onMounted(() => {
  loadReviews()
})

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await getTechnicianReviews()
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
.technician-reviews {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 5px 0;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  color: #1e293b;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stats {
  display: flex;
  gap: 25px;
  align-items: center;
}

.stat-item {
  font-size: 15px;
  color: #64748b;
  font-weight: 500;
}

.review-card {
  margin-bottom: 20px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px !important;
  background: white;
  position: relative;
  overflow: hidden;
}

.review-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.review-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.15) !important;
}

.review-card:hover::before {
  opacity: 1;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f1f5f9;
}

.review-info {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
}

.user-name {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name::before {
  content: '👤';
  font-size: 18px;
}

.order-info {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.order-no {
  font-size: 13px;
  color: #94a3b8;
  font-family: 'Courier New', monospace;
  background: #f8fafc;
  padding: 4px 10px;
  border-radius: 6px;
}

.service-name {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  padding: 4px 12px;
  border-radius: 6px;
}

.review-time {
  font-size: 13px;
  color: #94a3b8;
  white-space: nowrap;
}

.review-content {
  font-size: 15px;
  color: #475569;
  line-height: 1.8;
  margin-bottom: 15px;
  padding: 15px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border-left: 4px solid #667eea;
}

.review-images {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-top: 15px;
}

.review-images :deep(.el-image) {
  border-radius: 12px !important;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.review-images :deep(.el-image:hover) {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

/* 空状态优化 */
:deep(.el-empty) {
  padding: 60px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .stats {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .review-header {
    flex-direction: column;
    gap: 10px;
  }
  
  .order-info {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}
</style>
