<template>
  <div class="reviews-page">
    <!-- 简单的导航栏 -->
    <div class="simple-navbar">
      <div class="navbar-content">
        <el-button text @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          返回服务人员
        </el-button>
        <div class="logo">🐾 宠物服务平台</div>
      </div>
    </div>
    
    <div class="container">
      <!-- 标题 -->
      <h1 class="page-title">用户评价</h1>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <!-- 评价列表 -->
      <div v-else-if="reviews.length > 0" class="reviews-list">
        <el-card 
          v-for="review in reviews" 
          :key="review.id" 
          class="review-card"
          shadow="hover"
        >
          <div class="review-header">
            <div class="user-info">
              <el-avatar :size="40">
                {{ review.userName ? review.userName.charAt(0) : 'U' }}
              </el-avatar>
              <div class="user-details">
                <span class="user-name">{{ review.userName || '匿名用户' }}</span>
                <span class="review-time">{{ formatTime(review.createTime) }}</span>
              </div>
            </div>
            <div class="rating-badge">
              <el-rate 
                v-model="review.rating" 
                disabled 
                show-score 
                text-color="#ff9900"
                score-template="{value}"
              />
            </div>
          </div>
          
          <div class="review-content">
            <p class="content-text">{{ review.content }}</p>
            
            <!-- 评价图片（如果有） -->
            <div v-if="review.images" class="review-images">
              <el-image 
                v-for="(img, index) in parseImages(review.images)" 
                :key="index"
                :src="img"
                :preview-src-list="parseImages(review.images)"
                fit="cover"
                class="review-image"
              />
            </div>
          </div>
          
          <div class="review-footer">
            <el-tag size="small" type="info">
              <el-icon><Document /></el-icon>
              订单号: {{ review.orderNo }}
            </el-tag>
            <el-tag size="small" type="primary">
              <el-icon><Service /></el-icon>
              {{ review.serviceName }}
            </el-tag>
          </div>
        </el-card>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-else description="暂无评价" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Document, Service } from '@element-plus/icons-vue'
import { getTechnicianReviewsPublic } from '@/api/technician'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const reviews = ref([])
const technicianId = ref(route.params.technicianId)

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 解析图片字符串为数组
const parseImages = (images) => {
  if (!images) return []
  // 如果包含逗号，按逗号分割；否则返回原字符串数组
  return images.includes(',') ? images.split(',').map(img => img.trim()) : [images]
}

// 加载评价列表
const loadReviews = async () => {
  loading.value = true
  try {
    console.log('正在加载技师评价，技师ID:', technicianId.value)
    const res = await getTechnicianReviewsPublic(technicianId.value)
    console.log('评价列表响应:', res)
    if (res.code === 200) {
      reviews.value = res.data || []
      console.log('评价数量:', reviews.value.length)
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载评价列表失败:', error)
    ElMessage.error('加载失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(() => {
  console.log('TechnicianReviews 页面已加载')
  console.log('当前路由参数 technicianId:', route.params.technicianId)
  if (!technicianId.value) {
    ElMessage.error('缺少技师ID参数')
    return
  }
  loadReviews()
})
</script>

<style scoped>
.reviews-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.simple-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.06);
  height: 70px;
}

.navbar-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 30px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.back-btn {
  font-size: 16px;
  color: #667eea;
  padding: 8px 16px;
}

.back-btn:hover {
  color: #764ba2;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 110px 20px 40px;
}

.page-title {
  font-size: 28px;
  color: #333;
  margin: 0 0 30px 0;
  font-weight: 600;
}

.loading-container {
  background: white;
  padding: 30px;
  border-radius: 8px;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-card {
  transition: all 0.3s ease;
  border-radius: 12px;
}

.review-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.review-time {
  font-size: 13px;
  color: #666;
}

.rating-badge {
  flex-shrink: 0;
}

.review-content {
  margin-bottom: 15px;
}

.content-text {
  font-size: 15px;
  color: #333;
  line-height: 1.8;
  margin: 0 0 15px 0;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.review-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s;
}

.review-image:hover {
  transform: scale(1.05);
}

.review-footer {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-content {
    padding: 0 15px;
  }
  
  .logo {
    font-size: 18px;
  }
  
  .page-title {
    font-size: 22px;
  }
  
  .review-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .review-image {
    width: 80px;
    height: 80px;
  }
}
</style>
