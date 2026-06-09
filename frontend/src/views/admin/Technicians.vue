<template>
  <div class="technicians-page">
    <el-card>
      <el-table :data="technicians" v-loading="loading">
        <el-table-column prop="id" label="技师ID" width="100" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="workYears" label="工作年限" width="120">
          <template #default="{ row }">
            {{ row.workYears || 0 }} 年
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="120">
          <template #default="{ row }">
            <span class="rating">
              <el-icon color="#f5a623"><Star /></el-icon>
              {{ row.rating && row.rating > 0 ? row.rating.toFixed(1) : '5.0' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="serviceCount" label="服务次数" width="120">
          <template #default="{ row }">
            {{ row.serviceCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="账号状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.userStatus === 1 ? 'success' : 'danger'">
              {{ row.userStatus === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button size="small" type="success" @click="handleViewReviews(row)">
              查看评价
            </el-button>
            <el-button 
              size="small" 
              :type="row.userStatus === 1 ? 'danger' : 'success'"
              @click="handleToggleAccountStatus(row)"
            >
              {{ row.userStatus === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-empty v-if="!loading && technicians.length === 0" description="暂无技师数据" />
    
    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="技师详情"
      width="700px"
    >
      <el-descriptions :column="2" border v-if="currentTechnician">
        <el-descriptions-item label="技师ID">{{ currentTechnician.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ currentTechnician.userId }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentTechnician.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentTechnician.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentTechnician.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="工作年限">{{ currentTechnician.workYears || 0 }} 年</el-descriptions-item>
        <el-descriptions-item label="评分">
          <span class="rating">
            <el-icon color="#f5a623"><Star /></el-icon>
            {{ currentTechnician.rating && currentTechnician.rating > 0 ? currentTechnician.rating.toFixed(1) : '5.0' }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="服务次数">{{ currentTechnician.serviceCount || 0 }}</el-descriptions-item>
        <el-descriptions-item label="账号状态">
          <el-tag :type="currentTechnician.userStatus === 1 ? 'success' : 'danger'">
            {{ currentTechnician.userStatus === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="工作状态">
          <el-tag :type="currentTechnician.status === 1 ? 'success' : 'info'">
            {{ currentTechnician.status === 1 ? '工作中' : '休息中' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="擅长技能" :span="2">{{ currentTechnician.skills || '-' }}</el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">{{ currentTechnician.introduction || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ formatTime(currentTechnician.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看评价对话框 -->
    <el-dialog
      v-model="reviewsDialogVisible"
      title="客户评价"
      width="800px"
    >
      <div v-loading="reviewsLoading">
        <el-card 
          v-for="review in reviews" 
          :key="review.id" 
          class="review-card" 
          shadow="hover"
          style="margin-bottom: 15px;"
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
        </el-card>
        
        <el-empty v-if="!reviewsLoading && reviews.length === 0" description="还没有评价记录" />
      </div>
      <template #footer>
        <el-button @click="reviewsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import { getAllTechnicians, updateTechnicianAccountStatus } from '@/api/technician'
import { getTechnicianReviews } from '@/api/review'

const loading = ref(false)
const technicians = ref([])
const detailDialogVisible = ref(false)
const currentTechnician = ref(null)
const reviewsDialogVisible = ref(false)
const reviewsLoading = ref(false)
const reviews = ref([])

onMounted(() => {
  loadTechnicians()
})

const loadTechnicians = async () => {
  loading.value = true
  try {
    const res = await getAllTechnicians()
    technicians.value = res.data || []
  } catch (error) {
    console.error(error)
    ElMessage.error('加载技师列表失败')
  } finally {
    loading.value = false
  }
}

const handleViewDetail = (technician) => {
  currentTechnician.value = technician
  detailDialogVisible.value = true
}

const handleViewReviews = async (technician) => {
  currentTechnician.value = technician
  reviewsDialogVisible.value = true
  reviewsLoading.value = true
  try {
    const res = await getTechnicianReviews(technician.id)
    reviews.value = res.data || []
  } catch (error) {
    console.error('加载评价失败:', error)
    ElMessage.error('加载评价失败')
  } finally {
    reviewsLoading.value = false
  }
}

const handleToggleAccountStatus = async (technician) => {
  const action = technician.userStatus === 1 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}技师 "${technician.name}" 的账号吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 切换状态
    const newStatus = technician.userStatus === 1 ? 0 : 1
    await updateTechnicianAccountStatus(technician.id, newStatus)
    
    ElMessage.success(`${action}成功`)
    loadTechnicians() // 重新加载列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(`${action}失败`)
    }
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}
</script>

<style scoped>
.rating {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #f5a623;
  font-weight: bold;
}

.review-card {
  transition: all 0.3s;
}

.review-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.review-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.order-info {
  display: flex;
  gap: 15px;
  align-items: center;
}

.order-no {
  font-size: 13px;
  color: #909399;
  font-family: monospace;
}

.service-name {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.review-time {
  font-size: 14px;
  color: #999;
}

.review-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}
</style>
