import request from '@/utils/request'

// 获取服务分类
export function getCategories() {
  return request.get('/service/categories')
}

// 获取服务项目
export function getServiceItems(categoryId) {
  return request.get(`/service/items/${categoryId}`)
}

// 获取热门服务
export function getHotServices(limit = 6) {
  return request.get('/service/hot', { params: { limit } })
}

// 获取服务详情
export function getServiceDetail(id) {
  return request.get(`/service/detail/${id}`)
}

// 获取技师列表
export function getTechnicians() {
  return request.get('/technician/list')
}

// 获取所有服务项目（管理端）
export function getAllServiceItems() {
  return request.get('/service/items/all')
}

// 更新服务项目状态（上架/下架）
export function updateServiceItemStatus(id, status) {
  return request.put(`/service/items/${id}/status`, { status })
}

// 创建服务项目
export function createServiceItem(data) {
  return request.post('/service/items', data)
}

// 创建服务分类
export function createCategory(data) {
  return request.post('/service/categories', data)
}

// 删除服务分类
export function deleteCategory(id) {
  return request.delete(`/service/categories/${id}`)
}

// 删除服务项目
export function deleteServiceItem(id) {
  return request.delete(`/service/items/${id}`)
}
