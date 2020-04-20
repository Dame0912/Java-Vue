import request from '@/utils/request'

export function findAll(data) {
  return request({
    url: '/sys/perm/list',
    method: 'get',
    params: data
  })
}

export function detail(id) {
  return request({
    url: `/sys/perm/find/${id}`,
    method: 'get'
  })
}

export function add(data) {
  return request({
    url: '/sys/perm/save',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: `/sys/perm/update/${data.id}`,
    method: 'put',
    data
  })
}

export function remove(id) {
  return request({
    url: `/sys/perm/delete/${id}`,
    method: 'delete'
  })
}

export function saveOrUpdate(data) {
  return data.id ? update(data) : add(data)
}

export function findAssignPerms(data) {
  return request({
    url: `/sys/perm/assignPermNodes`,
    method: 'get',
    params: data
  })
}


