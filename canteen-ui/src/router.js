// src/router.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from './views/LoginPage.vue'
import MenuPage from './views/MenuPage.vue'
import AdminDashboard from './views/AdminDashboard.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginPage },
  { path: '/menu', component: MenuPage },
  { 
    path: '/admin',
    component: AdminDashboard,
    meta: { requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// route guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAdmin && (!token || role !== 'ADMIN')) {
    next('/login')
  } else {
    next()
  }
})

export default router
