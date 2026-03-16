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
      <el-container style="overflow: hidden;">
        <Sidebar active="profile" />
        <el-main>
          <h2>个人信息修改</h2>
          <el-form :model="form" label-width="100px" style="max-width: 500px;">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" />
            </el-form-item>
            <el-divider>修改密码</el-divider>
            <el-form-item label="原密码">
              <el-input v-model="form.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="form.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdate">提交修改</el-button>
              <el-button type="warning" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
          
          <Footer />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from '../components/Sidebar.vue'
import Footer from '../components/Footer.vue'
import { useUserStore } from '../store/user'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  username: '',
  nickname: '',
  oldPassword: '',
  newPassword: '',
  email: '',
  phone: ''
})

onMounted(() => {
  if (userStore.userInfo) {
    form.username = userStore.userInfo.username
    form.nickname = userStore.userInfo.nickname || ''
    form.email = userStore.userInfo.email || ''
    form.phone = userStore.userInfo.phone || ''
  }
})

const handleUpdate = () => {
  ElMessage.success('个人信息更新成功！')
  // TODO: Call API to update
  userStore.setUser({ ...userStore.userInfo, ...form })
}

const handleChangePassword = async () => {
  if (!userStore.userInfo?.id) {
    ElMessage.error('请先登录')
    return
  }
  try {
    const res = await axios.post('/api/auth/change-password', {
      userId: userStore.userInfo.id,
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    if (res.data?.code === 0) {
      ElMessage.success('密码修改成功，请重新登录')
      userStore.clearUser()
      // 延迟跳转让用户看到提示
      setTimeout(() => {
        router.push('/login')
      }, 500)
    } else {
      ElMessage.error(res.data?.message || '密码修改失败')
    }
  } catch (e) {
    ElMessage.error('密码修改失败，请检查后端服务')
  }
}
</script>
