<template>
  <div class="admin-wrapper">
    <Navbar />
    
    <div class="page-container admin-layout">
      <Sidebar active="user-mgmt" />
      
      <div class="admin-content with-sidebar">
        <div class="content-header">
          <h2 class="page-title">用户管理</h2>
          <el-button type="primary" size="large" @click="handleAdd" class="action-btn">
            <el-icon><Plus /></el-icon>添加用户
          </el-button>
        </div>
        
        <el-card class="admin-card" shadow="never">
          <el-table :data="users" style="width: 100%" class="custom-table">
            <el-table-column label="ID" prop="id" width="80" />
            <el-table-column label="用户名" prop="username" min-width="120" />
            <el-table-column label="角色" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'success'" effect="light" round>
                  {{ scope.row.role }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="手机号" prop="phone" width="150" />
            <el-table-column label="注册时间" prop="createTime" width="180" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" effect="light" round>
                  {{ scope.row.status === 1 ? '正常' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button type="primary" text @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="danger" text @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-wrapper" v-if="total > pageSize">
            <el-pagination
              background
              layout="prev, pager, next"
              :total="total"
              :page-size="pageSize"
              v-model:current-page="currentPage"
              @current-change="fetchUsers"
            />
          </div>
        </el-card>
        
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '添加用户'" width="450px" custom-class="coffee-dialog">
          <el-form :model="form" label-width="80px" class="admin-form">
            <el-form-item label="用户名">
              <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
            <el-form-item label="角色">
              <el-select v-model="form.role" style="width: 100%;">
                <el-option label="普通客户" value="CUSTOMER" />
                <el-option label="管理员" value="ADMIN" />
              </el-select>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </template>
        </el-dialog>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import Sidebar from '../components/Sidebar.vue'
import Footer from '../components/Footer.vue'
import { Plus } from '@element-plus/icons-vue'
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

// 分页获取后台用户列表，并更新表格与分页数据。
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

// 打开新增用户弹窗，并把表单重置为默认值。
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

// 把当前行用户数据回填到表单中，进入编辑模式。
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

// 删除用户前先进行二次确认，确认后请求后台删除。
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

// 根据当前是新增还是编辑状态，提交用户表单到对应接口。
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

<style scoped>
.admin-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color-page);
}

.page-container {
  flex: 1;
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 100px 20px 40px;
  gap: 24px;
  align-items: flex-start;
  box-sizing: border-box;
}

.admin-content {
  flex: 1;
  min-width: 0;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
  flex-wrap: wrap;
}

.page-title {
  font-size: 24px;
  color: var(--el-text-color-primary);
  margin: 0;
  font-weight: 600;
}

.admin-card {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--el-border-color-light);
  background-color: #fff;
  overflow-x: auto;
}

.custom-table {
  --el-table-border-color: var(--el-border-color-light);
  --el-table-header-bg-color: var(--el-bg-color-page);
  --el-table-header-text-color: var(--el-text-color-primary);
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
}

:deep(.coffee-dialog) {
  border-radius: 12px;
}

.admin-form {
  padding: 0 20px;
}

@media (max-width: 992px) {
  .page-container {
    flex-direction: column;
  }

  .content-header .el-button {
    width: 100%;
  }
}
</style>
