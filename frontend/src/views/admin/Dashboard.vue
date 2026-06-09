<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon service">
              <el-icon><Service /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.technicianCount }}</div>
              <div class="stat-label">服务人员总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon appointment">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.appointmentCount }}</div>
              <div class="stat-label">预约总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon order">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.orderCount }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 收入统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card class="stat-card revenue-card">
          <div class="stat-content">
            <div class="stat-icon revenue total">
              <el-icon><Coin /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">¥{{ formatRevenue(stats.totalRevenue) }}</div>
              <div class="stat-label">总收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card revenue-card">
          <div class="stat-content">
            <div class="stat-icon revenue yearly">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">¥{{ formatRevenue(stats.yearlyRevenue) }}</div>
              <div class="stat-label">{{ stats.year }}年收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card revenue-card">
          <div class="stat-content">
            <div class="stat-icon revenue monthly">
              <el-icon><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">¥{{ formatRevenue(stats.monthlyRevenue) }}</div>
              <div class="stat-label">{{ stats.month }}月收入</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 近七天收入图表 -->
    <el-card class="chart-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>近七天收入趋势</span>
        </div>
      </template>
      <div ref="revenueChartRef" class="revenue-chart"></div>
    </el-card>
    
    <!-- 最近预约 -->
    <el-card class="recent-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>最近预约</span>
          <el-button 
            type="primary" 
            @click="router.push('/admin/appointments')"
            class="view-all-btn"
          >
            查看全部
          </el-button>
        </div>
      </template>
      <el-table :data="recentAppointments" style="width: 100%">
        <el-table-column prop="appointmentNo" label="预约编号" width="180" />
        <el-table-column prop="appointmentTime" label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.appointmentTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              size="small"
              type="success"
              @click="handleConfirm(row.id)"
            >
              确认
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Calendar, ShoppingCart, Service, ArrowRight, Coin, TrendCharts, DataLine } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getAllUsers, getNonAdminUserCount } from '@/api/user'
import { getAllAppointments, confirmAppointment } from '@/api/appointment'
import { getAllOrders, getRevenueStats, getSevenDayRevenue } from '@/api/order'
import { getAllTechnicians } from '@/api/technician'

const router = useRouter()

const stats = ref({
  userCount: 0,
  appointmentCount: 0,
  orderCount: 0,
  technicianCount: 0,
  totalRevenue: 0,
  yearlyRevenue: 0,
  monthlyRevenue: 0,
  year: new Date().getFullYear(),
  month: new Date().getMonth() + 1
})

const recentAppointments = ref([])
const revenueChartRef = ref(null)
let revenueChart = null

onMounted(() => {
  loadStats()
  loadRecentAppointments()
  loadSevenDayRevenue()
})

