<template>
  <div class="common-layout">
    <el-container style="height: 100vh;">
      <el-header style="display:flex; align-items:center; gap:12px; border-bottom: 1px solid #dcdfe6;">
        <span id="logo" style="cursor:pointer;" @click="goHome">
          <img src="/static/picture/logo.jpg" alt="logo" style="height:40px;">
        </span>
        <h2 style="margin:0;">SmartCoffee</h2>
        <span style="opacity:.7;">智能咖啡系统</span>
      </el-header>
      <el-container style="overflow: hidden;">
        <Sidebar active="home" />
        <el-main>
          <p v-if="userStore.isLoggedIn">欢迎 {{ userStore.userInfo.username }} 使用 SmartCoffee。</p>
          <p v-else>欢迎使用 SmartCoffee。</p>
          <el-carousel :interval="4000" type="card" height="400px">
            <el-carousel-item v-for="item in 6" :key="item" :class="'bg-' + item">
              <h3 style="position: absolute; top: 12px; right: 16px; font-size: 15px; color: #475669; opacity: 0.9;">广告 {{ item }}</h3>
            </el-carousel-item>
          </el-carousel>
          
          <el-row :gutter="20">
            <el-col :span="10">
              <img src="/static/picture/xuanchuan1.jpg" alt="xuanchuan1" style="height:200px; width:100%; object-fit:cover;">
            </el-col>
            <el-col :span="6">
              <img src="/static/picture/xuanchuan2.jpg" alt="xuanchuan2" style="height:200px; width:100%; object-fit:cover;">
            </el-col>
            <el-col :span="8">
              <img src="/static/picture/xuanchuan3.jpg" alt="xuanchuan3" style="height:200px; width:100%; object-fit:cover;">
            </el-col>
          </el-row>
          
          <h1 style="text-align:center;">咖啡豆工艺</h1>
          
          <el-row :gutter="20">
            <el-col :span="8"><img src="/static/picture/xuanchuan4.jpg" alt="xuanchuan4" style="height:200px; width:100%; object-fit:cover;"></el-col>
            <el-col :span="8"><img src="/static/picture/xuanchuan5.jpg" alt="xuanchuan5" style="height:200px; width:100%; object-fit:cover;"></el-col>
            <el-col :span="8"><img src="/static/picture/xuanchuan6.jpg" alt="xuanchuan6" style="height:200px; width:100%; object-fit:cover;"></el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="8"><img src="/static/picture/xuanchuan7.jpg" alt="xuanchuan7" style="height:200px; width:100%; object-fit:cover;"></el-col>
            <el-col :span="8"><img src="/static/picture/xuanchuan8.jpg" alt="xuanchuan8" style="height:200px; width:100%; object-fit:cover;"></el-col>
            <el-col :span="8"><img src="/static/picture/xuanchuan9.jpg" alt="xuanchuan9" style="height:200px; width:100%; object-fit:cover;"></el-col>
          </el-row>
          
          <el-row :gutter="20">
            <el-col :span="12"><img src="/static/picture/xuanchuan7.jpg" alt="xuanchuan10" style="height:200px; width:100%; object-fit:cover;"></el-col>
            <el-col :span="12"><img src="/static/picture/xuanchuan8.jpg" alt="xuanchuan11" style="height:200px; width:100%; object-fit:cover;"></el-col>
          </el-row>

          <Footer />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '../components/Footer.vue'
import Sidebar from '../components/Sidebar.vue'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
const active = ref("1")

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.el-carousel__item {
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.bg-1 { background-image: url('/static/picture/background1.jpg'); }
.bg-2 { background-image: url('/static/picture/background2.jpg'); }
.bg-3 { background-image: url('/static/picture/background3.jpg'); }
.bg-4 { background-image: url('/static/picture/background4.jpg'); }
.bg-5 { background-image: url('/static/picture/background5.jpg'); }
.bg-6 { background-image: url('/static/picture/background6.jpg'); }

.el-row {
  margin-bottom: 20px;
}
</style>
