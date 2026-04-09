<template>
  <div class="map-wrapper">
    <Navbar />
    <div class="map-container">
      <div id="map" class="map-element"></div>
      <div v-if="mapFallbackVisible" class="map-fallback">
        <div class="fallback-card">
          <h3>{{ selectedStore?.name || '门店位置' }}</h3>
          <p>{{ selectedStore?.address || mapFallbackMessage }}</p>
          <div v-if="selectedStore?.lng != null && selectedStore?.lat != null" class="fallback-coords">
            <span>经度 {{ selectedStore.lng }}</span>
            <span>纬度 {{ selectedStore.lat }}</span>
          </div>
          <el-button v-if="selectedStore?.lng != null && selectedStore?.lat != null" type="primary" @click="openExternalMap(selectedStore)">打开地图查看位置</el-button>
        </div>
      </div>
      <div class="store-panel">
        <div class="panel-header">
          <h3>Nearby Stores</h3>
          <p>Find your nearest SmartCoffee</p>
        </div>
        <div class="store-list">
          <div v-for="store in stores" :key="store.id" class="store-item" @click="focusStore(store)">
            <div class="store-head">
              <h4>{{ store.name }}</h4>
              <el-tag v-if="selectedStoreState?.id === store.id" type="success" round>已选择</el-tag>
            </div>
            <p>{{ store.address }}</p>
            <div class="store-hours"><el-icon><Clock /></el-icon><span>{{ store.businessHours || '08:00 - 22:00' }}</span></div>
            <el-button type="primary" text @click.stop="chooseStore(store)">选择门店并去点单</el-button>
          </div>
          <el-empty v-if="stores.length === 0" description="No stores nearby" :image-size="60" />
        </div>
      </div>
      <div class="map-controls">
        <el-button class="control-btn" circle title="My Location" @click="locateMe"><el-icon><Location /></el-icon></el-button>
        <el-button class="control-btn" circle title="Zoom In" @click="zoomIn"><el-icon><Plus /></el-icon></el-button>
        <el-button class="control-btn" circle title="Zoom Out" @click="zoomOut"><el-icon><Minus /></el-icon></el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Clock, Location, Minus, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import Navbar from '../components/Navbar.vue'
import { useStoreSelectionStore } from '../store/store'

const router = useRouter()
const storeSelectionStore = useStoreSelectionStore()
const mapRef = ref(null)
const stores = ref([])
const selectedStore = ref(null)
const selectedStoreState = ref(null)
const mapFallbackVisible = ref(false)
const mapFallbackMessage = ref('当前地图不可用，请先从门店列表查看位置')
const amapKey = import.meta.env.VITE_AMAP_KEY || ''
const storeMarkers = []
let userMarker = null

const zoomIn = () => { if (mapRef.value) mapRef.value.zoomIn() }
const zoomOut = () => { if (mapRef.value) mapRef.value.zoomOut() }

const locateMe = () => {
  if (!mapRef.value || !navigator.geolocation) return
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      const lnglat = [pos.coords.longitude, pos.coords.latitude]
      mapRef.value.setZoomAndCenter(15, lnglat)
      if (userMarker) userMarker.setPosition(lnglat)
      else if (window.AMap) {
        userMarker = new window.AMap.Marker({ position: lnglat, anchor: 'bottom-center', title: '当前位置' })
        userMarker.setMap(mapRef.value)
      }
    },
    () => ElMessage.warning('定位失败，请检查浏览器定位权限'),
    { enableHighAccuracy: true, timeout: 8000, maximumAge: 0 }
  )
}

const focusStore = (store) => {
  selectedStore.value = store
  selectedStoreState.value = storeSelectionStore.currentStore
  if (mapRef.value && store.lng != null && store.lat != null) mapRef.value.setZoomAndCenter(16, [store.lng, store.lat])
}

const chooseStore = (store) => {
  storeSelectionStore.setCurrentStore(store)
  selectedStoreState.value = store
  ElMessage.success(`已选择门店：${store.name}`)
  router.push('/menu')
}

