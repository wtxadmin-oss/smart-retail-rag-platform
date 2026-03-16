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
        <Sidebar active="store-mgmt" />
        <el-main>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 style="margin: 0;">门店管理</h2>
            <div style="display:flex; align-items:center; gap:12px;">
              <el-button type="primary" @click="handleAdd">添加门店</el-button>
              <el-button @click="handleInit">从订单地址初始化</el-button>
            </div>
          </div>
          
          <el-table :data="stores" style="width: 100%">
            <el-table-column label="门店ID" prop="id" width="80" />
            <el-table-column label="名称" prop="name" width="200" />
            <el-table-column label="地址" prop="address" width="300" />
            <el-table-column label="电话" prop="phone" width="150" />
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '营业中' : '暂停营业' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
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
            @current-change="fetchStores"
            style="margin-top: 20px; display: flex; justify-content: center;"
          />
          
          <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑门店' : '添加门店'" width="500px">
            <el-form :model="form" label-width="80px">
              <el-form-item label="名称">
                <el-input v-model="form.name" placeholder="如：SmartCoffee 北京店" />
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="form.address" placeholder="门店地址（省市区+详细地址）" />
              </el-form-item>
              <el-form-item label="经纬度">
                <div style="display: flex; gap: 10px;">
                  <el-input-number v-model="form.lng" :precision="6" placeholder="经度" style="flex: 1;" />
                  <el-input-number v-model="form.lat" :precision="6" placeholder="纬度" style="flex: 1;" />
                </div>
              </el-form-item>
              <el-form-item label="电话">
                <el-input v-model="form.phone" placeholder="联系电话（选填）" />
              </el-form-item>
              <el-form-item label="状态">
                <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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

const handleDelete = (store) => {
  ElMessageBox.confirm('确定删除该门店吗？', '提示', { type: 'warning' }).then(() => {
    axios.delete(`/api/admin/stores/${store.id}`).then(() => {
      ElMessage.success('门店已删除！')
      fetchStores()
    })
  })
}

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
