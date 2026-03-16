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
        <Sidebar active="menu-mgmt" />
        <el-main>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 style="margin: 0;">菜单管理</h2>
            <el-button type="primary" @click="handleAdd">添加咖啡商品</el-button>
          </div>
          
          <el-table :data="products" style="width: 100%">
            <el-table-column label="图片" width="100">
              <template #default="scope">
                <img :src="scope.row.imageUrl || '/static/picture/xuanchuan1.jpg'" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;" />
              </template>
            </el-table-column>
            <el-table-column label="名称" prop="name" width="150" />
            <el-table-column label="分类" width="160">
              <template #default="scope">
                {{ categoryMap[scope.row.categoryId] || '—' }}
              </template>
            </el-table-column>
            <el-table-column label="价格" width="100">
              <template #default="scope">¥ {{ scope.row.minPrice ?? '—' }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isActive === 1 ? 'success' : 'info'">{{ scope.row.isActive === 1 ? '在售' : '下架' }}</el-tag>
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
            @current-change="fetchProducts"
            style="margin-top: 20px; display: flex; justify-content: center;"
          />
          
          <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '添加商品'" width="500px">
            <el-form :model="form" label-width="80px">
              <el-form-item label="名称">
                <el-input v-model="form.name" />
              </el-form-item>
              <el-form-item label="分类">
                <el-select v-model="form.categoryId" style="width: 100%;">
                  <el-option
                    v-for="c in categories"
                    :key="c.id"
                    :label="c.name"
                    :value="c.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="商品图片">
                <div style="display:flex; align-items:center; gap:12px;">
                  <el-upload
                    :http-request="handleImageUpload"
                    :show-file-list="false"
                    accept="image/*"
                  >
                    <el-button type="primary">上传图片</el-button>
                  </el-upload>
                  <img v-if="form.imageUrl" :src="form.imageUrl" style="width: 60px; height: 60px; object-fit: cover; border-radius:4px;" />
                </div>
              </el-form-item>
              <el-form-item label="状态">
                <el-switch v-model="form.isActive" :active-value="1" :inactive-value="0" />
              </el-form-item>
              <el-form-item label="描述">
                <el-input v-model="form.description" type="textarea" :rows="3" />
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
const products = ref([])
const categories = ref([])
const categoryMap = ref({})
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  description: '',
  imageUrl: '',
  isActive: 1
})

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/categories')
    categories.value = res.data.data || []
    categoryMap.value = Object.fromEntries(categories.value.map(c => [c.id, c.name]))
  } catch (e) {
    ElMessage.error('获取分类失败，请检查后端服务')
    console.error('Fetch categories failed', e)
  }
}

const fetchProducts = async () => {
  try {
    const res = await axios.get('/api/admin/products', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    const pageData = res.data.data
    products.value = pageData.list
    total.value = pageData.total
  } catch (e) {
    ElMessage.error('获取商品失败，请检查后端服务')
    console.error('Fetch products failed', e)
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.categoryId = null
  form.description = ''
  form.imageUrl = ''
  form.isActive = 1
  dialogVisible.value = true
}

const handleImageUpload = async (options) => {
  const { file, onError, onSuccess } = options
  const fd = new FormData()
  fd.append('file', file)
  try {
    const res = await axios.post('/api/admin/upload/image', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    const url = res.data.data
    form.imageUrl = url
    ElMessage.success('图片上传成功')
    onSuccess && onSuccess(res.data)
  } catch (e) {
    ElMessage.error('图片上传失败')
    console.error('Upload image failed', e)
    onError && onError(e)
  }
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
      ElMessage.success('商品已删除！')
      fetchProducts()
    })
  })
}

const handleSubmit = async () => {
  try {
    const payload = {
      name: form.name,
      categoryId: form.categoryId,
      description: form.description,
      imageUrl: form.imageUrl,
      isActive: form.isActive
    }
    if (isEdit.value) {
      await axios.put(`/api/admin/products/${form.id}`, payload)
      ElMessage.success('商品更新成功！')
    } else {
      await axios.post('/api/admin/products', payload)
      ElMessage.success('商品添加成功！')
    }
    dialogVisible.value = false
    fetchProducts()
  } catch (e) {
    ElMessage.error('保存失败，请检查后端服务')
    console.error('Save product failed', e)
  }
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>
