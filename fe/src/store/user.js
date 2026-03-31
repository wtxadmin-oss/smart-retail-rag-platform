import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null) // { username, role, id, ... }
  
  const isLoggedIn = computed(() => !!userInfo.value)
  const roleUpper = computed(() => (userInfo.value?.role || '').toUpperCase())
  const isAdmin = computed(() => roleUpper.value === 'ADMIN')
  const isCustomer = computed(() => roleUpper.value === 'CUSTOMER')

  function setUser(user) {
    userInfo.value = user
    localStorage.setItem('user', JSON.stringify(user))
  }

  function clearUser() {
    userInfo.value = null
    localStorage.removeItem('user')
  }

  // User-008: 退出登录时清空购物车内存
  function logout() {
    clearUser()
    // 动态引入避免循环依赖
    import('./cart').then(({ useCartStore }) => {
      useCartStore().resetCart()
    })
  }

  // Initialize from localStorage
  const savedUser = localStorage.getItem('user')
  if (savedUser) {
    userInfo.value = JSON.parse(savedUser)
  }

  return {
    userInfo,
    isLoggedIn,
    isAdmin,
    isCustomer,
    setUser,
    clearUser,
    logout
  }
})
