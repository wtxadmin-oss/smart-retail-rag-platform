<template>
  <div class="home-wrapper">
    <Navbar />
    
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1>Discover True Coffee Craft</h1>
        <p>每一杯都是精选产地与大师烘焙的完美邂逅</p>
        <el-button type="primary" size="large" class="hero-btn" @click="router.push('/menu')">开始点单</el-button>
      </div>
      <div class="hero-overlay"></div>
      <img src="/static/picture/background1.jpg" alt="Hero Background" class="hero-bg">
    </section>

    <div class="page-container">
      <div class="welcome-text" v-if="userStore.isLoggedIn">
        <h2>欢迎回来, {{ userStore.userInfo.username }}</h2>
        <p>今天想喝点什么？</p>
      </div>

      <!-- Feature Banners -->
      <el-row :gutter="24" class="feature-row">
        <el-col :span="10">
          <div class="feature-card large" @click="router.push('/menu?category=1')">
            <img src="/static/picture/xuanchuan1.jpg" alt="Feature 1">
            <div class="feature-text">
              <h3>新品推荐</h3>
              <p>探索当季限定风味</p>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-card" @click="router.push('/menu')">
            <img src="/static/picture/xuanchuan2.jpg" alt="Feature 2">
            <div class="feature-text">
              <h3>经典意式</h3>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="feature-card" @click="router.push('/wherecoffee')">
            <img src="/static/picture/xuanchuan3.jpg" alt="Feature 3">
            <div class="feature-text">
              <h3>附近门店</h3>
              <p>找到离您最近的咖啡馆</p>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <!-- Craft Section -->
      <div class="section-header">
        <h2>极致工艺</h2>
        <p>从豆子到杯子的每一步，我们都精益求精</p>
      </div>
      
      <el-row :gutter="24" class="craft-row">
        <el-col :span="8">
          <div class="craft-card">
            <img src="/static/picture/xuanchuan4.jpg" alt="Craft 1">
            <h4>产地直采</h4>
            <p>寻觅世界各地优质咖啡庄园</p>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="craft-card">
            <img src="/static/picture/xuanchuan5.jpg" alt="Craft 2">
            <h4>匠心烘焙</h4>
            <p>激发每一种豆子的独特风味</p>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="craft-card">
            <img src="/static/picture/xuanchuan6.jpg" alt="Craft 3">
            <h4>鲜萃艺术</h4>
            <p>精准控温，还原最纯粹的口感</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <Footer />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
import { useUserStore } from '../store/user'

const router = useRouter()
const userStore = useUserStore()
</script>

<style scoped>
.home-wrapper {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.hero-section {
  position: relative;
  height: 80vh;
  min-height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin-top: 70px;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(rgba(0,0,0,0.3), rgba(0,0,0,0.6));
  z-index: 2;
}

.hero-content {
  position: relative;
  z-index: 3;
  text-align: center;
  color: #fff;
  padding: 0 20px;
}

.hero-content h1 {
  font-size: 56px;
  font-weight: 700;
  margin-bottom: 16px;
  font-family: serif;
  letter-spacing: 2px;
  text-shadow: 0 4px 12px rgba(0,0,0,0.3);
}

.hero-content p {
  font-size: 20px;
  margin-bottom: 40px;
  font-weight: 300;
  letter-spacing: 1px;
}

.hero-btn {
  padding: 12px 40px;
  font-size: 18px;
  border-radius: 30px;
  transition: transform 0.3s;
}

.hero-btn:hover {
  transform: translateY(-2px);
}

.welcome-text {
  text-align: center;
  margin-bottom: 40px;
}

.welcome-text h2 {
  color: var(--el-text-color-primary);
  margin-bottom: 8px;
}

.welcome-text p {
  color: var(--el-text-color-secondary);
}

.feature-row {
  margin-bottom: 60px;
}

.feature-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  height: 300px;
  cursor: pointer;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.feature-card:hover img {
  transform: scale(1.05);
}

.feature-text {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 30px 20px 20px;
  background: linear-gradient(transparent, rgba(0,0,0,0.8));
  color: #fff;
}

.feature-text h3 {
  margin: 0 0 8px 0;
  font-size: 22px;
}

.feature-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-header h2 {
  font-size: 32px;
  color: var(--el-text-color-primary);
  margin-bottom: 12px;
  font-family: serif;
}

.section-header p {
  color: var(--el-text-color-secondary);
}

.craft-row {
  margin-bottom: 40px;
}

.craft-card {
  text-align: center;
}

.craft-card img {
  width: 100%;
  height: 240px;
  object-fit: cover;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

.craft-card h4 {
  font-size: 20px;
  margin: 0 0 10px 0;
  color: var(--el-text-color-primary);
}

.craft-card p {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  line-height: 1.6;
}
</style>
