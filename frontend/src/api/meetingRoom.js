import api from './index'

export const getAllMeetingRooms = () => {
  return api.get('/meeting-rooms')
}

export const getAllMeetingRoomsForAdmin = () => {
  return api.get('/meeting-rooms/admin')
}

export const getMeetingRoom = (id) => {
  return api.get(`/meeting-rooms/${id}`)
}

export const createMeetingRoom = (data) => {
  return api.post('/meeting-rooms', data)
}

export const updateMeetingRoom = (id, data) => {
  return api.put(`/meeting-rooms/${id}`, data)
}

export const deleteMeetingRoom = (id) => {
  return api.delete(`/meeting-rooms/${id}`)
}
