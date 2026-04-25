import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, getCurrentUser } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(null)
  const loading = ref(false)
  const error = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const userName = computed(() => user.value?.name || user.value?.username || '')

  const initializeAuth = async () => {
    if (token.value) {
      try {
        const response = await getCurrentUser()
        user.value = response.data
      } catch (err) {
        clearAuth()
      }
    }
  }

  const handleLogin = async (username, password) => {
    loading.value = true
    error.value = null
    try {
      const response = await login({ username, password })
      const { token: jwtToken, name, role } = response.data
      token.value = jwtToken
      user.value = { username, name, role }
      localStorage.setItem('token', jwtToken)
      localStorage.setItem('user', JSON.stringify(user.value))
      return true
    } catch (err) {
      error.value = err.message || '登录失败'
      return false
    } finally {
      loading.value = false
    }
  }

  const clearAuth = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const logout = () => {
    clearAuth()
  }

  const loadStoredUser = () => {
    const storedUser = localStorage.getItem('user')
    if (storedUser && token.value) {
      try {
        user.value = JSON.parse(storedUser)
      } catch (err) {
        user.value = null
      }
    }
  }

  return {
    token,
    user,
    loading,
    error,
    isAuthenticated,
    isAdmin,
    userName,
    initializeAuth,
    handleLogin,
    clearAuth,
    logout,
    loadStoredUser
  }
})
