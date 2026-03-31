<template>
  <div class="admin-wrapper">
    <Navbar />
    
    <div class="page-container admin-layout">
      <Sidebar active="menu-mgmt" />
      
      <div class="admin-content with-sidebar">
        <div class="content-header">
          <h2 class="page-title">菜单管理</h2>
          <el-button type="primary" size="large" @click="handleAdd" class="action-btn">
            <el-icon><Plus /></el-icon>添加咖啡商品
          </el-button>
        </div>
        
        <el-card class="admin-card" shadow="never">
          <el-table :data="products" style="width: 100%" class="custom-table">
            <el-table-column label="图片" width="100">
              <template #default="scope">
                <img :src="scope.row.imageUrl || '/static/picture/xuanchuan1.jpg'" class="table-img" />
              </template>
            </el-table-column>
            <el-table-column label="名称" prop="name" min-width="150" />
            <el-table-column label="分类" width="160">
              <template #default="scope">
                <el-tag type="info" effect="light">{{ categoryMap[scope.row.categoryId] || '—' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="价格" width="100">
              <template #default="scope"><span class="price-text">¥ {{ scope.row.minPrice ?? '—' }}</span></template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.isActive === 1 ? 'success' : 'info'" effect="light" round>
                  {{ scope.row.isActive === 1 ? '在售' : '下架' }}
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
              @current-change="fetchProducts"
            />
          </div>
        </el-card>
        
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '添加商品'" width="550px" custom-class="coffee-dialog">
          <el-form :model="form" label-width="80px" class="admin-form">
            <el-form-item label="名称">
              <el-input v-model="form.name" placeholder="请输入商品名称" />
            </el-form-item>
            <el-form-item label="分类">
              <el-select v-model="form.categoryId" style="width: 100%;" placeholder="请选择商品分类">
                <el-option
                  v-for="c in categories"
                  :key="c.id"
                  :label="c.name"
                  :value="c.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="商品图片">
              <div class="upload-wrapper">
                <el-upload
                  :http-request="handleImageUpload"
                  :show-file-list="false"
                  accept="image/*"
                >
                  <el-button type="primary" plain>上传图片</el-button>
                </el-upload>
                <img v-if="form.imageUrl" :src="form.imageUrl" class="preview-img" />
              </div>
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="form.isActive" :active-value="1" :inactive-value="0" active-text="在售" inactive-text="下架" />
            </el-form-item>
            <el-form-item label="描述">
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入商品描述..." />
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

// 获取所有商品分类，并生成分类 ID 到名称的映射表。
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

// 分页获取后台商品列表。
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

// 打开新增商品弹窗，并重置商品表单。
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

// 自定义图片上传逻辑：把图片传到后端并回填返回的图片地址。
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

// 把现有商品数据回填到表单中，方便管理员修改。
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

// 删除商品前先让管理员确认，避免误删。
const handleDelete = (product) => {
  ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' }).then(() => {
    axios.delete(`/api/admin/products/${product.id}`).then(() => {
      ElMessage.success('商品已删除！')
      fetchProducts()
    })
  })
}

// 根据当前模式提交新增商品或更新商品的请求。
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

.table-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
}

.price-text {
  color: var(--el-color-primary);
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
}

.upload-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.preview-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
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

  .upload-wrapper {
    flex-wrap: wrap;
    align-items: flex-start;
  }
}
</style>
