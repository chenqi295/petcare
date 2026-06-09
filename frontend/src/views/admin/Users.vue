<template>
  <div class="users-page">
    <el-card>
      <el-table :data="filteredUsers" v-loading="loading">
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="150" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            <el-tag type="primary">
              普通用户
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              size="small" 
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <el-empty v-if="!loading && filteredUsers.length === 0" description="暂无用户数据" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllUsers, updateUser } from '@/api/user'

const loading = ref(false)
const users = ref([])

// 过滤掉管理员和技师用户，只显示普通用户
const filteredUsers = computed(() => {
  return users.value.filter(user => user.role === 'USER')
})

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await getAllUsers()
    users.value = res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleToggleStatus = async (user) => {
  const action = user.status === 1 ? '禁用' : '启用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${user.username}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 切换状态
    const newStatus = user.status === 1 ? 0 : 1
    await updateUser(user.id, {
      status: newStatus
    })
    
    ElMessage.success(`${action}成功`)
    loadUsers() // 重新加载列表
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
