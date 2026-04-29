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
  uploadImage(formData) {
    return api.post('/images/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  getUserImages(userId, sortBy = 'uploadTime', sortOrder = 'desc', page = 1, pageSize = 20) {
    return api.get(`/images/user/${userId}`, { params: { sortBy, sortOrder, page, pageSize } })
  },
  getImage(id) {
    return api.get(`/images/${id}`)
  },
  deleteImage(id, userId) {
    return api.delete(`/images/${id}?userId=${userId}`)
  },
  moveToFolder(imageId, folderId, userId) {
    return api.put(`/images/${imageId}/move?folderId=${folderId}&userId=${userId}`)
  },
  updateImage(id, userId, filename, remark, folderId) {
    return api.put(`/images/${id}?userId=${userId}`, null, { params: { filename, remark, folderId } })
  },
  getTrashImages(userId) {
    return api.get(`/images/trash/${userId}`)
  },
  restoreImage(id, userId) {
    return api.put(`/images/${id}/restore?userId=${userId}`)
  },
  permanentDeleteImage(id, userId) {
    return api.delete(`/images/${id}/permanent?userId=${userId}`)
  },
  emptyTrash(userId) {
    return api.delete(`/images/trash/${userId}`)
  },
  getStats() {
    return api.get('/images/admin/stats')
  },
  getAllImages() {
    return api.get('/images/admin/all')
  },
  batchDelete(imageIds, userId) {
    return api.put('/images/batch/delete', { imageIds, userId })
  },
  batchMove(imageIds, folderId, userId) {
    return api.put('/images/batch/move', { imageIds, folderId, userId })
  }
}

export const folderAPI = {
  getUserFolders(userId) {
    return api.get(`/folders/user/${userId}`)
  },
  createFolder(name, userId) {
    return api.post('/folders', { name, userId })
  },
  deleteFolder(folderId, userId) {
    return api.delete(`/folders/${folderId}?userId=${userId}`)
  }
}

export const adminAPI = {
  getUsers() {
    return api.get('/admin/users')
  },
  deleteUser(userId) {
    return api.delete(`/admin/users/${userId}`)
  },
  getStats() {
    return api.get('/admin/stats')
  }
}

export const tagAPI = {
  createTag(userId, name, color) {
    return api.post('/tags', null, { params: { userId, name, color } })
  },
  getUserTags(userId) {
    return api.get(`/tags/user/${userId}`)
  },
  updateTag(tagId, userId, name, color) {
    return api.put(`/tags/${tagId}`, null, { params: { userId, name, color } })
  },
  deleteTag(tagId, userId) {
    return api.delete(`/tags/${tagId}`, { params: { userId } })
  },
  addTagToImage(imageId, tagId, userId) {
    return api.post(`/images/${imageId}/tags`, null, { params: { tagId, userId } })
  },
  removeTagFromImage(imageId, tagId, userId) {
    return api.delete(`/images/${imageId}/tags/${tagId}`, { params: { userId } })
  },
  getImagesByTags(tagIds, userId) {
    return api.get(`/images/tags/${tagIds.join(',')}`, { params: { userId } })
  },
  getTagStats(userId) {
    return api.get(`/tags/stats/${userId}`)
  }
}

export const userAPI = {
  getUserInfo(userId) {
    return api.get(`/user/${userId}/info`)
  },
  getStorageStats(userId) {
    return api.get(`/user/${userId}/storage`)
  },
  updateUserInfo(userId, data) {
    return api.put(`/user/${userId}/info`, data)
  }
}

export const settingsAPI = {
  getSettings(userId) {
    return api.get(`/user/${userId}/settings`)
  },
  updateSettings(userId, data) {
    return api.put(`/user/${userId}/settings`, data)
  }
}

export const logsAPI = {
  getUploadLogs(userId, range = 'all') {
    return api.get(`/user/${userId}/upload-logs`, { params: { range } })
  },
  getSecurityLogs(userId) {
    return api.get(`/user/${userId}/security-logs`)
  }
}

export const deviceAPI = {
  getDevices(userId) {
    return api.get(`/user/${userId}/devices`)
  },
  logoutDevice(deviceId) {
    return api.delete(`/user/devices/${deviceId}`)
  }
}

export const imageAPI = {
  uploadImage(formData) {
    return api.post('/images/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  getUserImages(userId, sortBy = 'uploadTime', sortOrder = 'desc', page = 1, pageSize = 20) {
    return api.get(`/images/user/${userId}`, { params: { sortBy, sortOrder, page, pageSize } })
  },
  getImage(id) {
    return api.get(`/images/${id}`)
  },
  deleteImage(id, userId) {
    return api.delete(`/images/${id}?userId=${userId}`)
  },
  moveToFolder(imageId, folderId, userId) {
    return api.put(`/images/${imageId}/move?folderId=${folderId}&userId=${userId}`)
  },
  updateImage(id, userId, filename, remark, folderId) {
    return api.put(`/images/${id}?userId=${userId}`, null, { params: { filename, remark, folderId } })
  },
  getTrashImages(userId) {
    return api.get(`/images/trash/${userId}`)
  },
  restoreImage(id, userId) {
    return api.put(`/images/${id}/restore?userId=${userId}`)
  },
  permanentDeleteImage(id, userId) {
    return api.delete(`/images/${id}/permanent?userId=${userId}`)
  },
  emptyTrash(userId) {
    return api.delete(`/images/trash/${userId}`)
  },
  getStats() {
    return api.get('/images/admin/stats')
  },
  getAllImages() {
    return api.get('/images/admin/all')
  },
  batchDelete(imageIds, userId) {
    return api.put('/images/batch/delete', { imageIds, userId })
  },
  batchMove(imageIds, folderId, userId) {
    return api.put('/images/batch/move', { imageIds, folderId, userId })
  },
  updatePermission(imageId, data) {
    return api.put(`/images/${imageId}/permission`, data)
  }
}

export default api