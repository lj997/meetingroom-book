<template>
  <div class="form-card">
    <div class="card">
      <h2>登录</h2>
      
      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            class="form-control"
            placeholder="请输入用户名"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            class="form-control"
            placeholder="请输入密码"
            required
          />
        </div>

        <div class="form-actions">
          <button 
            type="submit" 
            class="btn btn-primary"
            :disabled="loading"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </div>
      </form>

      <div class="form-footer">
        还没有账号？
        <router-link to="/register">立即注册</router-link>
      </div>

      <div class="form-footer" style="margin-top: 20px; padding-top: 16px; border-top: 1px solid #e5e7eb;">
        <p style="font-size: 12px; color: #9ca3af;">
          测试账号：admin / admin123 (管理员)
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../store/auth'

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const form = reactive({
  username: '',
  password: ''
})

const loading = ref(false)
const error = ref(null)

const handleSubmit = async () => {
  loading.value = true
  error.value = null

  const success = await authStore.handleLogin(form.username, form.password)
  
  if (success) {
    const redirect = route.query.redirect || '/dashboard'
    router.push(redirect)
  } else {
    error.value = authStore.error || '登录失败'
  }

  loading.value = false
}
</script>
