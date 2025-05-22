import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/manager/home', meta: { requiresAuth: true}},
    { path: '/manager',component: () => import('@/views/Manager.vue'), meta: { requiresAuth: true},
      children: [
        { path: 'home', meta: { name: '主页', meta: { requiresAuth: true} }, component: () => import('@/views/Home.vue')},
        { path: 'admin', meta: { name: '用户管理 / 管理员信息', meta: { requiresAuth: true} }, component: () => import('@/views/Admin.vue')},
        { path: 'user', meta: { name: '主页', meta: { requiresAuth: true} }, component: () => import('@/views/user/User.vue')}
      ]
    },
    { path: '/login', component: () => import('@/views/Login.vue'), meta: { requiresAuth: false} },
    { path: '/notFound', component: () => import('@/views/404.vue'), meta: { requiresAuth: false} },
    { path: '/:pathMatch(.*)', redirect: '/notFound' }
  ]
})

router.beforeEach((to, from, next) => {
  const userInfo = localStorage.getItem('code_user')

  if (to.meta.requiresAuth) {
    if (!userInfo) {
      ElMessage.warning('请先登录')
      next({ path: '/login'})
    } else {
      // 已登录，允许访问
      next()
    }
  } else {
    if (to.path === '/login' && userInfo) {
      next({ path: '/'})
    } else {
      next()
    }

  }
})
export default router