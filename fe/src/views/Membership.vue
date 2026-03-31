<template>
  <div class="membership page-container">
    <div class="member-header">
      <el-card class="user-card" shadow="hover">
        <div class="user-info">
          <el-avatar :size="80" class="avatar">
            {{ userStore.isLoggedIn ? userStore.userInfo.username.charAt(0).toUpperCase() : 'G' }}
          </el-avatar>
          <div class="details">
            <h2>{{ userStore.isLoggedIn ? userStore.userInfo.username : '游客 (请登录)' }}</h2>
            <div class="tier">
              <el-tag type="warning" effect="dark" round>
                <el-icon><Star /></el-icon> {{ userStore.isLoggedIn ? '黄金会员' : '普通会员' }}
              </el-tag>
            </div>
          </div>
        </div>
        
        <div class="points-section" v-if="userStore.isLoggedIn">
          <div class="points-info">
            <span>当前积分: <strong>1,250</strong></span>
            <span>距离白金会员还需 750 积分</span>
          </div>
          <el-progress :percentage="62.5" :stroke-width="12" status="warning" />
        </div>
      </el-card>
    </div>

    <div class="benefits-section">
      <h3 class="section-title">会员尊享权益</h3>
      <el-row :gutter="20">
        <el-col :span="6" v-for="(benefit, index) in benefits" :key="index">
          <el-card class="benefit-card" shadow="hover">
            <el-icon :size="40" :color="benefit.color"><component :is="benefit.icon" /></el-icon>
            <h4>{{ benefit.title }}</h4>
            <p>{{ benefit.desc }}</p>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="coupons-section">
      <h3 class="section-title">我的优惠券</h3>
      <el-row :gutter="20" v-if="userStore.isLoggedIn">
        <el-col :span="8" v-for="(coupon, index) in coupons" :key="index">
          <div class="coupon-card">
            <div class="coupon-left">
              <div class="amount">
                <span class="symbol" v-if="coupon.type === 'discount'">￥</span>
                <strong>{{ coupon.amount }}</strong>
                <span class="unit" v-if="coupon.type === 'percent'">折</span>
              </div>
              <div class="condition">{{ coupon.condition }}</div>
            </div>
            <div class="coupon-right">
              <h4>{{ coupon.title }}</h4>
              <p>有效期至: {{ coupon.expiry }}</p>
              <el-button type="primary" size="small" round>去使用</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      <div v-else class="login-prompt">
        <el-empty description="登录后查看您的专属优惠券">
          <el-button type="primary" @click="router.push('/login')">立即登录</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '../store/user'
import { useRouter } from 'vue-router'
import { Star, Present, Ticket, CoffeeCup, Discount } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const benefits = [
  { icon: 'Ticket', color: '#e6a23c', title: '每月免邮', desc: '每月可享2次无门槛免邮特权' },
  { icon: 'Present', color: '#f56c6c', title: '生日好礼', desc: '生日当月可获赠专属定制马克杯' },
  { icon: 'CoffeeCup', color: '#67c23a', title: '新品尝鲜', desc: '新品上市提前3天优先购买' },
  { icon: 'Discount', color: '#409eff', title: '积分抵现', desc: '消费累计积分，每100积分抵扣1元' }
]

const coupons = [
  { type: 'discount', amount: '15', condition: '满99元可用', title: '全场满减券', expiry: '2026-12-31' },
  { type: 'percent', amount: '8.8', condition: '无门槛', title: '白金会员折扣券', expiry: '2026-12-31' },
  { type: 'discount', amount: '5', condition: '限拿铁系列', title: '拿铁立减券', expiry: '2026-06-30' }
]
</script>

<style scoped>
.membership {
  padding-top: 40px;
}

.member-header {
  margin-bottom: 40px;
}

.user-card {
  background: linear-gradient(135deg, #2b2b2b 0%, #1a1a1a 100%);
  color: #fff;
  border: none;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
}

.avatar {
  background-color: var(--el-color-primary);
  font-size: 32px;
  border: 2px solid #e6a23c;
}

.details h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
}

.points-section {
  background: rgba(255, 255, 255, 0.1);
  padding: 16px;
  border-radius: 8px;
}

.points-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
}

.points-info strong {
  font-size: 18px;
  color: #e6a23c;
}

.section-title {
  font-size: 22px;
  margin-bottom: 24px;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  display: block;
  width: 4px;
  height: 20px;
  background-color: var(--el-color-primary);
  border-radius: 2px;
}

.benefits-section {
  margin-bottom: 50px;
}

.benefit-card {
  text-align: center;
  padding: 20px 10px;
  height: 100%;
  transition: transform 0.3s;
}

.benefit-card:hover {
  transform: translateY(-5px);
}

.benefit-card h4 {
  margin: 16px 0 8px 0;
  font-size: 16px;
}

.benefit-card p {
  margin: 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.5;
}

.coupons-section {
  margin-bottom: 40px;
}

.coupon-card {
  display: flex;
  height: 120px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  position: relative;
}

.coupon-card::before, .coupon-card::after {
  content: '';
  position: absolute;
  width: 16px;
  height: 16px;
  background-color: var(--el-bg-color-page);
  border-radius: 50%;
  left: 30%;
  transform: translateX(-50%);
}

.coupon-card::before { top: -8px; }
.coupon-card::after { bottom: -8px; }

.coupon-left {
  width: 30%;
  background: linear-gradient(135deg, #fdf6ec 0%, #faecd8 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-right: 2px dashed #f3d19e;
  color: #e6a23c;
}

.amount strong {
  font-size: 32px;
  font-weight: bold;
}

.amount .symbol, .amount .unit {
  font-size: 14px;
}

.condition {
  font-size: 12px;
  margin-top: 4px;
}

.coupon-right {
  width: 70%;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.coupon-right h4 {
  margin: 0;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.coupon-right p {
  margin: 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.coupon-right .el-button {
  align-self: flex-end;
}
</style>
