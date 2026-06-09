import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import Layout from '@/components/Layout.vue'
import AdminLayout from '@/components/AdminLayout.vue'
import TechnicianLayout from '@/components/TechnicianLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPassword.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/technician/:technicianId/reviews',
    name: 'TechnicianReviewsPublic',
    component: () => import('@/views/TechnicianReviews.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: 'services',
        name: 'Services',
        component: () => import('@/views/Services.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: 'pets',
        name: 'Pets',
        component: () => import('@/views/Pets.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'technicians',
        name: 'Technicians',
        component: () => import('@/views/Technicians.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: 'appointments',
        name: 'Appointments',
        component: () => import('@/views/Appointments.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/Orders.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'reviews',
        name: 'Reviews',
        component: () => import('@/views/Reviews.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  // 管理员后台路由
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue')
      },
      {
        path: 'appointments',
        name: 'AdminAppointments',
        component: () => import('@/views/admin/Appointments.vue')
      },
      {
        path: 'orders',
        name: 'AdminOrders',
        component: () => import('@/views/admin/Orders.vue')
      },
      {
        path: 'services',
        name: 'AdminServices',
        component: () => import('@/views/admin/Services.vue')
      },
      {
        path: 'technicians',
        name: 'AdminTechnicians',
        component: () => import('@/views/admin/Technicians.vue')
      }
    ]
  },
  // 服务人员端（技师端）路由
  {
    path: '/technician',
    component: TechnicianLayout,
    meta: { requiresAuth: true, requiresTechnician: true },
    children: [
      {
        path: '',
        name: 'TechnicianDashboard',
        component: () => import('@/views/technician/Dashboard.vue')
      },
      {
        path: 'orders',
        name: 'TechnicianOrders',
        component: () => import('@/views/technician/Orders.vue')
      },
      {
        path: 'appointments',
        name: 'TechnicianAppointments',
        component: () => import('@/views/technician/Appointments.vue')
      },
      {
        path: 'history',
        name: 'TechnicianHistory',
        component: () => import('@/views/technician/History.vue')
      },
      {
        path: 'reviews',
        name: 'TechnicianReviews',
        component: () => import('@/views/technician/Reviews.vue')
      },
      {
        path: 'profile',
        name: 'TechnicianProfile',
        component: () => import('@/views/technician/Profile.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 检查Token是否过期
  if (userStore.token && userStore.isTokenExpired()) {
    console.warn('Token已过期，清除登录状态')
    userStore.logout()
    // 保留记住的密码信息，不清除
    next('/login')
    return
  }
  
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.meta.requiresAdmin && userStore.userInfo?.role !== 'ADMIN') {
    // 如果不是管理员，跳转到首页
    next('/')
  } else if (to.meta.requiresTechnician && userStore.userInfo?.role !== 'TECHNICIAN') {
    // 如果不是服务人员，跳转到首页
    next('/')
  } else if (userStore.userInfo?.role === 'TECHNICIAN' && to.path.startsWith('/technician')) {
    // 服务人员已经在技师端，允许访问
    next()
  } else if (userStore.userInfo?.role === 'TECHNICIAN' && !to.path.startsWith('/technician') && to.path !== '/login' && to.path !== '/register') {
    // 服务人员尝试访问普通用户前台，重定向到技师工作台
    next('/technician')
  } else if (userStore.userInfo?.role === 'ADMIN' && to.path.startsWith('/admin')) {
    // 管理员在管理后台，允许访问
    next()
  } else if (userStore.userInfo?.role === 'ADMIN' && !to.path.startsWith('/admin') && to.path !== '/login' && to.path !== '/register') {
    // 管理员尝试访问其他页面，保持在管理后台
    next('/admin')
  } else {
    next()
  }
})

export default router
