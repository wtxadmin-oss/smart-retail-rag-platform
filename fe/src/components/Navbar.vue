<template>
  <el-header class="smart-coffee-header" :class="{ 'is-scrolled': isScrolled }">
    <div class="header-container">
      <!-- Logo Area -->
      <div class="logo-area" @click="router.push('/')">
        <img src="/static/picture/logo.jpg" alt="SmartCoffee Logo" class="logo-img">
        <div class="logo-text">
          <h1>SmartCoffee</h1>
          <span>Premium Quality</span>
        </div>
      </div>

      <!-- Center Navigation -->
      <nav class="center-nav">
        <router-link to="/" class="nav-link" active-class="active">首页</router-link>
        <router-link to="/menu" class="nav-link" active-class="active">菜单</router-link>
        <router-link to="/wherecoffee" class="nav-link" active-class="active">体验店</router-link>
        <router-link to="/brand" class="nav-link" active-class="active">品牌故事</router-link>
        <router-link to="/wiki" class="nav-link" active-class="active">咖啡百科</router-link>
        <router-link to="/customer" class="nav-link" active-class="active">AI客服</router-link>
      </nav>

      <!-- Right Actions -->
      <div class="right-actions">
        <!-- Customer / Guest Actions -->
        <template v-if="!userStore.isAdmin">
          <div class="icon-action" @click="goToCart">
            <el-badge :value="cartCount" :hidden="cartCount === 0" class="badge-item">
              <el-icon :size="22"><ShoppingCart /></el-icon>
            </el-badge>
          </div>
          <div class="icon-action" @click="goToOrders">
            <el-icon :size="22"><List /></el-icon>
          </div>
        </template>

        <!-- User Profile Dropdown -->
        <el-dropdown trigger="click" @command="handleCommand" class="user-dropdown">
          <div class="user-avatar-wrapper">
            <el-avatar :size="32" class="user-avatar">
              {{ userStore.isLoggedIn ? userStore.userInfo.username.charAt(0).toUpperCase() : 'G' }}
            </el-avatar>
            <span class="greeting" v-if="userStore.isLoggedIn">Hi, {{ userStore.userInfo.username }}</span>
            <span class="greeting" v-else>登录 / 注册</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <template v-if="userStore.isLoggedIn">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="membership">会员中心</el-dropdown-item>
                <el-dropdown-item command="cart" v-if="!userStore.isAdmin">购物车</el-dropdown-item>
                <el-dropdown-item command="orders" v-if="!userStore.isAdmin">历史订单</el-dropdown-item>
                <el-dropdown-item command="admin" v-if="userStore.isAdmin">管理后台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </template>
              <template v-else>
                <el-dropdown-item command="cart">购物车</el-dropdown-item>
                <el-dropdown-item command="orders">历史订单</el-dropdown-item>
                <el-dropdown-item divided command="login">用户登录</el-dropdown-item>
                <el-dropdown-item command="register">注册账号</el-dropdown-item>
              </template>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { useCartStore } from '../store/cart'
import { ShoppingCart, List } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const isScrolled = ref(false)

const cartCount = computed(() => {
  return cartStore.items.reduce((total, item) => total + item.quantity, 0)
})

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const goToCart = () => {
  if (userStore.isAdmin) {
    ElMessage.warning('管理员账号不显示购物车模块')
    return
  }
  router.push('/cart')
}

const goToOrders = () => {
  if (userStore.isAdmin) {
    ElMessage.warning('管理员账号不显示订单模块')
    return
  }
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后查看历史订单')
    router.push('/login')
    return
  }
  router.push('/orders')
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const handleCommand = (command) => {
  if (command === 'logout') {
    // User-008: 使用 logout() 同时清空购物车内存
    userStore.logout()
    router.push('/login')
  } else if (command === 'admin') {
    router.push('/admin/orders')
  } else if (command === 'cart') {
    goToCart()
  } else if (command === 'orders') {
    goToOrders()
  } else {
    router.push('/' + command)
  }
}
</script>

<style scoped>
.smart-coffee-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background-color: rgba(253, 251, 247, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(139, 90, 43, 0.1);
  transition: all 0.3s ease;
  height: 70px !important;
  display: flex;
  align-items: center;
}

.smart-coffee-header.is-scrolled {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.header-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo-img {
  height: 44px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(139, 90, 43, 0.2);
}

.logo-text h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: var(--el-color-primary);
  letter-spacing: 0.5px;
}

.logo-text span {
  font-size: 12px;
  color: #888;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.center-nav {
  display: flex;
  gap: 32px;
}

.nav-link {
  text-decoration: none;
  color: #4a4a4a;
  font-size: 15px;
  font-weight: 500;
  position: relative;
  padding: 8px 0;
  transition: color 0.3s;
}

.nav-link:hover, .nav-link.active {
  color: var(--el-color-primary);
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background-color: var(--el-color-primary);
  transition: width 0.3s ease;
}

.nav-link:hover::after, .nav-link.active::after {
  width: 100%;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 24px;
}

.icon-action {
  cursor: pointer;
  color: #4a4a4a;
  transition: color 0.3s;
  display: flex;
  align-items: center;
}

.icon-action:hover {
  color: var(--el-color-primary);
}

.user-dropdown {
  cursor: pointer;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-avatar {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
  font-weight: bold;
}

.greeting {
  font-size: 14px;
  color: #4a4a4a;
  font-weight: 500;
}
</style>
