import request from '@/utils/request'

// 获取我的订单列表
export function getMyOrders() {
  return request.get('/order/my-orders')
}

// 支付订单
export function payOrder(id, payMethod = 'ALIPAY') {
  return request.post(`/order/pay/${id}`, null, { params: { payMethod } })
}

// 取消订单
export function cancelOrder(id) {
  return request.post(`/order/cancel/${id}`)
}

// 获取所有订单列表（管理端）
export function getAllOrders() {
  return request.get('/order/list')
}

// 获取收入统计（管理端）
export function getRevenueStats() {
  return request.get('/order/revenue-stats')
}

// 获取近七天的收入统计（管理端）
export function getSevenDayRevenue() {
  return request.get('/order/seven-day-revenue')
}
