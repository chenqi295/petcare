import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    console.error('Request error:', error)
    if (error.response?.status === 401) {
      ElMessage.error('未登录或登录已过期')
      const userStore = useUserStore()
      userStore.logout()
      window.location.href = '/login'
    } else if (error.response?.status === 404) {
      ElMessage.error('接口不存在')
    } else if (error.response?.status === 500) {
      ElMessage.error('服务器错误')
    } else {
      ElMessage.error(error.message || '网络错误，请检查后端服务是否启动')
    }
    return Promise.reject(error)
  }
)

export default request
