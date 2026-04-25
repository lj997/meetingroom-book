<template>
  <div class="container">
    <div class="page-header">
      <h1>会议室管理</h1>
      <button class="btn btn-primary" @click="openCreateModal">
        新增会议室
      </button>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="meetingRooms.length === 0" class="empty-state">
      <p>暂无会议室数据</p>
    </div>

    <div v-else>
      <div v-for="room in meetingRooms" :key="room.id" class="card" style="margin-bottom: 16px;">
        <div class="meeting-room-card" style="border: none; margin: 0; padding: 0;">
          <div class="room-info">
            <h3>{{ room.name }}</h3>
            <p>📍 位置：{{ room.location || '未设置' }}</p>
            <p>👥 容量：{{ room.capacity }} 人</p>
            <p>📦 设备：{{ room.equipment || '未设置' }}</p>
            <p v-if="room.description">📝 描述：{{ room.description }}</p>
          </div>
          <div style="display: flex; flex-direction: column; gap: 8px; align-items: flex-end;">
            <span class="room-status" :class="room.active ? 'active' : 'inactive'">
              {{ room.active ? '启用' : '停用' }}
            </span>
            <div style="display: flex; gap: 8px;">
              <button class="btn btn-secondary" @click="openEditModal(room)">
                编辑
              </button>
              <button 
                class="btn btn-danger" 
                @click="handleDelete(room)"
                :disabled="deleting === room.id"
              >
                {{ deleting === room.id ? '删除中...' : '删除' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑会议室' : '新增会议室' }}</h2>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>

        <div v-if="error" class="alert alert-error">
          {{ error }}
        </div>

        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label>会议室名称 *</label>
            <input
              v-model="form.name"
              type="text"
              class="form-control"
              placeholder="请输入会议室名称"
              required
            />
          </div>

          <div class="form-group">
            <label>位置</label>
            <input
              v-model="form.location"
              type="text"
              class="form-control"
              placeholder="请输入位置"
            />
          </div>

          <div class="form-group">
            <label>容量（人） *</label>
            <input
              v-model.number="form.capacity"
              type="number"
              class="form-control"
              placeholder="请输入容量"
              min="1"
              required
            />
          </div>

          <div class="form-group">
            <label>设备</label>
            <input
              v-model="form.equipment"
              type="text"
              class="form-control"
              placeholder="例如：投影仪, 白板, 视频会议系统"
            />
          </div>

          <div class="form-group">
            <label>描述</label>
            <textarea
              v-model="form.description"
              class="form-control"
              rows="3"
              placeholder="请输入描述"
            ></textarea>
          </div>

          <div class="form-group" v-if="isEdit">
            <label style="display: flex; align-items: center; gap: 8px;">
              <input
                v-model="form.active"
                type="checkbox"
                style="width: auto;"
              />
              启用会议室
            </label>
          </div>

          <div class="form-actions">
            <button 
              type="button" 
              class="btn btn-secondary" 
              @click="closeModal"
            >
              取消
            </button>
            <button 
              type="submit" 
              class="btn btn-primary"
              :disabled="submitting"
            >
              {{ submitting ? '提交中...' : '确认' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { 
  getAllMeetingRoomsForAdmin, 
  createMeetingRoom, 
  updateMeetingRoom,
  deleteMeetingRoom 
} from '../api/meetingRoom'

const meetingRooms = ref([])
const loading = ref(false)
const submitting = ref(false)
const deleting = ref(null)
const error = ref(null)

const showModal = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const form = reactive({
  name: '',
  location: '',
  capacity: '',
  equipment: '',
  description: '',
  active: true
})

const loadMeetingRooms = async () => {
  loading.value = true
  try {
    const response = await getAllMeetingRoomsForAdmin()
    meetingRooms.value = response.data
  } catch (err) {
    console.error('加载会议室失败:', err)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.name = ''
  form.location = ''
  form.capacity = ''
  form.equipment = ''
  form.description = ''
  form.active = true
  error.value = null
}

const openCreateModal = () => {
  resetForm()
  isEdit.value = false
  editId.value = null
  showModal.value = true
}

const openEditModal = (room) => {
  resetForm()
  isEdit.value = true
  editId.value = room.id
  form.name = room.name
  form.location = room.location || ''
  form.capacity = room.capacity
  form.equipment = room.equipment || ''
  form.description = room.description || ''
  form.active = room.active
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  resetForm()
}

const handleSubmit = async () => {
  error.value = null
  submitting.value = true

  try {
    const data = {
      name: form.name,
      location: form.location || undefined,
      capacity: Number(form.capacity),
      equipment: form.equipment || undefined,
      description: form.description || undefined,
      active: form.active
    }

    if (isEdit.value) {
      await updateMeetingRoom(editId.value, data)
    } else {
      await createMeetingRoom(data)
    }

    closeModal()
    loadMeetingRooms()
  } catch (err) {
    error.value = err.message || '操作失败'
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (room) => {
  if (!confirm(`确定要删除会议室"${room.name}"吗？\n\n注意：删除后已有的预约记录不会被删除，但该会议室将不可用。`)) {
    return
  }

  deleting.value = room.id
  try {
    await deleteMeetingRoom(room.id)
    loadMeetingRooms()
  } catch (err) {
    alert(err.message || '删除失败')
  } finally {
    deleting.value = null
  }
}

onMounted(() => {
  loadMeetingRooms()
})
</script>
