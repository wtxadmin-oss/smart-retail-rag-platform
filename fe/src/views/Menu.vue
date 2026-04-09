<template>
  <div class="menu-wrapper">
    <Navbar />
    <div class="page-container">
      <div class="menu-header">
        <h2>Our Menu</h2>
        <div class="menu-filters">
          <el-select
            v-model="selectedStoreId"
            placeholder="Select Store"
            class="store-select"
            @change="handleStoreChange"
          >
            <el-option
              v-for="store in storeSelectionStore.stores"
              :key="store.id"
              :label="store.name"
              :value="store.id"
            />
          </el-select>
          <el-input
            v-model="searchQuery"
            placeholder="Search coffee..."
            class="search-input"
            :prefix-icon="Search"
            @input="handleFilterChange"
          />
          <el-select
            v-model="selectedCategory"
            placeholder="All Categories"
            class="category-select"
            clearable
            @change="handleFilterChange"
          >
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </div>
      </div>
      <p v-if="currentStore" class="store-tip">当前门店：{{ currentStore.name }} | {{ currentStore.address }}</p>

      <el-row :gutter="24">
        <el-col v-for="product in products" :key="product.id" :span="6" :xs="24" :sm="12" :md="8" :lg="6">
          <el-card class="coffee-card" @click="showDetail(product)">
            <div class="img-wrapper">
              <img :src="product.imageUrl || '/static/picture/xuanchuan1.jpg'" @error="handleImageError" />
            </div>
            <div class="card-content">
              <h3>{{ product.name }}</h3>
              <p class="desc">{{ product.description }}</p>
              <div class="card-footer">
                <span class="price">¥ {{ product.minPrice || '--' }}</span>
                <el-button v-if="!isAdmin" type="primary" circle class="add-btn" @click.stop="showDetail(product)">
                  <el-icon><Plus /></el-icon>
                </el-button>
                <el-button v-else type="primary" plain class="view-btn" @click.stop="showDetail(product)">
                  查看详情
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div class="pagination-wrapper" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          @current-change="fetchProducts"
        />
      </div>

      <el-empty v-if="products.length === 0" description="No coffee found" class="empty-state" />
    </div>
    <Footer />

    <el-dialog v-model="detailVisible" :title="selectedProduct?.name" width="650px" custom-class="coffee-dialog" destroy-on-close>
      <div v-if="selectedProduct" class="dialog-content">
        <img :src="selectedProduct.imageUrl || '/static/picture/xuanchuan1.jpg'" class="dialog-img" @error="handleImageError" />
        <div class="dialog-info">
          <h3>{{ selectedProduct.name }}</h3>
          <p class="dialog-desc">{{ selectedProduct.description }}</p>

          <div class="spec-section">
            <h4>Select Size & Spec</h4>
            <el-radio-group v-model="selectedSkuId">
              <el-radio-button v-for="sku in skus" :key="sku.id" :label="sku.id">
                {{ sku.specName }}
              </el-radio-button>
            </el-radio-group>
          </div>

          <div class="price-section">
            <span class="currency">¥</span>
            <span class="amount">{{ currentPrice }}</span>
          </div>

          <el-button
            v-if="!isAdmin"
            type="primary"
            size="large"
            class="submit-btn"
            :disabled="!selectedSkuId"
            @click="addToCartWithTip"
          >
            加入购物车
          </el-button>
          <el-button v-else plain size="large" class="submit-btn">
            管理员仅浏览菜单
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import axios from 'axios'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
import { useCartStore } from '../store/cart'
import { useStoreSelectionStore } from '../store/store'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const cartStore = useCartStore()
const storeSelectionStore = useStoreSelectionStore()
const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)
const currentStore = computed(() => storeSelectionStore.currentStore)
const selectedStoreId = ref(null)
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
  const sku = skus.value.find(item => item.id === selectedSkuId.value)
  return sku ? sku.price : (selectedProduct.value?.minPrice || '--')
})

const handleImageError = (event) => {
  event.target.src = '/static/picture/xuanchuan1.jpg'
}

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/categories')
    categories.value = res.data?.data || []
  } catch (error) {
    ElMessage.error('获取分类失败，请检查后端连接')
    console.error('Fetch categories failed', error)
  }
}

