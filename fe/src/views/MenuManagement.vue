<template>
  <div class="admin-wrapper">
    <Navbar />
    <div class="page-container admin-layout">
      <Sidebar active="menu-mgmt" />
      <div class="admin-content with-sidebar">
        <div class="content-header">
          <h2 class="page-title">菜单管理</h2>
          <el-select v-model="selectedStoreId" placeholder="选择门店" class="store-filter" @change="fetchProducts">
            <el-option v-for="store in stores" :key="store.id" :label="store.name" :value="store.id" />
          </el-select>
          <el-button type="primary" size="large" class="action-btn" @click="handleAdd"><el-icon><Plus /></el-icon>添加咖啡商品</el-button>
        </div>
        <el-card class="admin-card" shadow="never">
          <el-table :data="products" style="width: 100%" class="custom-table">
            <el-table-column label="图片" width="100"><template #default="scope"><img :src="scope.row.imageUrl || '/static/picture/xuanchuan1.jpg'" class="table-img" /></template></el-table-column>
            <el-table-column label="名称" prop="name" min-width="150" />
            <el-table-column label="分类" width="160"><template #default="scope"><el-tag type="info" effect="light">{{ categoryMap[scope.row.categoryId] || '--' }}</el-tag></template></el-table-column>
            <el-table-column label="价格" width="100"><template #default="scope"><span class="price-text">¥ {{ scope.row.minPrice ?? '--' }}</span></template></el-table-column>
            <el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="isOnSale(scope.row) ? 'success' : 'info'" effect="light" round>{{ isOnSale(scope.row) ? '在售' : '不在售' }}</el-tag></template></el-table-column>
            <el-table-column label="门店可售" width="120"><template #default="scope"><el-switch :model-value="scope.row.storeAvailable === 1" :disabled="!selectedStoreId" @change="(value) => updateStoreAvailability(scope.row, value)" /></template></el-table-column>
            <el-table-column label="操作" width="180" fixed="right"><template #default="scope"><el-button type="primary" text @click="handleEdit(scope.row)">编辑</el-button><el-button type="danger" text @click="handleDelete(scope.row)">删除</el-button></template></el-table-column>
          </el-table>
          <div class="pagination-wrapper" v-if="total > pageSize"><el-pagination v-model:current-page="currentPage" background layout="prev, pager, next" :total="total" :page-size="pageSize" @current-change="fetchProducts" /></div>
        </el-card>
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '添加商品'" width="550px" custom-class="coffee-dialog">
          <el-form :model="form" label-width="80px" class="admin-form">
            <el-form-item label="名称"><el-input v-model="form.name" placeholder="请输入商品名称" /></el-form-item>
            <el-form-item label="分类"><el-select v-model="form.categoryId" style="width: 100%" placeholder="请选择商品分类"><el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" /></el-select></el-form-item>
            <el-form-item label="商品图片"><div class="upload-wrapper"><el-upload :http-request="handleImageUpload" :show-file-list="false" accept="image/*"><el-button type="primary" plain>上传图片</el-button></el-upload><img v-if="form.imageUrl" :src="form.imageUrl" class="preview-img" /></div></el-form-item>
            <el-form-item label="状态"><el-switch v-model="form.isActive" :active-value="1" :inactive-value="0" active-text="在售" inactive-text="下架" /></el-form-item>
            <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入商品描述..." /></el-form-item>
          </el-form>
          <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
        </el-dialog>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import Sidebar from '../components/Sidebar.vue'
import Footer from '../components/Footer.vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const products = ref([])
const stores = ref([])
const selectedStoreId = ref(null)
const categories = ref([])
const categoryMap = ref({})
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({ id: null, name: '', categoryId: null, description: '', imageUrl: '', isActive: 1 })

const normalizeFlag = (value) => {
  if (value === 1 || value === '1' || value === true) return 1
  return 0
}

const isOnSale = (product) => normalizeFlag(product?.isActive) === 1 && normalizeFlag(product?.storeAvailable) === 1

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/categories')
    categories.value = res.data?.data || []
    categoryMap.value = Object.fromEntries(categories.value.map(category => [category.id, category.name]))
  } catch (error) {
    ElMessage.error('获取分类失败，请检查后端服务')
    console.error('Fetch categories failed', error)
  }
}

