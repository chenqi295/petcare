import request from '@/utils/request'

// 创建预约
export function createAppointment(data) {
  return request.post('/appointment/create', data)
}

// 获取我的预约列表
export function getMyAppointments() {
  return request.get('/appointment/my-appointments')
}

// 取消预约
export function cancelAppointment(id, reason) {
  return request.post(`/appointment/cancel/${id}`, null, { params: { reason } })
}

// 确认预约（技师）
export function confirmAppointment(id) {
  return request.post(`/appointment/confirm/${id}`)
}

// 拒绝预约（技师）
export function rejectAppointment(id, reason) {
  return request.post(`/appointment/reject/${id}`, null, { params: { reason } })
}

// 开始服务（技师）
export function startService(id) {
  return request.post(`/appointment/start/${id}`)
}

// 完成服务（技师）
export function completeService(id) {
  return request.post(`/appointment/complete/${id}`)
}

// 获取所有预约列表（管理端）
export function getAllAppointments() {
  return request.get('/appointment/list')
}
