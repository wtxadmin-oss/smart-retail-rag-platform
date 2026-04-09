import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import axios from 'axios'

const STORAGE_KEY = 'selectedStore'

export const useStoreSelectionStore = defineStore('store-selection', () => {
  const stores = ref([])
  const currentStore = ref(null)

  const currentStoreId = computed(() => currentStore.value?.id || null)

  function persist(store) {
    currentStore.value = store || null
    if (store) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(store))
    } else {
      localStorage.removeItem(STORAGE_KEY)
    }
  }

  function setCurrentStore(store) {
    persist(store)
  }

  async function fetchStores() {
    const res = await axios.get('/api/stores')
    stores.value = res.data?.data || []
    const saved = JSON.parse(localStorage.getItem(STORAGE_KEY) || 'null')
    if (saved?.id) {
      const matched = stores.value.find(item => item.id === saved.id)
      if (matched) {
        currentStore.value = matched
      }
    }
    if (!currentStore.value && stores.value.length > 0) {
      currentStore.value = stores.value[0]
      persist(stores.value[0])
    }
    return stores.value
  }

  async function ensureCurrentStore() {
    if (!stores.value.length) {
      await fetchStores()
    }
    return currentStore.value
  }

  function setCurrentStoreById(storeId) {
    const matched = stores.value.find(item => item.id === storeId)
    if (matched) {
      persist(matched)
    }
  }

  return {
    stores,
    currentStore,
    currentStoreId,
    fetchStores,
    ensureCurrentStore,
    setCurrentStore,
    setCurrentStoreById
  }
})
