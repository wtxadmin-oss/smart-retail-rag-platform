<template>
  <div class="map-wrapper">
    <Navbar />
    <div class="map-container">
      <div id="map" class="map-element"></div>
      <div v-if="mapFallbackVisible" class="map-fallback">
        <div class="fallback-card">
          <h3>{{ selectedStore?.name || '体验店位置' }}</h3>
          <p>{{ selectedStore?.address || mapFallbackMessage }}</p>
          <div v-if="selectedStore?.lng != null && selectedStore?.lat != null" class="fallback-coords">
            <span>经度 {{ selectedStore.lng }}</span>
            <span>纬度 {{ selectedStore.lat }}</span>
          </div>
          <el-button
            v-if="selectedStore?.lng != null && selectedStore?.lat != null"
            type="primary"
            @click="openExternalMap(selectedStore)"
          >
            打开地图查看位置
          </el-button>
        </div>
      </div>
      
      <!-- Store List Panel -->
      <div class="store-panel">
        <div class="panel-header">
          <h3>Nearby Stores</h3>
          <p>Find your nearest SmartCoffee</p>
        </div>
        <div class="store-list">
          <div v-for="store in stores" :key="store.id" class="store-item" @click="focusStore(store)">
            <h4>{{ store.name }}</h4>
            <p>{{ store.address }}</p>
            <div class="store-hours">
              <el-icon><Clock /></el-icon> {{ store.businessHours || '08:00 - 22:00' }}
            </div>
          </div>
          <el-empty v-if="stores.length === 0" description="No stores nearby" :image-size="60"></el-empty>
        </div>
      </div>

      <!-- Map Controls -->
      <div class="map-controls">
        <el-button class="control-btn" circle @click="locateMe" title="My Location">
          <el-icon><Location /></el-icon>
        </el-button>
        <el-button class="control-btn" circle @click="zoomIn" title="Zoom In">
          <el-icon><Plus /></el-icon>
        </el-button>
        <el-button class="control-btn" circle @click="zoomOut" title="Zoom Out">
          <el-icon><Minus /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Location, Plus, Minus, Clock } from '@element-plus/icons-vue'
import Navbar from '../components/Navbar.vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const mapRef = ref(null)
let userMarker = null
const amapKey = import.meta.env.VITE_AMAP_KEY || ''
const storeMarkers = []
const stores = ref([])
const selectedStore = ref(null)
const mapFallbackVisible = ref(false)
const mapFallbackMessage = ref('当前地图不可用，请先从门店列表查看位置')

// 地图放大控制按钮，调用地图实例的放大能力。
const zoomIn = () => {
  if (mapRef.value) mapRef.value.zoomIn()
}
// 地图缩小控制按钮，调用地图实例的缩小能力。
const zoomOut = () => {
  if (mapRef.value) mapRef.value.zoomOut()
}
// 获取浏览器定位，并把地图中心移动到用户当前位置。
const locateMe = () => {
  if (!mapRef.value) return
  if (!navigator.geolocation) {
    ElMessage.warning('浏览器不支持定位')
    return
  }
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      const lnglat = [pos.coords.longitude, pos.coords.latitude]
      mapRef.value.setZoomAndCenter(15, lnglat)
      if (userMarker) {
        userMarker.setPosition(lnglat)
      } else {
        userMarker = new window.AMap.Marker({
          position: lnglat,
          anchor: 'bottom-center',
          title: '当前位置'
        })
        userMarker.setMap(mapRef.value)
      }
    },
    () => {
      ElMessage.warning('定位失败，请检查浏览器定位权限')
    },
    { enableHighAccuracy: true, timeout: 8000, maximumAge: 0 }
  )
}

// 点击门店列表项后，聚焦到对应门店并在地图上居中显示。
const focusStore = (store) => {
  selectedStore.value = store
  if (mapRef.value && store.lng != null && store.lat != null) {
    mapRef.value.setZoomAndCenter(16, [store.lng, store.lat])
  }
}

// 拉取后端门店数据，并筛选出具备经纬度的门店供地图渲染。
const fetchStores = async () => {
  try {
    const res = await axios.get('/api/stores')
    stores.value = res.data?.data || []
    const validStores = stores.value.filter(store => store.lng != null && store.lat != null)
    if (!selectedStore.value) {
      selectedStore.value = validStores[0] || stores.value[0] || null
    }
    return validStores
  } catch (e) {
    ElMessage.error('获取门店失败，请检查后端服务')
    console.error('Fetch stores failed', e)
    return []
  }
}

// 当地图脚本或 Key 不可用时，切换到纯列表兜底模式。
const enableFallback = (message) => {
  mapFallbackVisible.value = true
  mapFallbackMessage.value = message
}

