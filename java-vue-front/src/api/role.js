import request from '@/utils/request'

export function list(data) {
  return request({
    url: '/sys/role/list',
    method: 'get',
    params: data
  })
}

export function add(data) {
  return request({
    url: '/sys/role/save',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: `/sys/role/update/${data.id}`,
    method: 'put',
    data
  })
}

export function saveOrUpdate(data){
  return data.id ? update(data) : add(data)
}

export function detail(id) {
  return request({
    url: `/sys/role/find/${id}`,
    method: 'get'
  })
}

export function remove(id) {
  return request({
    url: `/sys/role/delete/${id}`,
    method: 'delete'
  })
}

export function findAll() {
  return request({
    url: `/sys/role/all`,
    method: 'get'
  })
}

export function assignPerm(data) {
  return request({
    url: `/sys/role/assign/perms`,
    method: 'put',
    data
  })
}


