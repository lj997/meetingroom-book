<template>
  <div class="container">
    <div class="page-header">
      <h1>我的预约</h1>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="bookings.length === 0" class="empty-state">
      <p>暂无预约记录</p>
      <p>
        <button class="btn btn-primary" @click="router.push('/booking')">
          去预约
        </button>
      </p>
    </div>

    <div v-else>
      <div v-for="booking in bookings" :key="booking.id" class="card" style="margin-bottom: 16px;">
        <div class="booking-header">
          <div class="booking-title">{{ booking.title }}</div>
          <span class="booking-status" :class="booking.status.toLowerCase()">
            {{ getStatusText(booking.status) }}
          </span>
        </div>

        <div class="booking-info">
          📍 会议室：{{ booking.meetingRoom?.name || '未知' }}
        </div>
        <div class="booking-info">
          📅 日期：{{ formatDate(booking.date) }}
        </div>
        <div class="booking-info">
          ⏰ 时间：{{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime) }}
        </div>
        <div class="booking-info" v-if="booking.description">
          📝 描述：{{ booking.description }}
        </div>

        <div class="booking-actions">
          <button
            v-if="canCancel(booking)"
            class="btn btn-danger"
            :disabled="cancelling === booking.id"
            @click="handleCancel(booking)"
          >
            {{ cancelling === booking.id ? '取消中...' : '取消预约' }}
          </button>
          <button
            v-else-if="booking.status === 'CONFIRMED' && !canCancel(booking)"
            class="btn btn-secondary"
            disabled
          >
            已开始，不可取消
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyBookings, cancelBooking } from '../api/booking'

const router = useRouter()

const bookings = ref([])
const loading = ref(false)
const cancelling = ref(null)

const loadMyBookings = async () => {
  loading.value = true
  try {
    const response = await getMyBookings()
    bookings.value = response.data
  } catch (err) {
    console.error('加载预约失败:', err)
  } finally {
    loading.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    'CONFIRMED': '已确认',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekday = ['日', '一', '二', '三', '四', '五', '六'][date.getDay()]
  return `${year}年${month}月${day}日 周${weekday}`
}

const formatTime = (time) => {
  return time
}

const canCancel = (booking) => {
  if (booking.status !== 'CONFIRMED') return false

  const now = new Date()
  const bookingDate = new Date(booking.date)
  const [hours, minutes] = booking.startTime.split(':').map(Number)

  bookingDate.setHours(hours, minutes, 0, 0)

  return now < bookingDate
}

const handleCancel = async (booking) => {
  if (!confirm('确定要取消这个预约吗？')) return

  cancelling.value = booking.id
  try {
    await cancelBooking(booking.id)
    loadMyBookings()
  } catch (err) {
    alert(err.message || '取消失败')
  } finally {
    cancelling.value = null
  }
}

onMounted(() => {
  loadMyBookings()
})
</script>
