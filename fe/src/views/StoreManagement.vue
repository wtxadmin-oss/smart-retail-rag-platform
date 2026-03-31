<template>
  <div class="admin-wrapper">
    <Navbar />
    
    <div class="page-container admin-layout">
      <Sidebar active="store-mgmt" />
      
      <div class="admin-content with-sidebar">
        <div class="content-header">
          <h2 class="page-title">门店管理</h2>
          <div class="header-actions">
            <el-button @click="handleInit" class="action-btn">从订单地址初始化</el-button>
            <el-button type="primary" size="large" @click="handleAdd" class="action-btn">
              <el-icon><Plus /></el-icon>添加门店
            </el-button>
          </div>
        </div>
        
        <el-card class="admin-card" shadow="never">
          <el-table :data="stores" style="width: 100%" class="custom-table">
            <el-table-column label="门店ID" prop="id" width="80" />
            <el-table-column label="名称" prop="name" width="200" />
            <el-table-column label="地址" prop="address" min-width="250" />
            <el-table-column label="电话" prop="phone" width="150" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" effect="light" round>
                  {{ scope.row.status === 1 ? '营业中' : '暂停营业' }}
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
              @current-change="fetchStores"
            />
          </div>
        </el-card>
        
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑门店' : '添加门店'" width="550px" custom-class="coffee-dialog">
          <el-form :model="form" label-width="80px" class="admin-form">
            <el-form-item label="名称">
              <el-input v-model="form.name" placeholder="如：SmartCoffee 北京店" />
            </el-form-item>
            <el-form-item label="地址">
              <el-input v-model="form.address" placeholder="门店地址（省市区+详细地址）" />
            </el-form-item>
            <el-form-item label="经纬度">
              <div style="display: flex; gap: 10px; width: 100%;">
                <el-input-number v-model="form.lng" :precision="6" placeholder="经度" style="flex: 1;" :controls="false" />
                <el-input-number v-model="form.lat" :precision="6" placeholder="纬度" style="flex: 1;" :controls="false" />
              </div>
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="form.phone" placeholder="联系电话（选填）" />
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="营业中" inactive-text="暂停营业" />
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
const stores = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  name: '',
  address: '',
  lng: null,
  lat: null,
  phone: '',
  status: 1
})

// 分页加载后台门店列表。
const fetchStores = async () => {
  try {
    const res = await axios.get('/api/admin/stores', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    const pageData = res.data.data
    stores.value = pageData.list
    total.value = pageData.total
  } catch (e) {
    console.error('Fetch stores failed', e)
    ElMessage.error('获取门店失败，请检查后端服务')
  }
}

// 调用初始化接口，根据历史订单地址自动生成门店数据。
const handleInit = async () => {
  try {
    await axios.post('/api/admin/stores/init')
    ElMessage.success('已根据订单地址初始化门店')
    fetchStores()
  } catch (e) {
    ElMessage.error('初始化失败，请检查后端服务')
    console.error('Init stores failed', e)
  }
}

// 打开新增门店弹窗，并清空上一次录入内容。
const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.address = ''
  form.lng = null
  form.lat = null
  form.phone = ''
  form.status = 1
  dialogVisible.value = true
}

// 把当前门店数据填充到表单里，供管理员编辑。
const handleEdit = (store) => {
  isEdit.value = true
  form.id = store.id
  form.name = store.name
  form.address = store.address
  form.lng = store.lng
  form.lat = store.lat
  form.phone = store.phone
  form.status = store.status
  dialogVisible.value = true
}

// 删除门店前弹出确认提示，确认后再调用删除接口。
const handleDelete = (store) => {
  ElMessageBox.confirm('确定删除该门店吗？', '提示', { type: 'warning' }).then(() => {
    axios.delete(`/api/admin/stores/${store.id}`).then(() => {
      ElMessage.success('门店已删除！')
      fetchStores()
    })
  })
}

// 根据弹窗模式提交新增或更新门店请求。
const handleSubmit = async () => {
  try {
    const payload = {
      name: form.name,
      address: form.address,
      lng: form.lng,
      lat: form.lat,
      phone: form.phone,
      status: form.status
    }
    if (isEdit.value) {
      await axios.put(`/api/admin/stores/${form.id}`, payload)
      ElMessage.success('门店更新成功！')
    } else {
      await axios.post('/api/admin/stores', payload)
      ElMessage.success('门店添加成功！')
    }
    dialogVisible.value = false
    fetchStores()
  } catch (e) {
    ElMessage.error('保存失败，请检查后端服务')
    console.error('Save store failed', e)
  }
}

onMounted(() => {
  fetchStores()
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
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

  .header-actions {
    width: 100%;
  }

  .header-actions .el-button {
    flex: 1 1 220px;
  }
}
</style>
