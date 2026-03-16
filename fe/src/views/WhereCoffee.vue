<template>
  <div class="common-layout">
    <el-container direction="vertical" style="height: 100vh;">
      <el-header style="display:flex; align-items:center; gap:12px; border-bottom: 1px solid #dcdfe6;">
        <span id="logo" style="cursor:pointer;" @click="router.push('/')">
          <img src="/static/picture/logo.jpg" alt="logo" style="height:40px;">
        </span>
        <h2 style="margin:0;">SmartCoffee</h2>
        <span style="opacity:.7;">智能咖啡系统</span>
      </el-header>
      <el-main style="flex: 1; display: flex; position: relative; padding: 0;">
        <Sidebar active="wherecoffee" />
        <div style="flex: 1; position: relative;">
          <div id="map" style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;"></div>
          <div style="position: absolute; top: 20px; right: 20px; z-index: 1000; display: flex; flex-direction: column; gap: 8px;">
            <el-button type="primary" circle @click="locateMe">
              <el-icon><Location /></el-icon>
            </el-button>
            <el-button type="info" circle @click="zoomIn">
              <el-icon><Plus /></el-icon>
            </el-button>
            <el-button type="info" circle @click="zoomOut">
              <el-icon><Minus /></el-icon>
            </el-button>
          </div>
        </div>
      </el-main>
      <Footer />
    </el-container>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Plus, Minus } from '@element-plus/icons-vue'
import Footer from '../components/Footer.vue'
import Sidebar from '../components/Sidebar.vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const mapRef = ref(null)
let userMarker = null
const amapKey = import.meta.env.VITE_AMAP_KEY || ''
const storeMarkers = []

const zoomIn = () => {
  if (mapRef.value) mapRef.value.zoomIn()
}
const zoomOut = () => {
  if (mapRef.value) mapRef.value.zoomOut()
}
const locateMe = () => {
  if (!mapRef.value) return
  if (!navigator.geolocation) {
    alert('浏览器不支持定位')
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
      alert('定位失败，请检查浏览器定位权限')
    },
    { enableHighAccuracy: true, timeout: 8000, maximumAge: 0 }
  )
}

onMounted(() => {
  if (!amapKey) {
    console.error('AMap key is missing. Please set VITE_AMAP_KEY in .env file.')
    return
  }
  const s = document.createElement('script')
  s.src = 'https://webapi.amap.com/maps?v=2.0&key=' + amapKey
  s.onload = () => {
    if (!window.AMap) {
      console.error('AMap global not found');
      return;
    }
    const map = new window.AMap.Map('map', {
      center: [116.397428, 39.90923],
      zoom: 4,
      viewMode: '2D'
    })
    mapRef.value = map

    axios.get('/api/stores').then((res) => {
      const stores = res.data.data || []
      stores.forEach((store) => {
        if (store.lng == null || store.lat == null) return
        const marker = new window.AMap.Marker({
          position: [store.lng, store.lat],
          title: store.name,
          map: map
        })
        storeMarkers.push(marker)
        marker.on('click', () => {
          alert(`${store.name}\n${store.address}`)
        })
      })
      if (stores.length > 0) {
        const first = stores.find(s => s.lng != null && s.lat != null)
        if (first) map.setZoomAndCenter(12, [first.lng, first.lat])
      }
    }).catch((e) => {
      ElMessage.error('获取门店失败，请检查后端服务')
      console.error('Fetch stores failed', e)
    })
  }
  document.head.appendChild(s)
})
</script>

<style scoped>
#map {
  background: #f0f2f5;
}
</style>
