<template>
  <div class="container">
    <div class="page-header">
      <h1>欢迎，{{ authStore.userName }}</h1>
    </div>

    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px;">
      <div class="card" @click="router.push('/booking')" style="cursor: pointer;">
        <h3 style="margin-bottom: 12px; font-size: 18px;">📅 预约面板</h3>
        <p style="color: #6b7280; font-size: 14px;">查看会议室预约情况，预约新的会议室</p>
      </div>

      <div class="card" @click="router.push('/my-bookings')" style="cursor: pointer;">
        <h3 style="margin-bottom: 12px; font-size: 18px;">📋 我的预约</h3>
        <p style="color: #6b7280; font-size: 14px;">查看和管理您的预约记录</p>
      </div>

      <div 
        v-if="authStore.isAdmin" 
        class="card" 
        @click="router.push('/meeting-rooms')" 
        style="cursor: pointer;"
      >
        <h3 style="margin-bottom: 12px; font-size: 18px;">🏢 会议室管理</h3>
        <p style="color: #6b7280; font-size: 14px;">管理会议室资源（新增/编辑/删除）</p>
      </div>
    </div>

    <div style="margin-top: 32px;">
      <div class="card">
        <h3 style="margin-bottom: 16px; font-size: 18px;">会议室列表</h3>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="meetingRooms.length === 0" class="empty-state">
          暂无会议室数据
        </div>
        <div v-else>
          <div v-for="room in meetingRooms" :key="room.id" class="meeting-room-card">
            <div class="room-info">
              <h3>{{ room.name }}</h3>
              <p>📍 位置：{{ room.location || '未设置' }}</p>
              <p>👥 容量：{{ room.capacity }} 人</p>
              <p>📦 设备：{{ room.equipment || '未设置' }}</p>
            </div>
            <div>
              <span class="room-status active">可用</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/auth'
import { getAllMeetingRooms } from '../api/meetingRoom'

const authStore = useAuthStore()
const router = useRouter()

const meetingRooms = ref([])
const loading = ref(false)

const loadMeetingRooms = async () => {
  loading.value = true
  try {
    const response = await getAllMeetingRooms()
    meetingRooms.value = response.data
  } catch (err) {
    console.error('加载会议室失败:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadMeetingRooms()
})
</script>
