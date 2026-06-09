<template>
  <div class="services-page">
    <el-card>
      <el-tabs v-model="activeTab">
        <!-- 服务分类标签页 -->
        <el-tab-pane label="服务分类" name="categories">
          <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center;">
            <span style="font-size: 14px; color: #666;">管理服务分类</span>
            <el-button type="primary" @click="handleAddCategory">
              <el-icon><Plus /></el-icon>
              添加分类
            </el-button>
          </div>
          
          <el-table :data="categories" v-loading="loading">
            <el-table-column prop="name" label="分类名称" />
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column prop="sort" label="排序" width="100" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="handleViewItems(row)">
                  查看项目
                </el-button>
                <el-button size="small" type="danger" @click="handleDeleteCategory(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty v-if="!loading && categories.length === 0" description="暂无服务分类" />
        </el-tab-pane>
        
        <!-- 服务项目标签页 -->
        <el-tab-pane label="服务项目" name="items">
          <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center;">
            <div>
              <span style="font-size: 14px; color: #666;">管理服务项目</span>
              <el-tag v-if="currentCategoryId" type="info" size="small" style="margin-left: 10px;">
                当前分类：{{ categories.find(c => c.id === currentCategoryId)?.name || '未知' }}
              </el-tag>
            </div>
            <div style="display: flex; gap: 10px;">
              <el-button v-if="currentCategoryId" @click="handleShowAll">
                显示全部
              </el-button>
              <el-button type="primary" @click="handleAdd">
                <el-icon><Plus /></el-icon>
                添加服务
              </el-button>
            </div>
          </div>
          
          <el-table :data="serviceItems" v-loading="loading">
            <el-table-column prop="name" label="服务名称" />
            <el-table-column prop="price" label="价格" width="120">
              <template #default="{ row }">
                <span class="price">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="时长(分钟)" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                  {{ row.status === 1 ? '上架' : '下架' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button 
                  size="small" 
                  :type="row.status === 1 ? 'warning' : 'success'"
                  @click="handleToggleStatus(row)"
                >
                  {{ row.status === 1 ? '下架' : '上架' }}
                </el-button>
                <el-button size="small" type="danger" @click="handleDelete(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty v-if="!loading && serviceItems.length === 0" description="暂无服务项目" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 添加服务对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="添加服务"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="100px">
        <el-form-item label="服务分类" prop="categoryId">
          <el-select v-model="addForm.categoryId" placeholder="请选择服务分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="服务描述" prop="description">
          <el-input
            v-model="addForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入服务描述"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="addForm.price"
            :min="0"
            :precision="2"
            :step="10"
            style="width: 100%"
            placeholder="请输入价格"
          />
        </el-form-item>
        <el-form-item label="时长(分钟)" prop="duration">
          <el-input-number
            v-model="addForm.duration"
            :min="1"
            :step="10"
            style="width: 100%"
            placeholder="请输入服务时长"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          确定
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 添加分类对话框 -->
    <el-dialog
      v-model="addCategoryDialogVisible"
      title="添加分类"
      width="500px"
      @close="handleCategoryDialogClose"
    >
      <el-form :model="addCategoryForm" :rules="addCategoryRules" ref="addCategoryFormRef" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="addCategoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="分类描述" prop="description">
          <el-input
            v-model="addCategoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number
            v-model="addCategoryForm.sort"
            :min="0"
            :step="1"
            style="width: 100%"
            placeholder="请输入排序值"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addCategoryDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCategorySubmit" :loading="categorySubmitting">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAllServiceItems, updateServiceItemStatus, createServiceItem, getCategories, createCategory, deleteCategory, getServiceItems, deleteServiceItem } from '@/api/service'

const loading = ref(false)
const activeTab = ref('categories') // 默认显示服务分类
const serviceItems = ref([])
const allServiceItems = ref([]) // 存储所有服务项目
const categories = ref([])
const currentCategoryId = ref(null) // 当前选中的分类ID
const addDialogVisible = ref(false)
const submitting = ref(false)
const addFormRef = ref(null)

// 添加分类相关
const addCategoryDialogVisible = ref(false)
const categorySubmitting = ref(false)
const addCategoryFormRef = ref(null)

const addForm = ref({
  categoryId: null,
  name: '',
  description: '',
  price: 0,
  duration: 60
})

const addRules = {
  categoryId: [{ required: true, message: '请选择服务分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  duration: [{ required: true, message: '请输入时长', trigger: 'blur' }]
}

const addCategoryForm = ref({
  name: '',
  description: '',
  sort: 0
})

const addCategoryRules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

// 计算属性：根据当前分类ID过滤服务项目
const filteredServiceItems = computed(() => {
  if (!currentCategoryId.value) {
    return allServiceItems.value // 如果没有选中分类，显示所有项目
  }
  return allServiceItems.value.filter(item => item.categoryId === currentCategoryId.value)
})

onMounted(() => {
  loadServiceItems()
  loadCategories()
})

const loadServiceItems = async () => {
  loading.value = true
  try {
    const res = await getAllServiceItems()
    allServiceItems.value = res.data || []
    // 如果有选中的分类，则过滤显示
    if (currentCategoryId.value) {
      serviceItems.value = allServiceItems.value.filter(item => item.categoryId === currentCategoryId.value)
    } else {
      serviceItems.value = allServiceItems.value
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  addDialogVisible.value = true
}

const handleSubmit = async () => {
  if (!addFormRef.value) return
  
  try {
    await addFormRef.value.validate()
    submitting.value = true
    await createServiceItem(addForm.value)
    ElMessage.success('添加成功')
    addDialogVisible.value = false
    loadServiceItems() // 重新加载列表
  } catch (error) {
    if (error !== false) { // 排除表单验证失败
      console.error(error)
      ElMessage.error('添加失败')
    }
  } finally {
    submitting.value = false
  }
}

const handleDialogClose = () => {
  // 重置表单
  addForm.value = {
    categoryId: null,
    name: '',
    description: '',
    price: 0,
    duration: 60
  }
  if (addFormRef.value) {
    addFormRef.value.clearValidate()
  }
}

const handleToggleStatus = async (item) => {
  const action = item.status === 1 ? '下架' : '上架'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}服务 "${item.name}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 切换状态
    const newStatus = item.status === 1 ? 0 : 1
    await updateServiceItemStatus(item.id, newStatus)
    
    ElMessage.success(`${action}成功`)
    loadServiceItems() // 重新加载列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除服务 "${item.name}" 吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteServiceItem(item.id)
    ElMessage.success('删除成功')
    
    // 重新加载列表
    loadServiceItems()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      ElMessage.error('删除失败')
    }
  }
}

// 添加分类相关函数
const handleAddCategory = () => {
  addCategoryDialogVisible.value = true
}

const handleCategorySubmit = async () => {
  if (!addCategoryFormRef.value) return
  
  try {
    await addCategoryFormRef.value.validate()
    categorySubmitting.value = true
    await createCategory(addCategoryForm.value)
    ElMessage.success('添加成功')
    addCategoryDialogVisible.value = false
    loadCategories() // 重新加载分类列表
  } catch (error) {
    if (error !== false) { // 排除表单验证失败
      console.error(error)
      ElMessage.error('添加失败')
    }
  } finally {
    categorySubmitting.value = false
  }
}

const handleCategoryDialogClose = () => {
  // 重置表单
  addCategoryForm.value = {
    name: '',
    description: '',
    sort: 0
  }
  if (addCategoryFormRef.value) {
    addCategoryFormRef.value.clearValidate()
  }
}

const handleDeleteCategory = async (category) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除分类 "${category.name}" 吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteCategory(category.id)
    ElMessage.success('删除成功')
    loadCategories() // 重新加载分类列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
      if (error.response?.data?.message) {
        ElMessage.error(error.response.data.message)
      } else {
        ElMessage.error('删除失败')
      }
    }
  }
}

const handleViewItems = (category) => {
  // 设置当前选中的分类ID
  currentCategoryId.value = category.id
  // 切换到服务项目标签页
  activeTab.value = 'items'
  // 过滤显示该分类下的项目
  serviceItems.value = allServiceItems.value.filter(item => item.categoryId === category.id)
  ElMessage.success(`已切换到分类：${category.name} 的服务项目`)
}

const handleShowAll = () => {
  // 清除分类过滤
  currentCategoryId.value = null
  // 显示所有服务项目
  serviceItems.value = allServiceItems.value
  ElMessage.info('已显示所有服务项目')
}
</script>

<style scoped>
.price {
  color: #f56c6c;
  font-weight: bold;
}
</style>
