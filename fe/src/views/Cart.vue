<template>
  <div class="cart-wrapper">
    <Navbar />
    <div class="page-container">
      <div class="cart-content">
        <h2 class="page-title">{{ userStore.isLoggedIn ? '我的购物车' : '游客购物车' }}</h2>
        <p v-if="!userStore.isLoggedIn" class="guest-tip">游客可先加入购物车，登录后会自动同步购物车内容。</p>
        <div v-if="cartStore.items.length > 0" class="store-banner">
          <span class="store-banner-label">当前门店</span>
          <strong>{{ cartStore.storeName || currentStore?.name || '未选择门店' }}</strong>
        </div>
        <el-card class="cart-card" shadow="never">
          <el-table :data="cartStore.items" style="width: 100%" class="custom-table">
            <el-table-column label="商品" min-width="260">
              <template #default="scope">
                <div class="product-info">
                  <img :src="scope.row.imageUrl || '/static/picture/xuanchuan1.jpg'" class="product-img" @error="handleImageError" />
                  <div>
                    <div class="product-name">{{ scope.row.name }}</div>
                    <div class="product-spec" v-if="scope.row.specName">{{ scope.row.specName }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120"><template #default="scope"><span class="price-text">¥ {{ scope.row.price }}</span></template></el-table-column>
            <el-table-column label="数量" width="200"><template #default="scope"><el-input-number v-model="scope.row.quantity" :min="1" @change="(val) => cartStore.updateQuantity(scope.row.id, val)" /></template></el-table-column>
            <el-table-column label="小计" width="120"><template #default="scope"><span class="subtotal-text">¥ {{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span></template></el-table-column>
            <el-table-column label="操作" width="100"><template #default="scope"><el-button type="danger" text @click="cartStore.removeFromCart(scope.row.id)">删除</el-button></template></el-table-column>
          </el-table>
          <div v-if="cartStore.items.length > 0" class="cart-footer">
            <div class="total-price-wrapper"><span>合计:</span><span class="total-price">¥ {{ cartStore.totalPrice.toFixed(2) }}</span></div>
            <el-button type="primary" size="large" class="checkout-btn" @click="handleCheckout">去结算</el-button>
          </div>
          <el-empty v-else description="购物车空空如也" class="empty-cart"><el-button type="primary" @click="router.push('/menu')">去挑选咖啡</el-button></el-empty>
        </el-card>
        <el-dialog v-model="checkoutVisible" title="填写收货信息" width="500px" custom-class="coffee-dialog">
          <el-form :model="checkoutForm" label-width="90px">
            <el-form-item label="收货人"><el-input v-model="checkoutForm.receiverName" /></el-form-item>
            <el-form-item label="联系电话"><el-input v-model="checkoutForm.receiverPhone" /></el-form-item>
            <el-form-item label="收货地址"><el-input v-model="checkoutForm.receiverAddress" /></el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="checkoutVisible = false">取消</el-button>
            <el-button type="primary" @click="submitOrder">提交订单</el-button>
          </template>
        </el-dialog>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
import { useCartStore } from '../store/cart'
import { useStoreSelectionStore } from '../store/store'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import axios from 'axios'

const router = useRouter()
const cartStore = useCartStore()
const storeSelectionStore = useStoreSelectionStore()
const userStore = useUserStore()
const currentStore = computed(() => storeSelectionStore.currentStore)
const checkoutVisible = ref(false)
const checkoutForm = ref({ receiverName: '', receiverPhone: '', receiverAddress: '' })

const handleImageError = (event) => {
  event.target.src = '/static/picture/xuanchuan1.jpg'
}

const handleCheckout = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录再结算')
    router.push('/login')
    return
  }
  if (cartStore.items.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  if (!cartStore.storeId) {
    ElMessage.warning('请先选择门店')
    return
  }
  checkoutVisible.value = true
}

const submitOrder = async () => {
  try {
    const order = {
      userId: userStore.userInfo.id,
      storeId: cartStore.storeId,
      receiverName: checkoutForm.value.receiverName,
      receiverPhone: checkoutForm.value.receiverPhone,
      receiverAddress: checkoutForm.value.receiverAddress,
      items: cartStore.items.map(item => ({ productId: item.productId, productName: item.name, productImage: item.imageUrl || '', skuSpec: item.specName || '', price: item.price, quantity: item.quantity }))
    }
    const res = await axios.post('/api/orders', order)
    if (res.data?.code === 0) {
      ElMessage.success('下单成功')
      checkoutVisible.value = false
      await cartStore.clearCart()
      router.push('/orders')
      return
    }
    ElMessage.error(res.data?.message || '下单失败')
  } catch (error) {
    ElMessage.error('下单失败，请检查后端服务')
    console.error('Place order failed', error)
  }
}
</script>

<style scoped>
.cart-wrapper { min-height: 100vh; display: flex; flex-direction: column; background-color: var(--el-bg-color-page); }
.cart-content { padding: 40px 0; max-width: 1000px; margin: 0 auto; }
.page-title { font-size: 28px; margin-bottom: 24px; text-align: center; font-weight: 600; }
.guest-tip { margin: -8px 0 20px; text-align: center; color: var(--el-text-color-secondary); font-size: 14px; }
.store-banner { display: flex; align-items: center; gap: 12px; margin: 0 0 16px; padding: 14px 18px; background: var(--el-color-primary-light-9); border-radius: 10px; }
.store-banner-label { color: var(--el-text-color-secondary); }
.cart-card { border-radius: 12px; border: 1px solid var(--el-border-color-light); }
.product-info { display: flex; align-items: center; gap: 16px; }
.product-img { width: 72px; height: 72px; object-fit: cover; border-radius: 8px; }
.product-name { font-weight: 500; font-size: 16px; }
.product-spec { font-size: 12px; color: var(--el-text-color-secondary); margin-top: 2px; }
.price-text, .subtotal-text, .total-price { color: var(--el-color-primary); font-weight: 600; }
.cart-footer { display: flex; align-items: center; justify-content: space-between; padding-top: 24px; }
.checkout-btn { min-width: 140px; }
</style>
