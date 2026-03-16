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
        <Sidebar active="spec-mgmt" />
        <el-main>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 style="margin: 0;">规格管理</h2>
            <div style="display: flex; align-items: center; gap: 12px;">
              <el-select v-model="selectedProductId" filterable placeholder="请选择商品" style="width: 260px;" @change="fetchSkus">
                <el-option v-for="p in products" :key="p.id" :label="p.name" :value="p.id" />
              </el-select>
              <el-button type="primary" :disabled="!selectedProductId" @click="handleAdd">添加规格</el-button>
            </div>
          </div>
          
          <el-table :data="skus" style="width: 100%">
            <el-table-column label="ID" prop="id" width="80" />
            <el-table-column label="商品ID" prop="productId" width="100" />
            <el-table-column label="规格名" prop="specName" width="200" />
            <el-table-column label="价格" width="120">
              <template #default="scope">¥ {{ scope.row.price }}</template>
            </el-table-column>
            <el-table-column label="库存" prop="stock" width="120" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑规格' : '添加规格'" width="450px">
            <el-form :model="form" label-width="80px">
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

const handleAdd = () => {
  isEdit.value = false
  form.id = null
  form.productId = selectedProductId.value
  form.specName = ''
  form.price = 0
  form.stock = 0
  dialogVisible.value = true
}

const handleEdit = (sku) => {
  isEdit.value = true
  form.id = sku.id
  form.productId = sku.productId
  form.specName = sku.specName
  form.price = sku.price
  form.stock = sku.stock
  dialogVisible.value = true
}

const handleDelete = (sku) => {
  ElMessageBox.confirm('确定删除该规格吗？', '提示', { type: 'warning' }).then(() => {
    axios.delete(`/api/skus/${sku.id}`).then(() => {
      ElMessage.success('规格已删除！')
      fetchSkus()
    })
  })
}

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