const fetchProducts = async () => {
  try {
    if (!storeSelectionStore.currentStoreId) {
      products.value = []
      total.value = 0
      return
    }
    const res = await axios.get('/api/products', {
      params: {
        storeId: storeSelectionStore.currentStoreId,
        keyword: searchQuery.value,
        categoryId: selectedCategory.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    const pageData = res.data?.data || {}
    products.value = pageData.list || []
    total.value = pageData.total || 0
  } catch (error) {
    ElMessage.error('获取商品失败，请确认后端运行正常')
    console.error('Fetch products failed', error)
  }
}

const showDetail = async (product) => {
  if (!storeSelectionStore.currentStoreId) {
    ElMessage.warning('请先选择门店')
    return
  }
  selectedProduct.value = product
  detailVisible.value = true
  selectedSkuId.value = null
  skus.value = []
  try {
    const res = await axios.get(`/api/products/${product.id}/skus`)
    skus.value = res.data?.data || []
    if (skus.value.length > 0) {
      selectedSkuId.value = skus.value[0].id
    }
  } catch (error) {
    ElMessage.error('获取规格失败')
    console.error('Fetch skus failed', error)
  }
}

const addToCartWithTip = () => {
  if (isAdmin.value) {
    ElMessage.warning('管理员账号不支持购物车')
    return
  }
  const sku = skus.value.find(item => item.id === selectedSkuId.value)
  if (!sku) {
    ElMessage.warning('请选择规格')
    return
  }

  cartStore.addToCart({
    ...selectedProduct.value,
    storeId: storeSelectionStore.currentStoreId,
    storeName: storeSelectionStore.currentStore?.name || '',
    skuId: sku.id,
    specName: sku.specName,
    price: sku.price
  })

  ElMessage.success(`已加入购物车：${selectedProduct.value.name} (${sku.specName})`)
  detailVisible.value = false
}

const handleStoreChange = (storeId) => {
  storeSelectionStore.setCurrentStoreById(storeId)
}

const handleFilterChange = () => {
  currentPage.value = 1
  fetchProducts()
}

onMounted(async () => {
  await fetchCategories()
  await storeSelectionStore.ensureCurrentStore()
  selectedStoreId.value = storeSelectionStore.currentStoreId
  await fetchProducts()
})

watch(() => storeSelectionStore.currentStoreId, (value) => {
  selectedStoreId.value = value
  currentPage.value = 1
  fetchProducts()
})
</script>

<style scoped>
.menu-wrapper {
  background-color: var(--el-bg-color-page);
  min-height: 100vh;
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color);
}

.menu-header h2 {
  font-family: serif;
  color: var(--el-color-primary);
  font-size: 32px;
  margin: 0;
}

.menu-filters {
  display: flex;
  gap: 16px;
}

.search-input {
  width: 240px;
}

.store-select,
.category-select {
  min-width: 220px;
}

.store-tip {
  margin: -12px 0 24px;
  color: var(--el-text-color-secondary);
}

.img-wrapper {
  overflow: hidden;
  height: 220px;
}

.img-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.coffee-card:hover .img-wrapper img {
  transform: scale(1.05);
}

.card-content {
  padding: 20px;
}

.card-content h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: var(--el-text-color-primary);
}

.card-content .desc {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  height: 36px;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  color: var(--el-color-primary);
  font-size: 20px;
  font-weight: 600;
}

.add-btn {
  background-color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  transition: transform 0.2s;
}

.view-btn {
  border-radius: 20px;
}

.add-btn:hover {
  transform: scale(1.1);
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.dialog-content {
  display: flex;
  gap: 30px;
}

.dialog-img {
  width: 280px;
  height: 280px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.dialog-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.dialog-info h3 {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-family: serif;
}

.dialog-desc {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 24px;
}

.spec-section h4 {
  margin: 0 0 12px 0;
  font-size: 15px;
  color: var(--el-text-color-primary);
}

.price-section {
  margin-top: auto;
  margin-bottom: 20px;
  color: var(--el-color-primary);
}

.currency {
  font-size: 18px;
  font-weight: 600;
  margin-right: 4px;
}

.amount {
  font-size: 32px;
  font-weight: 700;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 24px;
}

@media (max-width: 900px) {
  .menu-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }

  .menu-filters {
    flex-direction: column;
  }

  .search-input,
  .store-select,
  .category-select {
    width: 100%;
  }

  .dialog-content {
    flex-direction: column;
  }

  .dialog-img {
    width: 100%;
    height: 240px;
  }
}
</style>
