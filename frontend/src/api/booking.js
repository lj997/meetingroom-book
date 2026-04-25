import api from './index'

export const getMyBookings = () => {
  return api.get('/bookings/my')
}

export const getBookingsByRoomAndDate = (roomId, date) => {
  return api.get(`/bookings/room/${roomId}/date/${date}`)
}

export const getBookingsByDate = (date) => {
  return api.get(`/bookings/date/${date}`)
}

export const getBooking = (id) => {
  return api.get(`/bookings/${id}`)
}

export const createBooking = (data) => {
  return api.post('/bookings', data)
}

export const cancelBooking = (id) => {
  return api.put(`/bookings/${id}/cancel`)
}
