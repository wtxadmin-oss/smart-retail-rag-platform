<template>
  <el-aside width="220px" style="border-right: 1px solid #dcdfe6;">
    <el-menu :default-active="active" style="border-right: none;">
      <!-- Common for all -->
      <el-menu-item index="home" @click="router.push('/')">首页</el-menu-item>
      <el-menu-item index="wherecoffee" @click="router.push('/wherecoffee')">门店</el-menu-item>
      <el-menu-item index="menu" @click="router.push('/menu')">菜单</el-menu-item>
      <el-menu-item index="customer" @click="router.push('/customer')">客服</el-menu-item>
      
      <!-- Customer Only -->
      <template v-if="userStore.isCustomer">
        <el-menu-item index="cart" @click="router.push('/cart')">购物车</el-menu-item>
        <el-menu-item index="orders" @click="router.push('/orders')">我的订单</el-menu-item>
      </template>

      <!-- Admin Only -->
      <template v-if="userStore.isAdmin">
        <el-sub-menu index="admin">
          <template #title>管理后台</template>
          <el-menu-item index="user-mgmt" @click="router.push('/admin/users')">用户管理</el-menu-item>
          <el-menu-item index="menu-mgmt" @click="router.push('/admin/menu')">菜单管理</el-menu-item>
          <el-menu-item index="store-mgmt" @click="router.push('/admin/stores')">门店管理</el-menu-item>
          <el-menu-item index="spec-mgmt" @click="router.push('/admin/specs')">规格管理</el-menu-item>
          <el-menu-item index="all-orders" @click="router.push('/admin/orders')">全部订单</el-menu-item>
        </el-sub-menu>
      </template>

      <!-- User Management -->
      <el-sub-menu index="user-profile">
        <template #title>
          <span>{{ userStore.isLoggedIn ? userStore.userInfo.username : '用户管理' }}</span>
        </template>
        <template v-if="userStore.isLoggedIn">
          <el-menu-item index="profile" @click="router.push('/profile')">个人信息</el-menu-item>
          <el-menu-item index="logout" @click="handleLogout">退出登录</el-menu-item>
        </template>
        <template v-else>
          <el-menu-item index="login" @click="router.push('/login')">登录</el-menu-item>
          <el-menu-item index="register" @click="router.push('/register')">注册</el-menu-item>
        </template>
      </el-sub-menu>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'

const props = defineProps({
  active: String
})

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  userStore.clearUser()
  router.push('/login')
}
</script>
