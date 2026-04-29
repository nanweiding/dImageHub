import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Admin from '../views/Admin.vue'
import UserProfile from '../views/UserProfile.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/admin', component: Admin },
  {
    path: '/profile',
    component: UserProfile,
    children: [
      { path: '', redirect: '/profile/overview' },
      { path: 'overview', component: () => import('../views/profile/AccountOverview.vue') },
      { path: 'account', component: () => import('../views/profile/AccountInfo.vue') },
      { path: 'images', component: () => import('../views/profile/ImageManagement.vue') },
      { path: 'quota', component: () => import('../views/profile/StorageQuota.vue') },
      { path: 'logs', component: () => import('../views/profile/UploadLogs.vue') },
      { path: 'security', component: () => import('../views/profile/SecuritySettings.vue') },
      { path: 'preferences', component: () => import('../views/profile/Preferences.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('userId')

  if (to.path === '/login' || to.path === '/register') {
    if (isLoggedIn) {
      next('/')
    } else {
      next()
    }
  } else {
    if (isLoggedIn) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
