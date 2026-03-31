<template>
  <div class="profile-wrapper">
    <Navbar />
    
    <div class="page-container">
      <div class="profile-content">
        <el-card class="profile-card" shadow="never">
          <div class="profile-header">
            <el-avatar :size="80" class="profile-avatar">
              {{ form.username.charAt(0).toUpperCase() || 'U' }}
            </el-avatar>
            <div class="profile-title">
              <h2>个人信息</h2>
              <p>管理您的账户设置与安全信息</p>
            </div>
          </div>

          <el-form :model="form" label-width="100px" class="profile-form" label-position="top">
            <div class="form-section">
              <h3>基本信息</h3>
              <div class="form-grid">
                <el-form-item label="用户名">
                  <el-input v-model="form.username" disabled />
                </el-form-item>
                <el-form-item label="昵称">
                  <el-input v-model="form.nickname" placeholder="请输入您的昵称" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="form.email" placeholder="请输入邮箱地址" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="form.phone" placeholder="请输入手机号码" />
                </el-form-item>
              </div>
              <div class="action-row">
                <el-button type="primary" @click="handleUpdate" class="action-btn">保存基本信息</el-button>
              </div>
            </div>

            <el-divider class="custom-divider" />

            <div class="form-section">
              <h3>安全设置</h3>
              <div class="form-grid">
                <el-form-item label="原密码">
                  <el-input v-model="form.oldPassword" type="password" show-password placeholder="请输入当前密码" />
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="form.newPassword" type="password" show-password placeholder="请输入新密码" />
                </el-form-item>
              </div>
              <div class="action-row">
                <el-button type="warning" @click="handleChangePassword" class="action-btn" :disabled="!form.oldPassword || !form.newPassword">修改密码</el-button>
              </div>
            </div>
          </el-form>
        </el-card>
      </div>
    </div>
    
    <Footer />
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
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

// 保存当前页面上的基础资料，并同步更新前端用户状态。
const handleUpdate = () => {
  ElMessage.success('个人信息更新成功！')
  // TODO: Call API to update
  userStore.setUser({ ...userStore.userInfo, ...form })
}

// 调用修改密码接口；成功后清空登录状态并跳回登录页。
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

<style scoped>
.profile-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color-page);
}

.page-container {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 40px 20px;
}

.profile-content {
  width: 100%;
  max-width: 800px;
}

.profile-card {
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--el-border-color-light);
  overflow: hidden;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 32px 40px;
  background-color: var(--el-color-primary-light-9);
  border-bottom: 1px solid var(--el-border-color-light);
}

.profile-avatar {
  background-color: var(--el-color-primary);
  font-size: 32px;
  color: #fff;
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.profile-title h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.profile-title p {
  margin: 0;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.profile-form {
  padding: 40px;
}

.form-section {
  margin-bottom: 32px;
}

.form-section:last-child {
  margin-bottom: 0;
}

.form-section h3 {
  font-size: 18px;
  color: var(--el-text-color-primary);
  margin: 0 0 24px 0;
  font-weight: 600;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 0 24px;
}

.action-row {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  padding: 10px 32px;
  font-size: 15px;
  border-radius: 8px;
}

.custom-divider {
  margin: 40px 0;
  border-color: var(--el-border-color-light);
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--el-text-color-regular);
  padding-bottom: 8px;
}

:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--el-border-color-light) inset;
  border-radius: 8px;
  padding: 0 16px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--el-color-primary-light-5) inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--el-color-primary) inset;
}

:deep(.el-input__inner) {
  height: 44px;
}

:deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: var(--el-bg-color-page);
}
</style>
