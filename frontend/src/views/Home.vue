<template>
  <div class="home">
    <!-- Banner -->
    <div class="banner">
      <div class="banner-content fade-in-up">
        <div class="banner-icon">🐾</div>
        <h1>专业宠物服务平台</h1>
        <p>为您的爱宠提供最优质的护理服务</p>
        <el-button type="primary" size="large" @click="$router.push('/services')" class="cta-button">
          <span>立即预约</span>
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <!-- 装饰元素 -->
      <div class="banner-decoration">
        <div class="deco-circle circle-1"></div>
        <div class="deco-circle circle-2"></div>
        <div class="deco-circle circle-3"></div>
        <div class="deco-dots">
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
          <div class="dot"></div>
        </div>
        <div class="floating-shapes">
          <div class="floating-shape"></div>
          <div class="floating-shape"></div>
          <div class="floating-shape"></div>
          <div class="floating-shape"></div>
        </div>
      </div>
    </div>

    <!-- 热门服务 -->
    <div class="container">
      <h2 class="section-title fade-in-up">
        <span class="title-icon">🔥</span>
        热门服务
      </h2>
      <el-row :gutter="20" v-loading="loading">
        <el-col :xs="24" :sm="12" :md="8" v-for="service in hotServices" :key="service.id">
          <el-card class="service-card" shadow="hover">
            <div class="service-icon">🐕</div>
            <h3>{{ service.name }}</h3>
            <p class="desc">{{ service.description }}</p>
            <div class="price-tag">
              <span class="currency">¥</span>
              <span class="amount">{{ service.price }}</span>
            </div>
            <el-button type="primary" @click="handleQuickBooking(service)" class="service-button">
              立即预约
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 特色优势 -->
    <div class="features">
      <div class="container">
        <h2 class="section-title fade-in-up">
          <span class="title-icon">✨</span>
          为什么选择我们
        </h2>
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="6">
            <div class="feature-item fade-in-up">
              <div class="icon-wrapper">
                <div class="icon">✓</div>
              </div>
              <h3>专业团队</h3>
              <p>经验丰富的宠物护理专家</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="feature-item fade-in-up" style="animation-delay: 0.1s">
              <div class="icon-wrapper">
                <div class="icon">✓</div>
              </div>
              <h3>优质服务</h3>
              <p>标准化服务流程保障</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="feature-item fade-in-up" style="animation-delay: 0.2s">
              <div class="icon-wrapper">
                <div class="icon">✓</div>
              </div>
              <h3>在线预约</h3>
              <p>随时随地便捷预约</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="12" :md="6">
            <div class="feature-item fade-in-up" style="animation-delay: 0.3s">
              <div class="icon-wrapper">
                <div class="icon">✓</div>
              </div>
              <h3>售后保障</h3>
              <p>完善的售后服务体系</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getHotServices } from '@/api/service'
import { ArrowRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const hotServices = ref([])

onMounted(() => {
  loadHotServices()
})

const loadHotServices = async () => {
  loading.value = true
  try {
    const res = await getHotServices(6)
    hotServices.value = res.data || []
  } catch (error) {
    console.error('加载热门服务失败:', error)
    // 静默失败，不影响页面展示
    hotServices.value = []
  } finally {
    loading.value = false
  }
}

// 快速预约 - 跳转到服务页面并打开预约对话框
const handleQuickBooking = (service) => {
  console.log('点击的服务:', service)
  console.log('服务ID:', service.id)
  console.log('分类ID:', service.categoryId)
  
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 跳转到服务页面，并通过 query 参数传递服务 ID
  console.log('准备跳转...')
  router.push({
    path: '/services',
    query: {
      serviceId: service.id,
      categoryId: service.categoryId
    }
  })
  console.log('跳转完成')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

/* Banner 样式 */
.banner {
  position: relative;
  background: 
    linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-size: 300% 300%;
  animation: gradientFlow 12s ease infinite;
  color: white;
  text-align: center;
  padding: 120px 20px 100px;
  overflow: hidden;
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

/* 渐变流动动画 */
@keyframes gradientFlow {
  0% {
    background-position: 0% 50%;
  }
  25% {
    background-position: 100% 50%;
  }
  50% {
    background-position: 100% 100%;
  }
  75% {
    background-position: 0% 100%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.banner-content {
  position: relative;
  z-index: 2;
  max-width: 800px;
  margin: 0 auto;
}

.banner-icon {
  font-size: 90px;
  margin-bottom: 25px;
  animation: bounce 2s infinite;
  filter: drop-shadow(0 8px 20px rgba(0, 0, 0, 0.25));
  display: inline-block;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  25% {
    transform: translateY(-20px) rotate(-5deg);
  }
  75% {
    transform: translateY(-10px) rotate(5deg);
  }
}

.banner h1 {
  font-size: 56px;
  margin-bottom: 25px;
  font-weight: 800;
  text-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  letter-spacing: 3px;
  line-height: 1.2;
  background: linear-gradient(to right, #ffffff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.banner p {
  font-size: 22px;
  margin-bottom: 45px;
  opacity: 0.95;
  font-weight: 500;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cta-button {
  padding: 18px 50px !important;
  font-size: 20px !important;
  font-weight: 700 !important;
  border-radius: 50px !important;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.25) !important;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1) !important;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%) !important;
  color: #667eea !important;
  border: none !important;
  letter-spacing: 1px;
  position: relative;
  overflow: hidden;
}

.cta-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.6), transparent);
  transition: left 0.5s;
}

.cta-button:hover::before {
  left: 100%;
}

.cta-button:hover {
  transform: translateY(-5px) scale(1.05) !important;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.35) !important;
}

