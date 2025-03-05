import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/home/index.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/admin',
    component: () => import('../views/Admin.vue'),
    children: [
      {
        path: 'users',
        component: () => import('../views/admin/UserManagement.vue')
      },
      {
        path: 'books',
        component: () => import('../views/admin/BookManagement.vue')
      }
    ],
    meta: { requiresAdmin: true }
  },
  {
    path: '/super-admin',
    component: () => import('../views/SuperAdmin.vue'),
    children: [
      {
        path: '',
        redirect: '/super-admin/users'
      },
      {
        path: 'users',
        component: () => import('../views/super-admin/UserManagement.vue')
      },
      {
        path: 'books',
        component: () => import('../views/admin/BookManagement.vue')
      }
    ],
    meta: { requiresSuperAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
    return
  }

  if ((to.meta.requiresAdmin && (!isLoggedIn || role !== 'admin')) ||
      (to.meta.requiresSuperAdmin && (!isLoggedIn || role !== 'super_admin'))) {
    next('/login')
  } else {
    next()
  }
})

export default router 