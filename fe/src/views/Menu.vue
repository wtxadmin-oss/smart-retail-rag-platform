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
        <Sidebar active="menu" />
        <el-main>
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
            <h2 style="margin: 0;">咖啡菜单</h2>
            <div style="display: flex; gap: 12px;">
              <el-input v-model="searchQuery" placeholder="搜索咖啡..." style="width: 200px;" @input="fetchProducts" />
              <el-select v-model="selectedCategory" placeholder="选择分类" style="width: 150px;" clearable @change="fetchProducts">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </div>
          </div>
          
          <el-row :gutter="20">
            <el-col v-for="product in products" :key="product.id" :span="6">
              <el-card :body-style="{ padding: '0px' }" style="margin-bottom: 20px; cursor: pointer;" @click="showDetail(product)">
                <img :src="product.imageUrl || '/static/picture/xuanchuan1.jpg'" style="width: 100%; height: 180px; object-fit: cover;" />
                <div style="padding: 14px;">
                  <span style="font-weight: bold;">{{ product.name }}</span>
                  <div style="margin-top: 8px; display: flex; justify-content: space-between; align-items: center;">
                    <span style="color: #f56c6c; font-size: 18px;">¥ {{ product.minPrice || '—' }} 起</span>
                    <el-button type="primary" size="small" circle @click.stop="showDetail(product)">
                      <el-icon><Plus /></el-icon>
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          
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
          
          <el-empty v-if="products.length === 0" description="暂无咖啡，换个搜索词试试" />
          
          <Footer />
        </el-main>
      </el-container>
    </el-container>
    
    <el-dialog v-model="detailVisible" :title="selectedProduct?.name" width="600px">
      <div v-if="selectedProduct" style="display: flex; gap: 20px;">
        <img :src="selectedProduct.imageUrl || '/static/picture/xuanchuan1.jpg'" style="width: 250px; height: 250px; object-fit: cover; border-radius: 8px;" />
        <div style="flex: 1;">
          <h3 style="margin-top: 0;">{{ selectedProduct.name }}</h3>
          <p style="color: #606266; font-size: 14px;">{{ selectedProduct.description }}</p>
          
          <div style="margin-top: 20px;">
            <p style="margin-bottom: 8px; font-weight: bold;">选择规格</p>
            <el-radio-group v-model="selectedSkuId">
              <el-radio-button v-for="sku in skus" :key="sku.id" :label="sku.id">
                {{ sku.specName }}
              </el-radio-button>
            </el-radio-group>
          </div>

          <div style="margin-top: 20px;">
            <span style="color: #f56c6c; font-size: 24px; font-weight: bold;">¥ {{ currentPrice }}</span>
          </div>
          
          <div style="margin-top: 30px;">
            <el-button type="primary" size="large" style="width: 100%;" :disabled="!selectedSkuId || isAdmin" @click="addToCartWithTip()">加入购物车</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'
import Footer from '../components/Footer.vue'
import Sidebar from '../components/Sidebar.vue'
import { useCartStore } from '../store/cart'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)
const active = ref("menu")
const searchQuery = ref('')
const selectedCategory = ref(null)
const categories = ref([])
const products = ref([])
const selectedProduct = ref(null)
const detailVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const skus = ref([])
const selectedSkuId = ref(null)
const currentPrice = computed(() => {
  const sku = skus.value.find(s => s.id === selectedSkuId.value)
  return sku ? sku.price : (selectedProduct.value?.minPrice || '—')
})

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/categories')
    categories.value = res.data.data
  } catch (e) {
    ElMessage.error('获取分类失败，请检查后端连接')
    console.error('Fetch categories failed', e)
  }
}

const fetchProducts = async () => {
  try {
    const res = await axios.get('/api/products', {
      params: {
        keyword: searchQuery.value,
        categoryId: selectedCategory.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    const pageData = res.data.data
    products.value = pageData.list
    total.value = pageData.total
  } catch (e) {
    ElMessage.error('获取商品失败，请确认后端运行正常')
    console.error('Fetch products failed', e)
  }
}

const showDetail = async (product) => {
  selectedProduct.value = product
  detailVisible.value = true
  selectedSkuId.value = null
  skus.value = []
  try {
    const res = await axios.get(`/api/products/${product.id}/skus`)
    skus.value = res.data.data
    if (skus.value.length > 0) {
      selectedSkuId.value = skus.value[0].id
    }
  } catch (e) {
    ElMessage.error('获取规格失败')
  }
}

const addToCartWithTip = () => {
  if (isAdmin.value) {
    ElMessage.warning('管理员账号不支持购物车')
    return
  }
  const sku = skus.value.find(s => s.id === selectedSkuId.value)
  if (!sku) {
    ElMessage.warning('请选择规格')
    return
  }
  
  cartStore.addToCart({
    ...selectedProduct.value,
    skuId: sku.id,
    specName: sku.specName,
    price: sku.price
  })
  
  ElMessage.success(`已添加 ${selectedProduct.value.name} (${sku.specName}) 到购物车`)
  detailVisible.value = false
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>
