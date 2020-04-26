import request from '@/utils/request'


export function findAllPage(data) {
  return request({
    url: '/sys/user/list',
    method: 'get',
    params: data
  })
}

export function getAll() {
  return request({
    url: '/sys/user/all',
    method: 'get'
  })
}

export function findById(userId) {
  return request({
    url: `/sys/user/find/${userId}`,
    method: 'get'
  })
}

export function save(data) {
  return request({
    url: '/sys/user/save',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: `/sys/user/update/${data.id}`,
    method: 'put',
    data
  })
}

export function saveOrUpdate(data) {
  return data.id ? update(data) : save(data)
}

export function remove(userId) {
  return request({
    url: `/sys/user/delete/${userId}`,
    method: 'delete'
  })
}

export function findUserRole(userId) {
  return request({
    url: `/sys/user/role/${userId}`,
    method: 'get'
  })
}

export function assignRoles(data) {
  return request({
    url: `/sys/user/assign/roles`,
    method: 'put',
    data
  })
}

export function profileInfo() {
  return request({
    url: `/sys/user/profile`,
    method: 'post',
  })
}

export function editPassWord(data) {
  return request({
    url: `/sys/user/edit/pwd`,
    method: 'put',
    data
  })
}

export function login(data) {
  return request({
    url: `/sys/user/login`,
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    url: `/sys/user/login/info`,
    method: 'post'
  })
}

export function logout() {
  return request({
    url: '/sys/user/logout',
    method: 'post'
  })
}
