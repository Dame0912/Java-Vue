import request from '@/utils/request'

export function organList() {
  return request({
    url: '/co/department',
    method: 'get'
  })
}

export function add(department) {
  return request({
    url: `/co/department`,
    method: 'post',
    data: department
  })
}

export function update(department) {
  return request({
    url: `/co/department/${department.id}`,
    method: 'put',
    data: department
  })
}

export function detail(departmentId) {
  return request({
    url: `/co/department/${departmentId}`,
    method: 'get'
  })
}

export function remove(departmentId) {
  return request({
    url: `/co/department/${departmentId}`,
    method: 'delete'
  })
}

export function saveOrUpdate(department) {
  return department.id ? update(department) : add(department)
}
