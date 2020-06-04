import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from '@/utils/auth' // get token from cookie
import getPageTitle from '@/utils/get-page-title'

NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['/login', '/auth-redirect'] // no redirect whitelist

router.beforeEach(async(to, from, next) => {
  // start progress bar
  NProgress.start()

  // set page title
  document.title = getPageTitle(to.meta.title)

  // determine whether the user has logged in
  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      if (store.getters.roles && store.getters.roles.length !== 0) { // 判断当前用户是否已拉取完user_info信息
        next()
      } else {
        try {
          const roles = await store.dispatch('user/getInfo')// 拉取user_info
          const accessRoutes = await store.dispatch('permission/generateRoutes', roles)// 根据roles权限生成可访问的路由表
          router.addRoutes(accessRoutes)// 动态添加可访问路由表

          // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
          next({ ...to, replace: true })
        } catch (error) {
          await store.dispatch('user/resetToken')
          //Message.error('验证失败, 请重新登录')
          next(`/login?redirect=${to.path}`)
        }
      }
    }
  } else { // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})
