<template>
  <div id="app">
    <nav v-if="authStore.isAuthenticated" class="navbar">
      <div class="navbar-content">
        <router-link to="/dashboard" class="navbar-brand">
          会议室预约系统
        </router-link>
        <div class="navbar-links">
          <router-link 
            to="/dashboard" 
            class="navbar-link"
            :class="{ active: $route.path === '/dashboard' }"
          >
            首页
          </router-link>
          <router-link 
            to="/booking" 
            class="navbar-link"
            :class="{ active: $route.path === '/booking' }"
          >
            预约面板
          </router-link>
          <router-link 
            to="/my-bookings" 
            class="navbar-link"
            :class="{ active: $route.path === '/my-bookings' }"
          >
            我的预约
          </router-link>
          <router-link 
            v-if="authStore.isAdmin"
            to="/meeting-rooms" 
            class="navbar-link"
            :class="{ active: $route.path === '/meeting-rooms' }"
          >
            会议室管理
          </router-link>
          <div class="navbar-user">
            <div class="user-info">
              <div class="user-name">{{ authStore.userName }}</div>
              <div class="user-role">{{ authStore.isAdmin ? '管理员' : '普通用户' }}</div>
            </div>
            <button class="btn btn-secondary" @click="handleLogout">
              退出
            </button>
          </div>
        </div>
      </div>
    </nav>
    <main :class="{ 'main-content': authStore.isAuthenticated }">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useAuthStore } from './store/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}
</script>
