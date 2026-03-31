<template>
  <div class="admin-wrapper">
    <Navbar />
    
    <div class="page-container admin-layout">
      <Sidebar active="spec-mgmt" />
      
      <div class="admin-content with-sidebar">
        <div class="content-header">
          <h2 class="page-title">规格管理</h2>
          <div class="header-actions">
            <el-select v-model="selectedProductId" filterable placeholder="请选择商品" class="product-select" @change="fetchSkus">
              <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
            </el-select>
            <el-button type="primary" size="large" :disabled="!selectedProductId" @click="handleAdd" class="action-btn">
              <el-icon><Plus /></el-icon>添加规格
            </el-button>
          </div>
        </div>
        
        <el-card class="admin-card" shadow="never">
          <el-table :data="skus" style="width: 100%" class="custom-table">
            <el-table-column label="ID" prop="id" width="80" />
            <el-table-column label="商品ID" prop="productId" width="100" />
            <el-table-column label="规格名" prop="specName" min-width="200" />
            <el-table-column label="价格" width="120">
              <template #default="scope"><span class="price-text">¥ {{ scope.row.price }}</span></template>
            </el-table-column>
            <el-table-column label="库存" prop="stock" width="120" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button type="primary" text @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="danger" text @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑规格' : '添加规格'" width="450px" custom-class="coffee-dialog">
          <el-form :model="form" label-width="80px" class="admin-form">
            <el-form-item label="规格名">
              <el-input v-model="form.specName" placeholder="如：中杯 / 大杯 / 热 / 少冰 ..." />
            </el-form-item>
            <el-form-item label="价格">
              <el-input-number v-model="form.price" :precision="2" :step="0.5" style="width: 100%;" />
            </el-form-item>
            <el-form-item label="库存">
              <el-input-number v-model="form.stock" :min="0" :step="1" style="width: 100%;" />
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
const selectedProductId = ref(null)
const skus = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  productId: null,
  specName: '',
  price: 0,
  stock: 0
})

// 先拉取商品列表，为规格管理页面提供商品下拉选项。
const fetchProducts = async () => {
  try {
    const res = await axios.get('/api/admin/products', { params: { pageNum: 1, pageSize: 2000 } })
    const pageData = res.data.data
    products.value = pageData.list
    if (!selectedProductId.value && products.value.length > 0) {
      selectedProductId.value = products.value[0].id
      fetchSkus()
    }
  } catch (e) {
    ElMessage.error('获取商品列表失败，请检查后端服务')
    console.error('Fetch products failed', e)
  }
}

// 根据当前选中的商品，加载它的规格列表。
const fetchSkus = async () => {
  if (!selectedProductId.value) return
  try {
    const res = await axios.get(`/api/skus/by-product/${selectedProductId.value}`)
    skus.value = res.data.data || []
  } catch (e) {
    ElMessage.error('获取规格失败，请检查后端服务')
    console.error('Fetch skus failed', e)
  }
}

// 打开新增规格弹窗，并把当前商品 ID 带入表单。
const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.productId = selectedProductId.value
  form.specName = ''
  form.price = 0
  form.stock = 0
  dialogVisible.value = true
}

// 把当前规格数据回填到表单中，用于编辑。
const handleEdit = (sku) => {
  isEdit.value = true
  form.id = sku.id
  form.productId = sku.productId
  form.specName = sku.specName
  form.price = sku.price
  form.stock = sku.stock
  dialogVisible.value = true
}

// 删除规格前进行确认，确认后刷新列表。
const handleDelete = (sku) => {
  ElMessageBox.confirm('确定删除该规格吗？', '提示', { type: 'warning' }).then(() => {
    axios.delete(`/api/skus/${sku.id}`).then(() => {
      ElMessage.success('规格已删除！')
      fetchSkus()
    })
  })
}

// 提交规格表单，根据模式决定是新增还是更新规格。
const handleSubmit = async () => {
  try {
    const payload = {
      productId: form.productId,
      specName: form.specName,
      price: form.price,
      stock: form.stock
    }
    if (isEdit.value) {
      await axios.put(`/api/skus/${form.id}`, payload)
      ElMessage.success('规格更新成功！')
    } else {
      await axios.post('/api/skus', payload)
      ElMessage.success('规格添加成功！')
    }
    dialogVisible.value = false
    fetchSkus()
  } catch (e) {
    ElMessage.error('保存失败，请检查后端服务')
    console.error('Save sku failed', e)
  }
}

onMounted(() => {
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.product-select {
  width: 260px;
  max-width: 100%;
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

.price-text {
  color: var(--el-color-primary);
  font-weight: 600;
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
    flex-wrap: wrap;
  }

  .product-select {
    width: 100%;
  }
}
</style>
