import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request.post('/user/login', data)
}

// 用户注册
export function register(data) {
  return request.post('/user/register', data)
}

// 获取用户信息
export function getUserInfo() {
  return request.get('/user/info')
}

// 更新用户信息
export function updateUserInfo(data) {
  return request.put('/user/update', data)
}

// 修改密码
export function changePassword(data) {
  return request.post('/user/change-password', data)
}

// 通过手机号重置密码（忘记密码）
export function resetPassword(data) {
  return request.post('/user/reset-password', data)
}

// 获取所有用户列表（管理端）
export function getAllUsers() {
  return request.get('/user/list')
}

// 获取非管理员用户数量（用于仪表台统计）
export function getNonAdminUserCount() {
  return request.get('/user/count/non-admin')
}

// 更新用户信息（管理端）
export function updateUser(id, data) {
  return request.put(`/user/update/${id}`, data)
}

// 上传头像
export function uploadAvatar(formData) {
  return request.post('/upload/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