const loadStats = async () => {
  try {
    const [usersCountRes, appointmentsRes, ordersRes, techniciansRes, revenueRes] = await Promise.all([
      getNonAdminUserCount(),
      getAllAppointments(),
      getAllOrders(),
      getAllTechnicians(),
      getRevenueStats()
    ])
    
    stats.value = {
      userCount: usersCountRes.data || 0,
      appointmentCount: appointmentsRes.data?.length || 0,
      orderCount: ordersRes.data?.length || 0,
      technicianCount: techniciansRes.data?.length || 0,
      totalRevenue: revenueRes.data?.totalRevenue || 0,
      yearlyRevenue: revenueRes.data?.yearlyRevenue || 0,
      monthlyRevenue: revenueRes.data?.monthlyRevenue || 0,
      year: revenueRes.data?.year || new Date().getFullYear(),
      month: revenueRes.data?.month || (new Date().getMonth() + 1)
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRecentAppointments = async () => {
  try {
    const res = await getAllAppointments()
    // 取最近的5条预约
    recentAppointments.value = (res.data || []).slice(0, 5)
  } catch (error) {
    console.error('加载最近预约失败:', error)
  }
}

const handleConfirm = async (id) => {
  try {
    await confirmAppointment(id)
    ElMessage.success('确认成功')
    loadRecentAppointments()
    loadStats()
  } catch (error) {
    console.error(error)
  }
}

const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

const getStatusText = (status) => {
  const map = {
    0: '已取消',
    1: '待确认',
    2: '已确认',
    3: '服务中',
    4: '已完成',
    5: '已过期'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    0: 'info',
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'success',
    5: 'danger'
  }
  return map[status] || ''
}

// 格式化收入显示
const formatRevenue = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 加载近七天收入数据
const loadSevenDayRevenue = async () => {
  try {
    const res = await getSevenDayRevenue()
    if (res.code === 200 && res.data) {
      initRevenueChart(res.data)
    }
  } catch (error) {
    console.error('加载七天收入失败:', error)
  }
}

// 初始化收入图表
const initRevenueChart = (data) => {
  if (!revenueChartRef.value) return
  
  // 如果图表实例已存在，先销毁
  if (revenueChart) {
    revenueChart.dispose()
  }
  
  // 创建新的图表实例
  revenueChart = echarts.init(revenueChartRef.value)
  
  const dates = data.map(item => item.date)
  const revenues = data.map(item => Number(item.revenue))
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const param = params[0]
        return `${param.name}<br/>收入: ¥${Number(param.value).toFixed(2)}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: '#e5e7eb'
        }
      },
      axisLabel: {
        color: '#6b7280',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      name: '金额(元)',
      nameTextStyle: {
        color: '#6b7280',
        fontSize: 12
      },
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: '#f3f4f6'
        }
      },
      axisLabel: {
        color: '#6b7280',
        fontSize: 12,
        formatter: function(value) {
          return '¥' + value
        }
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        lineStyle: {
          width: 3,
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#667eea' },
            { offset: 1, color: '#764ba2' }
          ])
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
            { offset: 1, color: 'rgba(118, 75, 162, 0.05)' }
          ])
        },
        itemStyle: {
          color: '#667eea',
          borderWidth: 2,
          borderColor: '#fff'
        },
        emphasis: {
          itemStyle: {
            color: '#764ba2',
            shadowBlur: 10,
            shadowColor: 'rgba(102, 126, 234, 0.5)'
          }
        },
        data: revenues
      }
    ]
  }
  
  revenueChart.setOption(option)
}
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.stat-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 16px !important;
  overflow: hidden;
  position: relative;
  background: white;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(255, 255, 255, 0) 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15) !important;
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.stat-icon::after {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover .stat-icon {
  transform: rotate(10deg) scale(1.1);
}

.stat-card:hover .stat-icon::after {
  opacity: 1;
}

.stat-icon.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.appointment {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.order {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.service {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

/* 收入统计图标样式 */
.stat-icon.revenue {
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon.revenue.total {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.revenue.yearly {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.revenue.monthly {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 800;
  background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 8px;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.recent-card {
  margin-top: 20px;
  border-radius: 16px !important;
  background: white;
}

.chart-card {
  border-radius: 16px !important;
  background: white;
}

.revenue-chart {
  width: 100%;
  height: 350px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-header .view-all-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  color: white !important;
  font-weight: 600 !important;
  border-radius: 12px !important;
  padding: 10px 20px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

.card-header .view-all-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4) !important;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 12px !important;
  overflow: hidden;
}

:deep(.el-table th) {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%) !important;
  font-weight: 700 !important;
  color: #1e293b;
}

:deep(.el-table tr:hover > td) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%) !important;
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
  border: none !important;
  border-radius: 10px !important;
  font-weight: 600 !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

:deep(.el-button--success:hover) {
  transform: translateY(-2px) !important;
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.4) !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stat-number {
    font-size: 24px;
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    font-size: 28px;
  }
  
  .card-header span {
    font-size: 18px;
  }
}
</style>
