<template>
  <div class="form-card">
    <div class="card">
      <h2>注册</h2>
      
      <div v-if="error" class="alert alert-error">
        {{ error }}
      </div>

      <div v-if="success" class="alert alert-success">
        注册成功！请登录
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="username">用户名 *</label>
          <input
            id="username"
            v-model="form.username"
            type="text"
            class="form-control"
            placeholder="请输入用户名 (3-50字符)"
            required
            minlength="3"
            maxlength="50"
          />
        </div>

        <div class="form-group">
          <label for="name">姓名</label>
          <input
            id="name"
            v-model="form.name"
            type="text"
            class="form-control"
            placeholder="请输入姓名"
          />
        </div>

        <div class="form-group">
          <label for="email">邮箱 *</label>
          <input
            id="email"
            v-model="form.email"
            type="email"
            class="form-control"
            placeholder="请输入邮箱"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">密码 *</label>
          <input
            id="password"
            v-model="form.password"
            type="password"
            class="form-control"
            placeholder="请输入密码 (至少6位)"
            required
            minlength="6"
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">确认密码 *</label>
          <input
            id="confirmPassword"
            v-model="form.confirmPassword"
            type="password"
            class="form-control"
            placeholder="请再次输入密码"
            required
          />
        </div>

        <div class="form-actions">
          <button 
            type="submit" 
            class="btn btn-primary"
            :disabled="loading || success"
          >
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </div>
      </form>

      <div class="form-footer">
        已有账号？
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/auth'

const router = useRouter()

const form = reactive({
  username: '',
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const error = ref(null)
const success = ref(false)

const handleSubmit = async () => {
  error.value = null
  
  if (form.password !== form.confirmPassword) {
    error.value = '两次输入的密码不一致'
    return
  }

  loading.value = true

  try {
    await register({
      username: form.username,
      name: form.name,
      email: form.email,
      password: form.password
    })
    success.value = true
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (err) {
    error.value = err.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>
