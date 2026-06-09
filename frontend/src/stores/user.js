import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const tokenExpireTime = ref(parseInt(localStorage.getItem('tokenExpireTime') || '0'))

  function setToken(newToken, expireTime) {
    token.value = newToken
    localStorage.setItem('token', newToken)
    // 保存Token过期时间（当前时间 + 24小时）
    if (expireTime) {
      tokenExpireTime.value = expireTime
      localStorage.setItem('tokenExpireTime', expireTime.toString())
    } else {
      // 默认24小时
      tokenExpireTime.value = Date.now() + 24 * 60 * 60 * 1000
      localStorage.setItem('tokenExpireTime', tokenExpireTime.value.toString())
    }
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    tokenExpireTime.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('tokenExpireTime')
  }

  // 检查Token是否过期
  function isTokenExpired() {
    if (!token.value) return true
    return Date.now() > tokenExpireTime.value
  }

  return { token, userInfo, tokenExpireTime, setToken, setUserInfo, logout, isTokenExpired }
})
