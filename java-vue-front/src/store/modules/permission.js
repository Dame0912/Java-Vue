import { asyncRoutes, constantRoutes } from '@/router'
import { hasPermission } from '@/utils/permissionUtil'


/**
 * Filter asynchronous routing tables by recursion
 * @param asyncRoutes asyncRoutes
 * @param roles
 */
export function filterAsyncRoutes(asyncRoutes, roles) {
  const accessedRouters = asyncRoutes.filter(route => {
    if(hasPermission(roles, route)){
      if (route.children && route.children.length) {
        route.children = filterAsyncRoutes(route.children, roles)
      }
      return true
    }
    return false
  })
  return accessedRouters
}

const state = {
  routes: [],
  addRoutes: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  }
}

const actions = {
  generateRoutes({ commit }, roles) {
    return new Promise(resolve => {
      let accessedRoutes = filterAsyncRoutes(asyncRoutes, roles)
      accessedRoutes.push({ path: '*', redirect: '/404', hidden: true })
      commit('SET_ROUTES', accessedRoutes)
      resolve(accessedRoutes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