const fetchStores = async () => {
  try {
    const res = await axios.get('/api/admin/stores', { params: { pageNum: 1, pageSize: 200 } })
    stores.value = res.data?.data?.list || []
    if (!selectedStoreId.value && stores.value.length > 0) selectedStoreId.value = stores.value[0].id
  } catch {
    ElMessage.error('获取门店失败')
  }
}

const fetchProducts = async () => {
  try {
    if (!selectedStoreId.value) {
      products.value = []
      total.value = 0
      return
    }
    const res = await axios.get(`/api/admin/stores/${selectedStoreId.value}/products`, { params: { pageNum: currentPage.value, pageSize: pageSize.value } })
    const pageData = res.data?.data || {}
    products.value = (pageData.list || []).map(product => ({
      ...product,
      isActive: normalizeFlag(product?.isActive),
      storeAvailable: normalizeFlag(product?.storeAvailable)
    }))
    total.value = pageData.total || 0
  } catch (error) {
    ElMessage.error('获取商品失败，请检查后端服务')
    console.error('Fetch products failed', error)
  }
}

const updateStoreAvailability = async (product, value) => {
  if (!selectedStoreId.value) return
  try {
    await axios.put(`/api/admin/stores/${selectedStoreId.value}/products/${product.id}`, { isAvailable: value ? 1 : 0 })
    product.storeAvailable = value ? 1 : 0
    ElMessage.success('门店可售状态已更新')
  } catch {
    ElMessage.error('更新门店可售状态失败')
    fetchProducts()
  }
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.categoryId = null
  form.description = ''
  form.imageUrl = ''
  form.isActive = 1
}

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (product) => {
  isEdit.value = true
  form.id = product.id
  form.name = product.name
  form.categoryId = product.categoryId
  form.description = product.description || ''
  form.imageUrl = product.imageUrl || ''
  form.isActive = product.isActive
  dialogVisible.value = true
}

const handleDelete = (product) => {
  ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' }).then(() => {
    axios.delete(`/api/admin/products/${product.id}`).then(() => {
      ElMessage.success('商品已删除')
      fetchProducts()
    })
  })
}

const handleImageUpload = async (options) => {
  const { file, onError, onSuccess } = options
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await axios.post('/api/admin/upload/image', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    form.imageUrl = res.data?.data || ''
    ElMessage.success('图片上传成功')
    onSuccess?.(res.data)
  } catch (error) {
    ElMessage.error('图片上传失败')
    console.error('Upload image failed', error)
    onError?.(error)
  }
}

const handleSubmit = async () => {
  try {
    const payload = { name: form.name, categoryId: form.categoryId, description: form.description, imageUrl: form.imageUrl, isActive: form.isActive }
    if (isEdit.value) {
      await axios.put(`/api/admin/products/${form.id}`, payload)
      ElMessage.success('商品更新成功')
    } else {
      await axios.post('/api/admin/products', payload)
      ElMessage.success('商品添加成功')
    }
    dialogVisible.value = false
    fetchProducts()
  } catch (error) {
    ElMessage.error('保存失败，请检查后端服务')
    console.error('Save product failed', error)
  }
}

onMounted(async () => {
  await fetchStores()
  await fetchCategories()
  await fetchProducts()
})
</script>

<style scoped>
.admin-wrapper { min-height: 100vh; display: flex; flex-direction: column; background-color: var(--el-bg-color-page); }
.page-container { flex: 1; display: flex; max-width: 1400px; margin: 0 auto; width: 100%; padding: 100px 20px 40px; gap: 24px; align-items: flex-start; box-sizing: border-box; }
.admin-content { flex: 1; min-width: 0; }
.content-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.page-title { margin: 0; }
.store-filter { min-width: 220px; }
.action-btn { margin-left: auto; }
.table-img, .preview-img { width: 64px; height: 64px; object-fit: cover; border-radius: 8px; }
.upload-wrapper { display: flex; align-items: center; gap: 12px; }
.price-text { color: var(--el-color-primary); font-weight: 600; }
</style>
