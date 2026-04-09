<template>
  <div class="brand-story-page">
    <Navbar />

    <div class="brand-story page-container">
    <div class="hero-banner">
      <img src="/static/picture/xuanchuan1.jpg" alt="Brand Story Banner" class="hero-image" />
      <div class="hero-text">
        <h1>寻找世界好豆</h1>
        <p>用心寻觅每一颗纯粹，成就非凡咖啡体验</p>
      </div>
    </div>

    <div class="story-section">
      <el-row :gutter="40" align="middle" class="section-row">
        <el-col :span="12">
          <img src="/static/picture/xuanchuan2.jpg" alt="Our Origin" class="section-image" />
        </el-col>
        <el-col :span="12">
          <div class="section-content">
            <h2>我们的起源</h2>
            <p>SmartCoffee 诞生于对咖啡无尽的热爱。我们跨越数千公里，深入高海拔核心产区，只为寻找那一颗风味独特、品质卓越的阿拉比卡咖啡豆。每一杯 SmartCoffee，都是大自然与时间的完美馈赠。</p>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="40" align="middle" class="section-row reverse-row">
        <el-col :span="12">
          <div class="section-content">
            <h2>大师级烘焙</h2>
            <p>我们坚持“小批量、精细化”的烘焙理念。经验丰富的烘焙大师根据不同豆种的特性，定制专属烘焙曲线，精准控制温度与时间，唤醒咖啡豆深藏的醇香与风味层级。</p>
          </div>
        </el-col>
        <el-col :span="12">
          <img src="/static/picture/xuanchuan3.jpg" alt="Master Roasting" class="section-image" />
        </el-col>
      </el-row>

      <el-row :gutter="40" align="middle" class="section-row">
        <el-col :span="12">
          <img src="/static/picture/xuanchuan4.jpg" alt="Quality Commitment" class="section-image" />
        </el-col>
        <el-col :span="12">
          <div class="section-content">
            <h2>品质承诺</h2>
            <p>从种子到杯子，我们严格把控每一个环节。先进的萃取设备、纯净的水质过滤系统、新鲜的冷链奶源，再配合专业的咖啡师团队，确保您品尝到的每一口都达到极致标准。</p>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
  </div>
</template>

<script setup>
import Navbar from '../components/Navbar.vue'
</script>

<style scoped>
.brand-story-page {
  min-height: 100vh;
  background-color: var(--el-bg-color-page);
}

.brand-story {
  padding-top: 110px;
  padding-bottom: 40px;
}

.hero-banner {
  position: relative;
  width: 100%;
  height: 400px;
  overflow: hidden;
  border-radius: 12px;
  margin-bottom: 60px;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.7);
}

.hero-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: white;
}

.hero-text h1 {
  font-size: 48px;
  margin-bottom: 16px;
  letter-spacing: 4px;
}

.hero-text p {
  font-size: 20px;
  letter-spacing: 2px;
  opacity: 0.9;
}

.story-section {
  max-width: 1000px;
  margin: 0 auto;
}

.section-row {
  margin-bottom: 80px;
}

.section-image {
  width: 100%;
  height: 350px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.section-image:hover {
  transform: translateY(-5px);
}

.section-content {
  padding: 0 20px;
}

.section-content h2 {
  font-size: 32px;
  color: var(--el-text-color-primary);
  margin-bottom: 24px;
  position: relative;
}

.section-content h2::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 0;
  width: 40px;
  height: 3px;
  background-color: var(--el-color-primary);
}

.section-content p {
  font-size: 16px;
  color: var(--el-text-color-regular);
  line-height: 1.8;
  text-align: justify;
}

@media (max-width: 768px) {
  .section-row {
    flex-direction: column !important;
  }
  .section-row .el-col {
    width: 100% !important;
    margin-bottom: 30px;
  }
  .section-content h2::after {
    left: 50%;
    transform: translateX(-50%);
  }
  .section-content {
    text-align: center;
  }
  .section-content p {
    text-align: center;
  }
}
</style>
