<template>
  <div class="technicians-page">
    <div class="container">
      <h1 class="page-title">服务人员</h1>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      
      <!-- 技师列表 -->
      <div v-else-if="technicians.length > 0" class="technician-grid">
        <el-card 
          v-for="tech in technicians" 
          :key="tech.id" 
          class="technician-card"
          shadow="hover"
        >
          <div class="card-content">
            <!-- 头像区域 -->
            <div class="avatar-section">
              <el-avatar 
                :size="80" 
                :src="tech.avatar ? `http://localhost:8080/api${tech.avatar}` : ''"
              >
                {{ tech.name ? tech.name.charAt(0) : 'T' }}
              </el-avatar>
              <div class="status-badge" :class="tech.status === 1 ? 'working' : 'resting'">
                {{ tech.status === 1 ? '工作中' : '休息中' }}
              </div>
            </div>
            
            <!-- 基本信息 -->
            <div class="info-section">
              <h3 class="technician-name">{{ tech.name || '未命名' }}</h3>
              <p class="username">@{{ tech.username }}</p>
              
              <!-- 评分 -->
              <div class="rating-section">
                <el-rate 
                  v-model="tech.rating" 
                  disabled 
                  show-score 
                  text-color="#ff9900"
                  score-template="{value}"
                />
              </div>
              
              <!-- 服务次数 -->
              <div class="stats-row">
                <span class="stat-item">
                  <el-icon><Service /></el-icon>
                  服务 {{ tech.serviceCount || 0 }} 次
                </span>
                <span class="stat-item" v-if="tech.workYears">
                  <el-icon><Clock /></el-icon>
                  {{ tech.workYears }} 年经验
                </span>
              </div>
              
              <!-- 技能标签 -->
              <div v-if="tech.skills" class="skills-section">
                <el-tag 
                  v-for="(skill, index) in parseSkills(tech.skills)" 
                  :key="index"
                  size="small"
                  type="primary"
                  effect="plain"
                >
                  {{ skill }}
                </el-tag>
              </div>
              
              <!-- 简介 -->
              <p v-if="tech.introduction" class="introduction">
                {{ tech.introduction }}
              </p>
              
              <!-- 查看评价按钮 -->
              <el-button 
                type="primary" 
                plain
                size="small"
                @click="viewReviews(tech.id)"
                class="review-btn"
              >
                <el-icon><ChatDotRound /></el-icon>
                查看全部评价
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-else description="暂无服务人员" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Service, Clock, ChatDotRound } from '@element-plus/icons-vue'
import { getAllTechniciansPublic } from '@/api/technician'

const router = useRouter()
const loading = ref(false)
const technicians = ref([])

// 解析技能字符串为数组
const parseSkills = (skills) => {
  if (!skills) return []
  // 如果包含逗号，按逗号分割；否则返回原字符串数组
  return skills.includes(',') ? skills.split(',').map(s => s.trim()) : [skills]
}

// 查看技师评价
const viewReviews = (technicianId) => {
  router.push(`/technician/${technicianId}/reviews`)
}

// 加载技师列表
const loadTechnicians = async () => {
  loading.value = true
  try {
    const res = await getAllTechniciansPublic()
    if (res.code === 200) {
      technicians.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载技师列表失败:', error)
    ElMessage.error('加载失败，请刷新重试')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTechnicians()
})
</script>

<style scoped>
.technicians-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 40px 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-title {
  font-size: 32px;
  color: #333;
  margin-bottom: 30px;
  font-weight: 600;
  text-align: center;
}

.loading-container {
  background: white;
  padding: 30px;
  border-radius: 8px;
}

.technician-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.technician-card {
  transition: all 0.3s ease;
  border-radius: 12px;
  overflow: hidden;
}

.technician-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.card-content {
  padding: 10px;
}

.avatar-section {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  margin-bottom: 20px;
  padding: 20px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
}

.avatar-section .el-avatar {
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.status-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  color: white;
  backdrop-filter: blur(10px);
}

.status-badge.working {
  background: rgba(103, 194, 58, 0.9);
}

.status-badge.resting {
  background: rgba(144, 147, 153, 0.9);
}

.info-section {
  padding: 0 10px;
}

.technician-name {
  font-size: 20px;
  color: #333;
  margin: 0 0 5px 0;
  font-weight: 600;
  text-align: center;
}

.username {
  font-size: 14px;
  color: #666;
  margin: 0 0 15px 0;
  text-align: center;
}

.rating-section {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  margin-bottom: 15px;
  padding: 10px 0;
  background: #f5f7fa;
  border-radius: 6px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #444;
}

.stat-item .el-icon {
  font-size: 16px;
  color: #667eea;
}

.skills-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
  justify-content: center;
}

.introduction {
  font-size: 14px;
  color: #444;
  line-height: 1.6;
  margin: 0;
  padding: 10px;
  background: #fafafa;
  border-radius: 6px;
  min-height: 60px;
}

.review-btn {
  width: 100%;
  margin-top: 15px;
  font-weight: 600;
  background-color: #667eea !important;
  border-color: #667eea !important;
  color: white !important;
}

.review-btn:hover {
  background-color: #764ba2 !important;
  border-color: #764ba2 !important;
  color: white !important;
}

.review-btn:focus {
  background-color: #764ba2 !important;
  border-color: #764ba2 !important;
  color: white !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .technician-grid {
    grid-template-columns: 1fr;
  }
  
  .page-title {
    font-size: 24px;
  }
}
</style>
