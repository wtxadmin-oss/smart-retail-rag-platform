<template>
  <div class="auth-wrapper">
    <Navbar />
    
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h2>加入我们</h2>
          <p>注册成为 SmartCoffee 会员，享受更多优惠</p>
        </div>
        
        <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="0px" class="auth-form">
          <el-form-item prop="username">
            <el-input 
              v-model="registerForm.username" 
              placeholder="请输入用户名" 
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input 
              v-model="registerForm.password" 
              type="password" 
              placeholder="请输入密码" 
              prefix-icon="Lock"
              show-password 
              size="large"
            />
          </el-form-item>
          <el-form-item prop="checkPass">
            <el-input 
              v-model="registerForm.checkPass" 
              type="password" 
              placeholder="请再次输入密码" 
              prefix-icon="CircleCheck"
              show-password 
              size="large"
              @keyup.enter="submitForm(registerFormRef)"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="submitForm(registerFormRef)" :loading="loading">
              注册账号
            </el-button>
          </el-form-item>
          <div class="auth-footer">
            <span class="text">已有账号？</span>
            <el-button link type="primary" @click="router.push('/login')">立即登录</el-button>
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
import { User, Lock, CircleCheck } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  checkPass: ''
})

// 校验两次输入的密码是否一致，供注册表单规则使用。
const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error("两次输入密码不一致!"))
  } else {
    callback()
  }
}

const rules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  checkPass: [{ required: true, validator: validatePass2, trigger: 'blur' }]
})

// 先触发表单校验，再调用注册接口创建新账号。
const submitForm = (formEl) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (!valid) return false
    loading.value = true
    axios.post('/api/auth/register', {
      username: registerForm.username,
      password: registerForm.password
    }).then((res) => {
      if (res.data?.code === 0) {
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } else {
        ElMessage.error(res.data?.message || '注册失败')
      }
    }).catch(() => {
      ElMessage.error('注册失败，请检查后端服务')
    }).finally(() => {
      loading.value = false
    })
  })
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