// 打开高德外链地图，让用户在新页面查看门店坐标。
const openExternalMap = (store) => {
  if (store?.lng == null || store?.lat == null) return
  const name = encodeURIComponent(store.name || 'SmartCoffee')
  const url = `https://uri.amap.com/marker?position=${store.lng},${store.lat}&name=${name}`
  window.open(url, '_blank')
}

// 把门店数据渲染成地图标记点，并自动调整到合适视野。
const renderStoresOnMap = (map, validStores) => {
  mapFallbackVisible.value = false
  storeMarkers.splice(0, storeMarkers.length)

  validStores.forEach((store) => {
    const marker = new window.AMap.Marker({
      position: [store.lng, store.lat],
      title: store.name,
      content: `<div style="background:#8B5A2B;color:#fff;padding:4px 12px;border-radius:20px;box-shadow:0 2px 6px rgba(0,0,0,0.2);font-size:12px;white-space:nowrap;">${store.name}</div>`
    })
    marker.setMap(map)
    storeMarkers.push(marker)
  })

  if (storeMarkers.length > 0) {
    map.setFitView(storeMarkers)
  } else {
    locateMe()
  }
}

onMounted(() => {
  fetchStores().then((validStores) => {
    if (!amapKey) {
      enableFallback('未配置地图 Key，已切换为列表定位模式')
      ElMessage.warning('未配置地图 Key，已切换为列表定位模式')
      return
    }

    if (window.AMap) {
      const map = new window.AMap.Map('map', {
        center: [116.397428, 39.90923],
        zoom: 4,
        viewMode: '2D',
        mapStyle: 'amap://styles/whitesmoke'
      })
      mapRef.value = map
      renderStoresOnMap(map, validStores)
      return
    }

    const script = document.createElement('script')
    script.src = 'https://webapi.amap.com/maps?v=2.0&key=' + amapKey
    script.onload = () => {
      if (!window.AMap) {
        enableFallback('地图加载失败，请先查看左侧门店列表')
        ElMessage.error('地图加载失败，请稍后重试')
        return
      }
      const map = new window.AMap.Map('map', {
        center: [116.397428, 39.90923],
        zoom: 4,
        viewMode: '2D',
        mapStyle: 'amap://styles/whitesmoke'
      })
      mapRef.value = map
      renderStoresOnMap(map, validStores)
    }
    script.onerror = () => {
      enableFallback('地图脚本加载失败，请先查看左侧门店列表')
      ElMessage.error('地图脚本加载失败')
    }
    document.head.appendChild(script)
  })
})
</script>

<style scoped>
.map-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.map-container {
  flex: 1;
  position: relative;
  display: flex;
  margin-top: 0;
}

.map-element {
  flex: 1;
  width: 100%;
  height: 100%;
  background: #f0f2f5;
  position: absolute;
  top: 0;
  left: 0;
}

.map-fallback {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: linear-gradient(135deg, #f5f2eb 0%, #fdfbf7 100%);
}

.fallback-card {
  width: min(420px, calc(100% - 48px));
  padding: 28px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 16px 40px rgba(44, 36, 27, 0.08);
  border: 1px solid var(--el-border-color-light);
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.fallback-card h3 {
  margin: 0;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.fallback-card p {
  margin: 0;
  color: var(--el-text-color-regular);
  line-height: 1.7;
}

.fallback-coords {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.store-panel {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 340px;
  height: calc(100% - 40px);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  z-index: 10;
  overflow: hidden;
  border: 1px solid var(--el-border-color-light);
}

@media (max-width: 768px) {
  .map-container {
    min-height: calc(100vh - 72px);
  }

  .store-panel {
    position: static;
    width: 100%;
    height: auto;
    margin: 16px;
  }

  .map-fallback {
    padding: 16px;
  }

  .map-controls {
    right: 16px;
    bottom: 16px;
  }
}


.panel-header {
  padding: 24px 20px;
  background: var(--el-color-primary);
  color: #fff;
}

.panel-header h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 600;
}

.panel-header p {
  margin: 0;
  font-size: 13px;
  opacity: 0.9;
}

.store-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.store-item {
  padding: 16px;
  margin-bottom: 12px;
  background: #fff;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.store-item:hover {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-7);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 90, 43, 0.1);
}

.store-item h4 {
  margin: 0 0 8px 0;
  color: var(--el-text-color-primary);
  font-size: 16px;
}

.store-item p {
  margin: 0 0 12px 0;
  color: var(--el-text-color-regular);
  font-size: 13px;
  line-height: 1.5;
}

.store-hours {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.map-controls {
  position: absolute;
  right: 24px;
  bottom: 40px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 10;
}

.control-btn {
  width: 44px;
  height: 44px;
  font-size: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background: #fff;
  border: none;
  color: var(--el-text-color-primary);
}

.control-btn:hover {
  color: var(--el-color-primary);
  background: var(--el-bg-color-page);
}

#map {
  background: #f0f2f5;
}
</style>
