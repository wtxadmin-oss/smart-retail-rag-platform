<template>
  <div class="common-layout">
    <el-container style="height: 100vh;">
      <el-header style="display:flex; align-items:center; gap:12px; border-bottom: 1px solid #dcdfe6;">
        <span id="logo" style="cursor:pointer;" @click="router.push('/')">
          <img src="/static/picture/logo.jpg" alt="logo" style="height:40px;">
        </span>
        <h2 style="margin:0;">SmartCoffee</h2>
        <span style="opacity:.7;">智能咖啡系统</span>
      </el-header>
      <el-main style="display: flex; justify-content: center; align-items: center; background-color: #f5f7fa;">
        <div style="width: 400px; padding: 40px; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);">
          <h2 style="text-align: center; margin-bottom: 30px;">用户登录</h2>
          <el-form :model="loginForm" label-width="0px">
            <el-form-item>
              <el-input v-model="loginForm.username" placeholder="用户名" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="loginForm.password" type="password" placeholder="密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%;" @click="handleLogin">登录</el-button>
            </el-form-item>
            <div style="text-align: center; margin-top: 16px;">
              <el-button link @click="router.push('/register')">没有账号？去注册</el-button>
            </div>
          </el-form>
          <Footer style="margin-top: 40px; border-top: none;" />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '../components/Footer.vue'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useCartStore } from '../store/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  try {
    const res = await axios.post('/api/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })
    if (res.data?.code === 0) {
      const data = res.data.data || {}
      userStore.setUser({
        id: data.userId,
        username: data.username,
        role: data.role,
        token: data.token
      })
      try {
        await cartStore.syncAfterLogin()
      } catch {
        ElMessage.warning('购物车同步失败，但登录已成功')
      }
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.data?.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查后端服务和账号密码')
  }
}
</script>
