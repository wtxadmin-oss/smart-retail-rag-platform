<template>
  <div class="auth-wrapper">
    <Navbar />
    
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h2>欢迎回来</h2>
          <p>登录您的 SmartCoffee 账号</p>
        </div>
        
        <el-form :model="loginForm" label-width="0px" class="auth-form">
          <el-form-item>
            <el-input 
              v-model="loginForm.username" 
              placeholder="用户名" 
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item>
            <el-input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="密码" 
              prefix-icon="Lock"
              show-password 
              size="large"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="handleLogin" :loading="loading">
              登录
            </el-button>
          </el-form-item>
          <div class="auth-footer">
            <span class="text">还没有账号？</span>
            <el-button link type="primary" @click="router.push('/register')">立即注册</el-button>
          </div>
        </el-form>
      </div>
    </div>
    
    <Footer />
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { useCartStore } from '../store/cart'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

// 提交登录表单，保存用户信息和 token，并在成功后同步购物车。
const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
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
        console.warn('Cart sync failed')
      }
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.data?.message || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录失败，请检查后端服务和账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color-page);
  background-image: url('/static/picture/background1.jpg');
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  position: relative;
}

.auth-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(44, 36, 27, 0.6);
  z-index: 0;
}

.auth-wrapper > * {
  position: relative;
  z-index: 1;
}

.auth-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 12px 32px rgba(0,0,0,0.15);
  padding: 48px 40px;
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.auth-header h2 {
  font-size: 28px;
  color: var(--el-text-color-primary);
  margin: 0 0 12px 0;
  font-weight: 600;
}

.auth-header p {
  margin: 0;
  color: var(--el-text-color-secondary);
  font-size: 15px;
}

.auth-form {
  margin-bottom: 16px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px var(--el-border-color-light) inset;
  padding: 0 16px;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}

:deep(.el-input__inner) {
  height: 48px;
}

:deep(.el-input__prefix-inner) {
  font-size: 18px;
  color: var(--el-text-color-secondary);
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 8px;
  margin-top: 12px;
}

.auth-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
}

.auth-footer .text {
  color: var(--el-text-color-regular);
}
</style>
