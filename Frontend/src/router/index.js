import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for auth protection
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('userId')

  // Allow access to login and register routes
  if (to.path === '/login' || to.path === '/register') {
    // If already logged in, redirect to home
    if (isLoggedIn) {
      next('/')
    } else {
      next()
    }
  } else {
    // For other routes, require auth
    if (isLoggedIn) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router