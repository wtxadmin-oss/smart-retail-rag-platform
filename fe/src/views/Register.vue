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
        <div style="width: 450px; padding: 40px; background: #fff; border-radius: 8px; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);">
          <h2 style="text-align: center; margin-bottom: 30px;">用户注册</h2>
          <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="80px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="registerForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="checkPass">
              <el-input v-model="registerForm.checkPass" type="password" placeholder="请再次输入密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%;" @click="submitForm(registerFormRef)">注册</el-button>
            </el-form-item>
            <div style="text-align: center; margin-top: 16px;">
              <el-button link @click="router.push('/login')">已有账号？去登录</el-button>
            </div>
          </el-form>
          <Footer style="margin-top: 40px; border-top: none;" />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '../components/Footer.vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const registerFormRef = ref()
const registerForm = reactive({
  username: '',
  password: '',
  checkPass: ''
})

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
  checkPass: [{ validator: validatePass2, trigger: 'blur' }]
})

const submitForm = (formEl) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (!valid) return false
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
    })
  })
}
</script>
