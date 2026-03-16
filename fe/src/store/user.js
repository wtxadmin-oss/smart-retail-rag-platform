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
    clearUser
  }
})
