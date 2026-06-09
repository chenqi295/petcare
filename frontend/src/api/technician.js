import request from '@/utils/request'

// 获取技师信息
export function getTechnicianInfo() {
  return request.get('/technician/info')
}

// 更新技师信息
export function updateTechnicianInfo(data) {
  return request.put('/technician/update', data)
}

// 更新工作状态
export function updateStatus(status) {
  return request.post('/technician/status', { status })
}

// 获取工作台统计数据
export function getDashboard() {
  return request.get('/technician/dashboard')
}

// 获取订单列表
export function getOrders(status) {
  return request.get('/technician/orders', { params: { status } })
}

// 获取预约列表
export function getAppointments(status) {
  // 如果status是数组，转换为逗号分隔的字符串
  const statusParam = Array.isArray(status) ? status.join(',') : status
  return request.get('/technician/appointments', { params: { status: statusParam } })
}

// 获取服务历史记录
export function getHistory() {
  return request.get('/technician/history')
}

// 获取技师的评价列表
export function getTechnicianReviews() {
  return request.get('/technician/reviews')
}

// 获取所有技师列表（管理端）
export function getAllTechnicians() {
  return request.get('/technician/admin/list')
}

// 获取所有技师列表（公开接口）
export function getAllTechniciansPublic() {
  return request.get('/technician/all')
}

// 获取指定技师的评价列表（公开接口）
export function getTechnicianReviewsPublic(technicianId) {
  return request.get(`/technician/${technicianId}/reviews/public`)
}

// 更新技师账号状态（启用/禁用）- 管理端
export function updateTechnicianAccountStatus(id, status) {
  return request.put(`/technician/admin/${id}/status`, { status })
}

// 上传头像
export function uploadAvatar(formData) {
  return request.post('/upload/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
