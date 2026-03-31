<template>
  <div class="menu-wrapper">
    <Navbar />
    
    <div class="page-container">
      <div class="menu-header">
        <h2>Our Menu</h2>
        <div class="menu-filters">
          <el-input 
            v-model="searchQuery" 
            placeholder="Search coffee..." 
            class="search-input"
            prefix-icon="Search"
            @input="fetchProducts" 
          />
          <el-select 
            v-model="selectedCategory" 
            placeholder="All Categories" 
            class="category-select"
            clearable 
            @change="fetchProducts">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </div>
      </div>
      
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
                <span class="price">¥ {{ product.minPrice || '—' }}</span>
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
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          @current-change="fetchProducts"
        />
      </div>
      
      <el-empty v-if="products.length === 0" description="No coffee found" class="empty-state" />
      
    </div>
    <Footer />
    
    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" :title="selectedProduct?.name" width="650px" custom-class="coffee-dialog" destroy-on-close>
      <div v-if="selectedProduct" class="dialog-content">
        <img :src="selectedProduct.imageUrl || '/static/picture/xuanchuan1.jpg'" class="dialog-img" @error="handleImageError" />
        <div class="dialog-info">
          <h3>{{ selectedProduct.name }}</h3>
          <p class="dialog-desc">{{ selectedProduct.description }}</p>
          
          <div class="spec-section">
            <h4>Select Size & Spec</h4>
            <el-radio-group v-model="selectedSkuId">
              <el-radio-button v-for="sku in skus" :key="sku.id" :label="sku.id" border>
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
            @click="addToCartWithTip()"
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
import { ref, onMounted, computed } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import axios from 'axios'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
import { useCartStore } from '../store/cart'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'

const cartStore = useCartStore()
const userStore = useUserStore()
// 标记当前登录用户是否为管理员，用于控制是否展示加购能力。
const isAdmin = computed(() => userStore.isAdmin)
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
// 根据当前选中的规格，实时计算弹窗中展示的价格。
const currentPrice = computed(() => {
  const sku = skus.value.find(s => s.id === selectedSkuId.value)
  return sku ? sku.price : (selectedProduct.value?.minPrice || '—')
})

// 当商品图片失效时，回退到默认宣传图。
const handleImageError = (event) => {
  event.target.src = '/static/picture/xuanchuan1.jpg'
}

// 拉取商品分类数据，用于顶部筛选下拉框。
const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/categories')
    categories.value = res.data.data
  } catch (e) {
    ElMessage.error('获取分类失败，请检查后端连接')
    console.error('Fetch categories failed', e)
  }
}

// 根据搜索词、分类和分页条件请求商品列表。
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

// 打开商品详情弹窗，并同步加载该商品的所有规格。
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

// 校验规格和角色后，把当前商品与规格加入购物车并给出提示。
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

/* Dialog Styles */
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
</style>