.cta-button :deep(.el-icon) {
  margin-left: 8px;
  transition: transform 0.3s;
}

.cta-button:hover :deep(.el-icon) {
  transform: translateX(5px);
}

/* Banner 装饰元素 */
.banner-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  background-image: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 40% 20%, rgba(255, 255, 255, 0.08) 0%, transparent 40%),
    radial-gradient(circle at 70% 30%, rgba(255, 255, 255, 0.12) 0%, transparent 45%);
  animation: lightShift 20s ease-in-out infinite;
}

/* 光斑移动动画 */
@keyframes lightShift {
  0%, 100% {
    transform: translate(0, 0);
  }
  25% {
    transform: translate(-20px, 20px);
  }
  50% {
    transform: translate(20px, -20px);
  }
  75% {
    transform: translate(-10px, -10px);
  }
}

/* 漂浮几何图形 */
.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
}

.floating-shape {
  position: absolute;
  border: 2px solid rgba(255, 255, 255, 0.3);
  animation: floatShape 20s infinite ease-in-out;
}

.floating-shape:nth-child(1) {
  width: 80px;
  height: 80px;
  top: 15%;
  left: 8%;
  border-radius: 30% 70% 70% 30% / 30% 30% 70% 70%;
  animation-delay: 0s;
  animation-duration: 18s;
}

.floating-shape:nth-child(2) {
  width: 60px;
  height: 60px;
  top: 65%;
  left: 85%;
  border-radius: 50%;
  animation-delay: 3s;
  animation-duration: 22s;
}

.floating-shape:nth-child(3) {
  width: 100px;
  height: 100px;
  top: 75%;
  left: 15%;
  border-radius: 60% 40% 30% 70% / 60% 30% 70% 40%;
  animation-delay: 6s;
  animation-duration: 25s;
}

.floating-shape:nth-child(4) {
  width: 70px;
  height: 70px;
  top: 25%;
  left: 75%;
  border-radius: 40% 60% 60% 40% / 40% 40% 60% 60%;
  animation-delay: 9s;
  animation-duration: 20s;
}

@keyframes floatShape {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg) scale(1);
    opacity: 0.6;
  }
  25% {
    transform: translate(30px, -30px) rotate(90deg) scale(1.1);
    opacity: 0.8;
  }
  50% {
    transform: translate(-20px, 20px) rotate(180deg) scale(0.9);
    opacity: 0.6;
  }
  75% {
    transform: translate(20px, 30px) rotate(270deg) scale(1.05);
    opacity: 0.7;
  }
}

/* 波浪装饰 */
.banner::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 100"><path fill="rgba(255,255,255,0.1)" d="M0,50 C360,100 720,0 1080,50 C1260,75 1380,60 1440,50 L1440,100 L0,100 Z"></path></svg>');
  background-size: cover;
  z-index: 1;
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.25);
  animation: float 15s infinite ease-in-out;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1), inset 0 0 20px rgba(255, 255, 255, 0.1);
}

.deco-circle::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.3), transparent, rgba(255, 255, 255, 0.2));
  z-index: -1;
  animation: glowRotate 8s linear infinite;
}

@keyframes glowRotate {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
  background: rgba(255, 255, 255, 0.15);
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -60px;
  right: -60px;
  animation-delay: 5s;
  background: rgba(255, 255, 255, 0.1);
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 40%;
  right: 10%;
  animation-delay: 10s;
  background: rgba(255, 255, 255, 0.12);
}

/* 添加更多装饰元素 */
.deco-dots {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
}

.dot {
  position: absolute;
  width: 8px;
  height: 8px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: twinkle 3s infinite ease-in-out;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.6), 0 0 20px rgba(255, 255, 255, 0.3);
}

