<template>
  <div class="container">
    <div class="page-header">
      <h1>预约面板</h1>
      <button class="btn btn-primary" @click="showCreateModal = true">
        新建预约
      </button>
    </div>

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 24px;">
      <div class="calendar-container">
        <div class="calendar-header">
          <h2>{{ currentMonthLabel }}</h2>
          <div class="calendar-nav">
            <button class="btn btn-secondary" @click="prevMonth">◀</button>
            <button class="btn btn-secondary" @click="nextMonth">▶</button>
          </div>
        </div>

        <div class="calendar-weekdays">
          <div v-for="day in weekdays" :key="day" class="calendar-weekday">
            {{ day }}
          </div>
        </div>

        <div class="calendar-days">
          <div
            v-for="(day, index) in calendarDays"
            :key="index"
            class="calendar-day"
            :class="{
              'other-month': !day.isCurrentMonth,
              'today': day.isToday,
              'selected': day.isSelected
            }"
            @click="selectDate(day)"
          >
            <span>{{ day.date.getDate() }}</span>
            <span v-if="day.bookingCount > 0" class="booking-count">
              {{ day.bookingCount }} 个预约
            </span>
          </div>
        </div>
      </div>

      <div>
        <div class="card">
          <div class="time-slots-header">
            <h3>
              {{ selectedDateLabel }} - 预约情况
            </h3>
          </div>

          <div class="room-selector" v-if="meetingRooms.length > 0">
            <div
              v-for="room in meetingRooms"
              :key="room.id"
              class="room-selector-item"
              :class="{ selected: selectedRoomId === room.id }"
              @click="selectedRoomId = room.id"
            >
              {{ room.name }}
            </div>
          </div>

          <div v-if="loadingBookings" class="loading">加载中...</div>
          <div v-else-if="bookings.length === 0" class="empty-state">
            <p>该日期暂无预约</p>
          </div>
          <div v-else>
            <div v-for="booking in bookings" :key="booking.id" class="booking-card">
              <div class="booking-header">
                <div class="booking-title">{{ booking.title || '未知会议' }}</div>
                <span class="booking-status" :class="booking.status?.toLowerCase() || ''">
                  {{ booking.status === 'CONFIRMED' ? '已确认' : booking.status === 'CANCELLED' ? '已取消' : '' }}
                </span>
              </div>
              <div class="booking-info">
                📍 会议室：{{ booking.meetingRoom?.name || '未知' }}
              </div>
              <div class="booking-info">
                ⏰ 时间：{{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime) }}
              </div>
              <div class="booking-info" v-if="booking.description">
                📝 描述：{{ booking.description }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showCreateModal" class="modal-overlay" @click.self="showCreateModal = false">
      <div class="modal">
        <div class="modal-header">
          <h2>新建预约</h2>
          <button class="modal-close" @click="showCreateModal = false">&times;</button>
        </div>

        <div v-if="createError" class="alert alert-error">
          {{ createError }}
        </div>

        <div v-if="createSuccess" class="alert alert-success">
          预约成功！
        </div>

        <form @submit.prevent="handleCreateBooking">
          <div class="form-group">
            <label>会议室 *</label>
            <select v-model="createForm.meetingRoomId" class="form-control" required>
              <option value="">请选择会议室</option>
              <option v-for="room in meetingRooms" :key="room.id" :value="room.id">
                {{ room.name }} ({{ room.capacity }}人)
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>会议主题 *</label>
            <input
              v-model="createForm.title"
              type="text"
              class="form-control"
              placeholder="请输入会议主题"
              required
            />
          </div>

          <div class="form-group">
            <label>日期 *</label>
            <input
              v-model="createForm.date"
              type="date"
              class="form-control"
              required
            />
          </div>

          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 16px;">
            <div class="form-group">
              <label>开始时间 *</label>
              <select v-model="createForm.startTime" class="form-control" required>
                <option value="">请选择</option>
                <option v-for="time in timeSlots" :key="time" :value="time">
                  {{ time }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>结束时间 *</label>
              <select v-model="createForm.endTime" class="form-control" required>
                <option value="">请选择</option>
                <option v-for="time in timeSlots" :key="time" :value="time">
                  {{ time }}
                </option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label>描述</label>
            <textarea
              v-model="createForm.description"
              class="form-control"
              rows="3"
              placeholder="请输入会议描述（可选）"
            ></textarea>
          </div>

          <p style="font-size: 12px; color: #6b7280; margin-bottom: 16px;">
            ⚠️ 预约最小单位为30分钟，例如：09:00-09:30、09:30-10:00 等
          </p>

          <div class="form-actions">
            <button 
              type="button" 
              class="btn btn-secondary" 
              @click="showCreateModal = false"
            >
              取消
            </button>
            <button 
              type="submit" 
              class="btn btn-primary"
              :disabled="createLoading"
            >
              {{ createLoading ? '提交中...' : '确认预约' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, getCurrentScope } from 'vue'
import { getAllMeetingRooms } from '../api/meetingRoom'
import { getBookingsByRoomAndDate, getBookingsByDate, createBooking } from '../api/booking'

const meetingRooms = ref([])
const bookings = ref([])
const loadingBookings = ref(false)

const showCreateModal = ref(false)
const createLoading = ref(false)
const createError = ref(null)
const createSuccess = ref(false)

const currentDate = ref(new Date())
const selectedDate = ref(new Date())
const selectedRoomId = ref(null)

let successTimeoutId = null

const weekdays = ['日', '一', '二', '三', '四', '五', '六']

const timeSlots = [
  '08:00', '08:30', '09:00', '09:30', '10:00', '10:30',
  '11:00', '11:30', '12:00', '12:30', '13:00', '13:30',
  '14:00', '14:30', '15:00', '15:30', '16:00', '16:30',
  '17:00', '17:30', '18:00', '18:30', '19:00', '19:30', '20:00'
]

const createForm = ref({
  meetingRoomId: '',
  title: '',
  date: '',
  startTime: '',
  endTime: '',
  description: ''
})

const currentMonthLabel = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth() + 1
  return `${year}年${month}月`
})

const selectedDateLabel = computed(() => {
  const date = selectedDate.value
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${year}年${month}月${day}日`
})

const calendarDays = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const startDay = firstDay.getDay()
  const totalDays = lastDay.getDate()

  const days = []

  const prevMonthLastDay = new Date(year, month, 0).getDate()
  for (let i = startDay - 1; i >= 0; i--) {
    const date = new Date(year, month - 1, prevMonthLastDay - i)
    days.push({
      date,
      isCurrentMonth: false,
      isToday: isSameDay(date, today),
      isSelected: isSameDay(date, selectedDate.value),
      bookingCount: 0
    })
  }

  for (let i = 1; i <= totalDays; i++) {
    const date = new Date(year, month, i)
    days.push({
      date,
      isCurrentMonth: true,
      isToday: isSameDay(date, today),
      isSelected: isSameDay(date, selectedDate.value),
      bookingCount: 0
    })
  }

  const remainingDays = 42 - days.length
  for (let i = 1; i <= remainingDays; i++) {
    const date = new Date(year, month + 1, i)
    days.push({
      date,
      isCurrentMonth: false,
      isToday: isSameDay(date, today),
      isSelected: isSameDay(date, selectedDate.value),
      bookingCount: 0
    })
  }

  return days
})

const isSameDay = (date1, date2) => {
  return date1.getFullYear() === date2.getFullYear() &&
         date1.getMonth() === date2.getMonth() &&
         date1.getDate() === date2.getDate()
}

const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatTime = (time) => {
  return time
}

const prevMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
}

const nextMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
}

const selectDate = (day) => {
  selectedDate.value = day.date
}

const loadBookings = async () => {
  if (!selectedRoomId.value) return
  
  loadingBookings.value = true
  try {
    const dateStr = formatDate(selectedDate.value)
    const response = await getBookingsByRoomAndDate(selectedRoomId.value, dateStr)
    bookings.value = response.data.filter(b => b.status === 'CONFIRMED')
  } catch (err) {
    console.error('加载预约失败:', err)
    bookings.value = []
  } finally {
    loadingBookings.value = false
  }
}

const loadMeetingRooms = async () => {
  try {
    const response = await getAllMeetingRooms()
    meetingRooms.value = response.data
    if (meetingRooms.value.length > 0) {
      selectedRoomId.value = meetingRooms.value[0].id
    }
  } catch (err) {
    console.error('加载会议室失败:', err)
  }
}

const handleCreateBooking = async () => {
  createError.value = null
  createSuccess.value = false

  if (createForm.value.startTime >= createForm.value.endTime) {
    createError.value = '结束时间必须晚于开始时间'
    return
  }

  createLoading.value = true
  try {
    await createBooking({
      meetingRoomId: Number(createForm.value.meetingRoomId),
      title: createForm.value.title,
      date: createForm.value.date,
      startTime: createForm.value.startTime,
      endTime: createForm.value.endTime,
      description: createForm.value.description || undefined
    })
    createSuccess.value = true
    loadBookings()
    
    if (successTimeoutId) {
      clearTimeout(successTimeoutId)
    }
    successTimeoutId = setTimeout(() => {
      showCreateModal.value = false
      resetCreateForm()
      successTimeoutId = null
    }, 1500)
  } catch (err) {
    createError.value = err.message || '预约失败'
  } finally {
    createLoading.value = false
  }
}

const resetCreateForm = () => {
  createForm.value = {
    meetingRoomId: meetingRooms.value[0]?.id || '',
    title: '',
    date: formatDate(selectedDate.value),
    startTime: '',
    endTime: '',
    description: ''
  }
  createError.value = null
  createSuccess.value = false
}

watch(selectedRoomId, () => {
  loadBookings()
})

watch(selectedDate, () => {
  loadBookings()
})

onMounted(() => {
  loadMeetingRooms()
  createForm.value.date = formatDate(selectedDate.value)
})

onUnmounted(() => {
  if (successTimeoutId) {
    clearTimeout(successTimeoutId)
    successTimeoutId = null
  }
})

watch(showCreateModal, (val) => {
  if (val) {
    resetCreateForm()
  }
})
</script>
