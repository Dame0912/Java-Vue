import Vue from 'vue'
import Router from 'vue-router'
/* Layout */
import Layout from '@/layout'
/* Router Modules */

Vue.use(Router)


/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: 'Profile', icon: 'user', noCache: true }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [

  {
    path: '/settings',
    component: Layout,
    redirect: 'noredirect',
    name: 'settings',
    meta: { title: '公司设置', icon: 'set' },
    root: true,
    children: [
      {
        path: 'department',
        name: 'settings-department',
        component: () => import('@/views/department/index'),
        meta: { title: '部门管理', icon: 'tree', noCache: true }
      },
      {
        path: 'user',
        name: 'settings-user',
        component: () => import('@/views/user/index'),
        meta: { title: '用户管理', icon: 'user', noCache: true }
      },
      {
        path: 'role',
        name: 'settings-role',
        component: () => import('@/views/role/index'),
        meta: { title: '角色管理', icon: 'peoples', noCache: true }
      },
      {
        path: 'permission',
        name: 'settings-permission',
        component: () => import('@/views/permission/index'),
        meta: { title: '权限管理', icon: 'lock', noCache: true }
      },
    ]
  },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes,
  mode: 'history'
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
