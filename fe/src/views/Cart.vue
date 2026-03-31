<template>
  <div class="cart-wrapper">
    <Navbar />
    
    <div class="page-container">
      <div class="cart-content">
        <h2 class="page-title">{{ userStore.isLoggedIn ? '我的购物车' : '游客购物车' }}</h2>
        <p v-if="!userStore.isLoggedIn" class="guest-tip">游客可先加入购物车，登录后会自动同步购物车内容</p>
        <el-card class="cart-card" shadow="never">
          <el-table :data="cartStore.items" style="width: 100%" class="custom-table">
            <el-table-column label="商品" min-width="300">
              <template #default="scope">
                <div class="product-info">
                  <img :src="scope.row.imageUrl || '/static/picture/xuanchuan1.jpg'" class="product-img" @error="handleImageError" />
                  <span class="product-name">{{ scope.row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="scope">
                <span class="price-text">¥ {{ scope.row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column label="数量" width="200">
              <template #default="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" @change="(val) => cartStore.updateQuantity(scope.row.id, val)" />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="scope">
                <span class="subtotal-text">¥ {{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button type="danger" text @click="cartStore.removeFromCart(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div v-if="cartStore.items.length > 0" class="cart-footer">
            <div class="total-price-wrapper">
              <span>合计:</span>
              <span class="total-price">¥ {{ cartStore.totalPrice.toFixed(2) }}</span>
            </div>
            <el-button type="primary" size="large" class="checkout-btn" @click="handleCheckout">去结算</el-button>
          </div>
          <el-empty v-else description="购物车空空如也" class="empty-cart">
            <el-button type="primary" @click="router.push('/menu')">去挑选咖啡</el-button>
          </el-empty>
        </el-card>
        
        <el-dialog v-model="checkoutVisible" title="填写收货信息" width="500px" custom-class="coffee-dialog">
          <el-form :model="checkoutForm" label-width="90px">
            <el-form-item label="收货人">
              <el-input v-model="checkoutForm.receiverName" />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="checkoutForm.receiverPhone" />
            </el-form-item>
            <el-form-item label="收货地址">
              <el-input v-model="checkoutForm.receiverAddress" />
            </el-form-item>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import Footer from '../components/Footer.vue'
import { useCartStore } from '../store/cart'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store/user'
import axios from 'axios'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const checkoutVisible = ref(false)
const checkoutForm = ref({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: ''
})

// 当商品图片加载失败时，自动切换为默认宣传图。
const handleImageError = (event) => {
  event.target.src = '/static/picture/xuanchuan1.jpg'
}

// 点击结算时，先校验登录状态和购物车是否为空，再打开收货信息弹窗。
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
  checkoutVisible.value = true
}

// 根据购物车内容组装订单数据，提交到后端并在成功后清空购物车。
const submitOrder = async () => {
  try {
    const order = {
      userId: userStore.userInfo.id,
      totalAmount: cartStore.totalPrice,
      status: 0,
      receiverName: checkoutForm.value.receiverName,
      receiverPhone: checkoutForm.value.receiverPhone,
      receiverAddress: checkoutForm.value.receiverAddress,
      items: cartStore.items.map(it => ({
        productId: it.productId,
        productName: it.name,
        productImage: it.imageUrl || '',
        skuSpec: it.specName || '',
        price: it.price,
        quantity: it.quantity
      }))
    }
    const res = await axios.post('/api/orders', order)
    if (res.data?.code === 0) {
      ElMessage.success('下单成功')
      checkoutVisible.value = false
      await cartStore.clearCart()
      router.push('/orders')
    } else {
      ElMessage.error(res.data?.message || '下单失败')
    }
  } catch (e) {
    ElMessage.error('下单失败，请检查后端服务')
    console.error('Place order failed', e)
  }
}
</script>

<style scoped>
.cart-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color-page);
}

.cart-content {
  padding: 40px 0;
  max-width: 1000px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  color: var(--el-text-color-primary);
  margin-bottom: 24px;
  text-align: center;
  font-weight: 600;
}

.guest-tip {
  margin: -8px 0 20px;
  text-align: center;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.cart-card {
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--el-border-color-light);
  background-color: #fff;
}

.custom-table {
  --el-table-border-color: var(--el-border-color-light);
  --el-table-header-bg-color: var(--el-bg-color-page);
  --el-table-header-text-color: var(--el-text-color-primary);
}

.product-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.product-img {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
}

.product-name {
  font-weight: 500;
  color: var(--el-text-color-primary);
  font-size: 16px;
}

.price-text {
  color: var(--el-text-color-regular);
  font-size: 15px;
}

.subtotal-text {
  color: var(--el-color-primary);
  font-weight: 600;
  font-size: 16px;
}

.cart-footer {
  margin-top: 30px;
  padding-top: 24px;
  border-top: 1px solid var(--el-border-color-light);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 32px;
}

.total-price-wrapper {
  display: flex;
  align-items: baseline;
  gap: 12px;
  font-size: 16px;
  color: var(--el-text-color-regular);
}

.total-price {
  color: var(--el-color-primary);
  font-weight: bold;
  font-size: 28px;
}

.checkout-btn {
  padding: 12px 36px;
  font-size: 16px;
  border-radius: 8px;
}

.empty-cart {
  padding: 60px 0;
}

:deep(.coffee-dialog) {
  border-radius: 12px;
}
</style>
