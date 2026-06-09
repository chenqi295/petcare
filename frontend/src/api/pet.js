import request from '@/utils/request'

// 获取我的宠物列表
export function getMyPets() {
  return request.get('/pet/my-pets')
}

// 添加宠物
export function addPet(data) {
  return request.post('/pet/add', data)
}

// 更新宠物
export function updatePet(data) {
  return request.put('/pet/update', data)
}

// 删除宠物
export function deletePet(id) {
  return request.delete(`/pet/delete/${id}`)
}

// 上传宠物照片
export function uploadPetPhoto(formData) {
  return request.post('/upload/pet-photo', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
