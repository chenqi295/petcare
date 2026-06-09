import request from '@/utils/request'

// 创建评价
export function createReview(data) {
  return request.post('/review/create', data)
}

// 获取服务的评价列表
export function getServiceReviews(serviceId) {
  return request.get(`/review/service/${serviceId}`)
}

// 获取技师的评价列表
export function getTechnicianReviews(technicianId) {
  return request.get(`/review/technician/${technicianId}`)
}

// 获取我的评价列表
export function getMyReviews() {
  return request.get('/review/my-reviews')
}
