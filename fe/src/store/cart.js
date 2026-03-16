import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from './user'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const userStore = useUserStore()
  const isLoggedIn = computed(() => userStore.isLoggedIn)
  const userId = computed(() => userStore.userInfo?.id)
  const getStorageKey = () => {
    try {
      const u = JSON.parse(localStorage.getItem('user') || 'null')
      const uid = u?.id || 'guest'
      return `cart:${uid}`
    } catch {
      return 'cart:guest'
    }
  }

  const totalCount = computed(() => items.value.reduce((acc, item) => acc + item.quantity, 0))
  const totalPrice = computed(() => items.value.reduce((acc, item) => acc + item.price * item.quantity, 0))

  function addToCart(product) {
    if (isLoggedIn.value && !userStore.isCustomer) {
      ElMessage.warning('管理员账号不支持购物车')
      return
    }
    if (isLoggedIn.value && userId.value) {
      const payload = {
        userId: userId.value,
        productId: product.id,
        productName: product.name,
        productImage: product.imageUrl || '',
        skuSpec: product.specName || '',
        price: product.price,
        quantity: 1
      }
      return axios.post('/api/cart/add', payload)
        .then(() => syncFromServer())
        .catch(() => {
          const existingItem = items.value.find(item => item.id === product.id && item.specName === product.specName)
          if (existingItem) {
            existingItem.quantity++
          } else {
            items.value.push({ ...product, quantity: 1 })
          }
          saveCart()
          ElMessage.warning('购物车服务暂不可用，已临时保存到本地')
        })
    } else {
      const existingItem = items.value.find(item => item.id === product.id && item.specName === product.specName)
      if (existingItem) {
        existingItem.quantity++
      } else {
        items.value.push({ ...product, quantity: 1 })
      }
      saveCart()
    }
  }

  function removeFromCart(idOrProductId) {
    if (isLoggedIn.value && !userStore.isCustomer) return
    if (isLoggedIn.value && userId.value) {
      return axios.delete(`/api/cart/${idOrProductId}`).then(() => syncFromServer())
    } else {
      items.value = items.value.filter(item => item.id !== idOrProductId)
      saveCart()
    }
  }

  function updateQuantity(idOrProductId, quantity) {
    if (isLoggedIn.value && !userStore.isCustomer) return
    if (isLoggedIn.value && userId.value) {
      return axios.put(`/api/cart/${idOrProductId}/quantity`, null, { params: { quantity } }).then(() => syncFromServer())
    } else {
      const item = items.value.find(i => i.id === idOrProductId)
      if (item) {
        item.quantity = quantity
        if (item.quantity <= 0) {
          removeFromCart(idOrProductId)
        }
      }
      saveCart()
    }
  }

  async function clearCart() {
    if (isLoggedIn.value && !userStore.isCustomer) return
    items.value = []
    localStorage.removeItem(getStorageKey())
    if (isLoggedIn.value && userId.value) {
      try {
        await axios.delete('/api/cart/clear', { params: { userId: userId.value } })
        await syncFromServer()
      } catch {
        ElMessage.warning('购物车服务暂不可用，已清空本地购物车')
      }
    }
  }

  function saveCart() {
    localStorage.setItem(getStorageKey(), JSON.stringify(items.value))
  }

  function syncFromServer() {
    if (!isLoggedIn.value || !userId.value) return
    return axios.get('/api/cart', { params: { userId: userId.value } })
      .then(res => {
        items.value = (res.data?.data || []).map(it => ({
          id: it.id,
          userId: it.userId,
          productId: it.productId,
          name: it.productName,
          imageUrl: it.productImage,
          specName: it.skuSpec,
          price: Number(it.price),
          quantity: it.quantity
        }))
      })
      .catch(() => {
        ElMessage.warning('购物车服务暂不可用，已回退到本地')
        const savedCart = localStorage.getItem(getStorageKey())
        items.value = savedCart ? JSON.parse(savedCart) : []
      })
  }

  async function syncAfterLogin() {
    const guest = JSON.parse(localStorage.getItem('cart:guest') || '[]')
    if (guest.length > 0 && userId.value) {
      for (const it of guest) {
        try {
          await axios.post('/api/cart/add', {
            userId: userId.value,
            productId: it.id,
            productName: it.name,
            productImage: it.imageUrl || '',
            skuSpec: it.specName || '',
            price: it.price,
            quantity: it.quantity
          })
        } catch {
          saveCart()
          ElMessage.warning('购物车服务暂不可用，已保留本地购物车')
          return
        }
      }
      localStorage.removeItem('cart:guest')
    }
    await syncFromServer()
  }

  if (isLoggedIn.value && userId.value) {
    syncFromServer()
  } else {
    const savedCart = localStorage.getItem(getStorageKey())
    if (savedCart) {
      items.value = JSON.parse(savedCart)
    }
  }

  return {
    items,
    totalCount,
    totalPrice,
    addToCart,
    removeFromCart,
    updateQuantity,
    clearCart,
    syncAfterLogin
  }
})
