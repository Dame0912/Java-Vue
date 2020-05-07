import request from '@/utils/request'


export function upload_dataUrl(data) {
  return request({
    url: '/upload/dataUrl',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
}

export function upload_oss_back(data) {
  return request({
    url: '/upload/oss/back',
    method: 'post',
    data
  })
}

export function upload_oss_policy() {
  return request({
    url: '/upload/oss/policy',
    method: 'post'
  })
}
