import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Admin from '../views/Admin.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/admin', component: Admin }
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
