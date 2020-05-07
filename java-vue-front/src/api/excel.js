import request from '@/utils/request'

export function findAllPage(data) {
  return request({
    url: '/excel/user/page',
    method: 'get',
    params: data
  })
}


export function exportExcel() {
  return request({
    url: '/excel/user/export',
    method: 'get',
    responseType: 'blob'
  })
}

export function upload_oss_excel_policy() {
  return request({
    url: '/excel/user/style/oss/policy',
    method: 'post'
  })
}


export function exportStyleExcel() {
  return request({
    url: '/excel/user/style/export',
    method: 'get',
    responseType: 'blob'
  })
}