const fetchStores = async () => {
  try {
    stores.value = await storeSelectionStore.fetchStores()
    const validStores = stores.value.filter(store => store.lng != null && store.lat != null)
    selectedStoreState.value = storeSelectionStore.currentStore
    if (!selectedStore.value) selectedStore.value = storeSelectionStore.currentStore || validStores[0] || stores.value[0] || null
    return validStores
  } catch (error) {
    ElMessage.error('获取门店失败，请检查后端服务')
    console.error('Fetch stores failed', error)
    return []
  }
}

const enableFallback = (message) => {
  mapFallbackVisible.value = true
  mapFallbackMessage.value = message
}

const openExternalMap = (store) => {
  if (store?.lng == null || store?.lat == null) return
  const name = encodeURIComponent(store.name || 'SmartCoffee')
  window.open(`https://uri.amap.com/marker?position=${store.lng},${store.lat}&name=${name}`, '_blank')
}

const renderStoresOnMap = (map, validStores) => {
  mapFallbackVisible.value = false
  storeMarkers.splice(0, storeMarkers.length)
  validStores.forEach((store) => {
    const marker = new window.AMap.Marker({ position: [store.lng, store.lat], title: store.name, content: `<div style="background:#8B5A2B;color:#fff;padding:4px 12px;border-radius:20px;box-shadow:0 2px 6px rgba(0,0,0,0.2);font-size:12px;white-space:nowrap;">${store.name}</div>` })
    marker.setMap(map)
    storeMarkers.push(marker)
  })
  if (storeMarkers.length > 0) map.setFitView(storeMarkers)
}

onMounted(() => {
  fetchStores().then((validStores) => {
    if (!amapKey) {
      enableFallback('未配置地图 Key，已切换为列表定位模式')
      return
    }
    if (window.AMap) {
      const map = new window.AMap.Map('map', { center: [116.397428, 39.90923], zoom: 4, viewMode: '2D', mapStyle: 'amap://styles/whitesmoke' })
      mapRef.value = map
      renderStoresOnMap(map, validStores)
      return
    }
    const script = document.createElement('script')
    script.src = `https://webapi.amap.com/maps?v=2.0&key=${amapKey}`
    script.onload = () => {
      if (!window.AMap) {
        enableFallback('地图加载失败，请先查看门店列表')
        return
      }
      const map = new window.AMap.Map('map', { center: [116.397428, 39.90923], zoom: 4, viewMode: '2D', mapStyle: 'amap://styles/whitesmoke' })
      mapRef.value = map
      renderStoresOnMap(map, validStores)
    }
    script.onerror = () => enableFallback('地图脚本加载失败，请先查看门店列表')
    document.head.appendChild(script)
  })
})
</script>

<style scoped>
.map-wrapper { min-height: 100vh; display: flex; flex-direction: column; }
.map-container { flex: 1; position: relative; display: flex; }
.map-element { flex: 1; width: 100%; min-height: calc(100vh - 80px); background: #f0f2f5; }
.store-panel { position: absolute; top: 24px; left: 24px; width: 340px; max-height: calc(100vh - 140px); overflow: auto; padding: 20px; border-radius: 20px; background: rgba(255, 255, 255, 0.95); box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12); }
.store-list { display: flex; flex-direction: column; gap: 16px; }
.store-item { padding: 16px; border: 1px solid var(--el-border-color-light); border-radius: 14px; background: #fff; cursor: pointer; }
.store-head, .store-hours, .fallback-coords { display: flex; align-items: center; gap: 8px; }
.map-controls { position: absolute; right: 24px; bottom: 24px; display: flex; flex-direction: column; gap: 8px; }
.map-fallback { position: absolute; inset: 0; display: flex; align-items: center; justify-content: center; pointer-events: none; }
.fallback-card { pointer-events: auto; background: rgba(255, 255, 255, 0.96); padding: 24px; border-radius: 20px; box-shadow: 0 12px 30px rgba(0, 0, 0, 0.12); }
</style>
