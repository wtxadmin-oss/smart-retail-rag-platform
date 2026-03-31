import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/wherecoffee',
    name: 'WhereCoffee',
    component: () => import('../views/WhereCoffee.vue')
  },
  {
    path: '/menu',
    name: 'Menu',
    component: () => import('../views/Menu.vue')
  },
  {
    path: '/customer',
    name: 'Customer',
    component: () => import('../views/Customer.vue')
  },
  {
    path: '/brand',
    name: 'BrandStory',
    component: () => import('../views/BrandStory.vue')
  },
  {
    path: '/membership',
    name: 'Membership',
    component: () => import('../views/Membership.vue')
  },
  {
    path: '/wiki',
    name: 'CoffeeWiki',
    component: () => import('../views/CoffeeWiki.vue')
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/Cart.vue'),
    meta: { forbidAdmin: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/Orders.vue'),
    meta: { requiresAuth: true, forbidAdmin: true }
  },
  // Admin Routes
  {
    path: '/admin/users',
    name: 'UserManagement',
    component: () => import('../views/UserManagement.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/menu',
    name: 'MenuManagement',
    component: () => import('../views/MenuManagement.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/stores',
    name: 'StoreManagement',
    component: () => import('../views/StoreManagement.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/specs',
    name: 'SpecManagement',
    component: () => import('../views/SpecManagement.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  },
  {
    path: '/admin/orders',
    name: 'AdminOrders',
    component: () => import('../views/Orders.vue'),
    meta: { requiresAuth: true, role: 'admin' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const roleUpper = (userStore.userInfo?.role || '').toUpperCase()
  const isAdmin = roleUpper === 'ADMIN'
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.forbidAdmin && isAdmin) {
    next('/')
  } else if (to.meta.role && roleUpper !== to.meta.role.toUpperCase()) {
    next('/')
  } else {
    next()
  }
})

export default router
