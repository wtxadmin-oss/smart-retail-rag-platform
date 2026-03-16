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
        <Sidebar active="user-mgmt" />
        <el-main>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 style="margin: 0;">用户管理</h2>
            <el-button type="primary" @click="handleAdd">添加用户</el-button>
          </div>
          
          <el-table :data="users" border style="width: 100%">
            <el-table-column label="ID" prop="id" width="80" />
            <el-table-column label="用户名" prop="username" width="120" />
            <el-table-column label="角色" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'success'">{{ scope.row.role }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="手机号" prop="phone" width="150" />
            <el-table-column label="注册时间" prop="createTime" width="180" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="total > pageSize"
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            v-model:current-page="currentPage"
            @current-change="fetchUsers"
            style="margin-top: 20px; display: flex; justify-content: center;"
          />
          
          <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'" width="400px">
            <el-form :model="form" label-width="80px">
              <el-form-item label="用户名">
                <el-input v-model="form.username" :disabled="isEdit" />
              </el-form-item>
              <el-form-item label="密码">
                <el-input v-model="form.password" type="password" show-password />
              </el-form-item>
              <el-form-item label="角色">
                <el-select v-model="form.role" style="width: 100%;">
                  <el-option label="普通客户" value="CUSTOMER" />
                  <el-option label="管理员" value="ADMIN" />
                </el-select>
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="form.phone" />
              </el-form-item>
            </el-form>
            <template #footer>
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handleSubmit">确定</el-button>
            </template>
          </el-dialog>
          
          <Footer />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from '../components/Sidebar.vue'
import Footer from '../components/Footer.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const users = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  username: '',
  password: '',
  role: 'CUSTOMER',
  phone: '',
  status: 1
})

const fetchUsers = async () => {
  try {
    const res = await axios.get('/api/admin/users', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    const pageData = res.data.data
    users.value = pageData.list
    total.value = pageData.total
  } catch (e) {
    ElMessage.error('获取用户列表失败')
    console.error('Fetch users failed', e)
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.username = ''
  form.password = ''
  form.role = 'CUSTOMER'
  form.phone = ''
  form.status = 1
  dialogVisible.value = true
}

const handleEdit = (user) => {
  isEdit.value = true
  form.id = user.id
  form.username = user.username
  form.password = ''
  form.role = user.role
  form.phone = user.phone || ''
  form.status = user.status
  dialogVisible.value = true
}

const handleDelete = (user) => {
  ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' }).then(() => {
    axios.delete(`/api/admin/users/${user.id}`).then(() => {
      ElMessage.success('用户已删除！')
      fetchUsers()
    }).catch((e) => {
      ElMessage.error('删除失败，请检查后端服务')
      console.error('Delete user failed', e)
    })
  })
}

const handleSubmit = async () => {
  try {
    const payload = {
      username: form.username,
      password: form.password,
      role: form.role,
      phone: form.phone,
      status: form.status
    }
    if (isEdit.value) {
      await axios.put(`/api/admin/users/${form.id}`, payload)
      ElMessage.success('用户更新成功！')
    } else {
      await axios.post('/api/admin/users', payload)
      ElMessage.success('用户添加成功！')
    }
    dialogVisible.value = false
    fetchUsers()
  } catch (e) {
    ElMessage.error('保存失败，请检查后端服务')
    console.error('Save user failed', e)
  }
}

onMounted(() => {
  fetchUsers()
})
</script>
