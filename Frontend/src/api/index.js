import axios from 'axios'

const api = axios.create({
  baseURL: '/api'
})

export const authAPI = {
  register(username, password) {
    return api.post('/auth/register', { username, password })
  },
  login(username, password) {
    return api.post('/auth/login', { username, password })
  }
}

export const imageAPI = {
  uploadImage(file, userId) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId)
    return api.post('/images/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  getUserImages(userId) {
    return api.get(`/images/user/${userId}`)
  },
  getImage(id) {
    return api.get(`/images/${id}`)
  },
  deleteImage(id, userId) {
    return api.delete(`/images/${id}?userId=${userId}`)
  }
}

export default api