.dot:nth-child(1) { top: 18%; left: 12%; animation-delay: 0s; }
.dot:nth-child(2) { top: 55%; left: 88%; animation-delay: 0.5s; }
.dot:nth-child(3) { top: 35%; left: 25%; animation-delay: 1s; }
.dot:nth-child(4) { top: 75%; left: 35%; animation-delay: 1.5s; }
.dot:nth-child(5) { top: 22%; left: 68%; animation-delay: 2s; }
.dot:nth-child(6) { top: 65%; left: 55%; animation-delay: 2.5s; }

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
    box-shadow: 0 0 10px rgba(255, 255, 255, 0.6), 0 0 20px rgba(255, 255, 255, 0.3);
  }
  50% {
    opacity: 1;
    transform: scale(1.8);
    box-shadow: 0 0 15px rgba(255, 255, 255, 0.9), 0 0 30px rgba(255, 255, 255, 0.5), 0 0 45px rgba(255, 255, 255, 0.3);
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 20px;
}

.section-title {
  text-align: center;
  font-size: 36px;
  margin-bottom: 50px;
  color: #1e293b;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.title-icon {
  font-size: 32px;
  animation: pulse 2s infinite;
}

/* 服务卡片样式 */
.service-card {
  margin-bottom: 30px;
  text-align: center;
  padding: 10px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px !important;
  background: white;
  position: relative;
  overflow: hidden;
}

.service-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.service-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.15) !important;
}

.service-card:hover::before {
  opacity: 1;
}

.service-icon {
  font-size: 60px;
  margin-bottom: 20px;
  animation: pulse 2s infinite;
  filter: drop-shadow(0 4px 10px rgba(0, 0, 0, 0.1));
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.service-card h3 {
  margin-bottom: 15px;
  color: #1e293b;
  font-size: 20px;
  font-weight: 700;
}

.desc {
  color: #64748b;
  margin-bottom: 20px;
  min-height: 45px;
  line-height: 1.6;
  font-size: 14px;
}

.price-tag {
  margin-bottom: 20px;
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4px;
  padding: 15px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
}

.currency {
  font-size: 20px;
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
}

.amount {
  font-size: 36px;
  background: linear-gradient(135deg, #f59e0b 0%, #f97316 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 800;
}

.service-button {
  width: 100%;
  border-radius: 12px !important;
  font-weight: 600 !important;
  padding: 14px !important;
  font-size: 16px !important;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.service-button:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4) !important;
}

/* 特色优势样式 */
.features {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 80px 0;
}

.feature-item {
  text-align: center;
  padding: 30px 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.feature-item:hover {
  transform: translateY(-5px);
}

.icon-wrapper {
  margin-bottom: 25px;
}

.feature-item .icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto;
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.feature-item:hover .icon {
  transform: rotate(360deg) scale(1.1);
  border-radius: 50%;
}

.feature-item h3 {
  margin-bottom: 12px;
  color: #1e293b;
  font-size: 20px;
  font-weight: 700;
}

.feature-item p {
  color: #64748b;
  line-height: 1.6;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner {
    padding: 60px 15px 40px;
    min-height: 400px;
  }
  
  .banner-icon {
    font-size: 60px;
    margin-bottom: 15px;
  }
  
  .banner h1 {
    font-size: 28px;
    margin-bottom: 15px;
    letter-spacing: 1px;
  }
  
  .banner p {
    font-size: 16px;
    margin-bottom: 30px;
  }
  
  .cta-button {
    padding: 14px 35px !important;
    font-size: 16px !important;
  }
  
  /* 隐藏部分装饰元素以提升性能 */
  .floating-shapes,
  .deco-dots {
    display: none;
  }
  
  .circle-1 {
    width: 150px;
    height: 150px;
  }
  
  .circle-2 {
    width: 100px;
    height: 100px;
  }
  
  .circle-3 {
    display: none;
  }
  
  .container {
    padding: 40px 15px;
  }
  
  .section-title {
    font-size: 24px;
    margin-bottom: 30px;
  }
  
  .title-icon {
    font-size: 24px;
  }
  
  /* 服务卡片单列显示 */
  .service-card {
    margin-bottom: 20px;
  }
  
  .service-icon {
    font-size: 50px;
  }
  
  .service-card h3 {
    font-size: 18px;
  }
  
  .desc {
    font-size: 13px;
    min-height: auto;
  }
  
  .amount {
    font-size: 28px;
  }
  
  /* 特色优势区域优化 */
  .features {
    padding: 40px 0;
  }
  
  .feature-item {
    padding: 20px 15px;
  }
  
  .feature-item .icon {
    width: 60px;
    height: 60px;
    font-size: 28px;
  }
  
  .feature-item h3 {
    font-size: 18px;
  }
  
  .feature-item p {
    font-size: 13px;
  }
}

/* 超小屏幕优化 */
@media (max-width: 480px) {
  .banner {
    padding: 50px 10px 30px;
    min-height: 350px;
  }
  
  .banner h1 {
    font-size: 24px;
  }
  
  .banner p {
    font-size: 14px;
  }
  
  .section-title {
    font-size: 20px;
  }
}
</style>
