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
      const user = JSON.parse(localStorage.getItem('user') || 'null')
      return `cart:${user?.id || 'guest'}`
    } catch {
      return 'cart:guest'
    }
  }

  const totalCount = computed(() => items.value.reduce((acc, item) => acc + item.quantity, 0))
  const totalPrice = computed(() => items.value.reduce((acc, item) => acc + item.price * item.quantity, 0))
  const storeId = computed(() => items.value[0]?.storeId || null)
  const storeName = computed(() => items.value[0]?.storeName || '')

  function getLocalItemId(productId, skuId, currentStoreId) {
    return `local-${productId}-${skuId || 'default'}-${currentStoreId || 'store'}`
  }

  function createLocalItem(product, quantity = 1) {
    const productId = product.productId || product.id
    const skuId = product.skuId || null
    const currentStoreId = product.storeId || null
    return {
      id: getLocalItemId(productId, skuId, currentStoreId),
      productId,
      skuId,
      storeId: currentStoreId,
      storeName: product.storeName || '',
      name: product.name,
      imageUrl: product.imageUrl || '',
      specName: product.specName || '',
      price: Number(product.price),
      quantity
    }
  }

  function normalizeCartItems(rawItems = []) {
    return rawItems.map(item => ({
      id: item.id || getLocalItemId(item.productId || item.id, item.skuId, item.storeId),
      userId: item.userId,
      productId: item.productId || item.id,
      skuId: item.skuId || null,
      storeId: item.storeId || null,
      storeName: item.storeName || '',
      name: item.name || item.productName,
      imageUrl: item.imageUrl || item.productImage || '',
      specName: item.specName || item.skuSpec || '',
      price: Number(item.price),
      quantity: item.quantity || 1
    }))
  }

  function findLocalItem(productId, skuId, currentStoreId) {
    return items.value.find(item => item.productId === productId && item.skuId === (skuId || null) && item.storeId === (currentStoreId || null))
  }

  function saveCart() {
    localStorage.setItem(getStorageKey(), JSON.stringify(items.value))
  }

  function resetCart() {
    items.value = []
  }

  function syncFromServer() {
    if (!isLoggedIn.value || !userId.value) {
      return Promise.resolve()
    }
    return axios.get('/api/cart', { params: { userId: userId.value } })
      .then((res) => {
        items.value = normalizeCartItems(res.data?.data || [])
        saveCart()
      })
      .catch(() => {
        ElMessage.warning('购物车同步失败，已回退到本地购物车')
        const savedCart = localStorage.getItem(getStorageKey())
        items.value = savedCart ? normalizeCartItems(JSON.parse(savedCart)) : []
      })
  }

  function addToCart(product) {
    if (isLoggedIn.value && !userStore.isCustomer) {
      ElMessage.warning('管理员账号不支持购物车')
      return Promise.resolve()
    }
    const productId = product.productId || product.id
    const skuId = product.skuId || null
    const currentStoreId = product.storeId || null
    if (!currentStoreId) {
      ElMessage.warning('请先选择门店')
      return Promise.resolve()
    }
    if (items.value.length > 0 && items.value[0].storeId !== currentStoreId) {
      ElMessage.warning('购物车暂不支持混合不同门店商品，请先清空当前购物车')
      return Promise.resolve()
    }
    if (isLoggedIn.value && userId.value) {
      return axios.post('/api/cart/add', { userId: userId.value, storeId: currentStoreId, productId, skuId, quantity: 1 })
        .then(() => syncFromServer())
        .catch(() => {
          const existingItem = findLocalItem(productId, skuId, currentStoreId)
          if (existingItem) existingItem.quantity += 1
          else items.value.push(createLocalItem(product))
          saveCart()
          ElMessage.warning('购物车同步失败，已暂存到本地')
        })
    }
    const existingItem = findLocalItem(productId, skuId, currentStoreId)
    if (existingItem) existingItem.quantity += 1
    else items.value.push(createLocalItem(product))
    saveCart()
    return Promise.resolve()
  }

  function removeFromCart(idOrProductId) {
    if (isLoggedIn.value && !userStore.isCustomer) return Promise.resolve()
    if (isLoggedIn.value && userId.value) return axios.delete(`/api/cart/${idOrProductId}`).then(() => syncFromServer())
    items.value = items.value.filter(item => item.id !== idOrProductId)
    saveCart()
    return Promise.resolve()
  }

  function updateQuantity(idOrProductId, quantity) {
    if (isLoggedIn.value && !userStore.isCustomer) return Promise.resolve()
    if (isLoggedIn.value && userId.value) return axios.put(`/api/cart/${idOrProductId}/quantity`, null, { params: { quantity } }).then(() => syncFromServer())
    const item = items.value.find(entry => entry.id === idOrProductId)
    if (item) {
      item.quantity = quantity
      if (item.quantity <= 0) return removeFromCart(idOrProductId)
    }
    saveCart()
    return Promise.resolve()
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
        ElMessage.warning('购物车清空失败，当前仅清除了本地购物车')
      }
    }
  }

  async function syncAfterLogin() {
    const guestItems = normalizeCartItems(JSON.parse(localStorage.getItem('cart:guest') || '[]'))
    if (guestItems.length > 0 && userId.value) {
      for (const item of guestItems) {
        try {
          await axios.post('/api/cart/add', { userId: userId.value, storeId: item.storeId, productId: item.productId, skuId: item.skuId, quantity: item.quantity })
        } catch {
          saveCart()
          ElMessage.warning('购物车同步失败，已保留本地购物车')
          return
        }
      }
      localStorage.removeItem('cart:guest')
    }
    await syncFromServer()
  }

  if (isLoggedIn.value && userId.value) syncFromServer()
  else {
    const savedCart = localStorage.getItem(getStorageKey())
    items.value = savedCart ? normalizeCartItems(JSON.parse(savedCart)) : []
  }

  return { items, storeId, storeName, totalCount, totalPrice, addToCart, removeFromCart, updateQuantity, clearCart, resetCart, syncAfterLogin }
})
