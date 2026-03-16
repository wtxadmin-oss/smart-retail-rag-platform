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
        <Sidebar active="cart" />
        <el-main>
          <h2>我的购物车</h2>
          <el-table :data="cartStore.items" style="width: 100%">
            <el-table-column label="商品" width="400">
              <template #default="scope">
                <div style="display: flex; align-items: center; gap: 12px;">
                  <img :src="scope.row.imageUrl || '/static/picture/xuanchuan1.jpg'" style="width: 60px; height: 60px; object-fit: cover; border-radius: 4px;" />
                  <span>{{ scope.row.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="单价" width="120">
              <template #default="scope">
                ¥ {{ scope.row.price }}
              </template>
            </el-table-column>
            <el-table-column label="数量" width="200">
              <template #default="scope">
                <el-input-number v-model="scope.row.quantity" :min="1" @change="(val) => cartStore.updateQuantity(scope.row.id, val)" />
              </template>
            </el-table-column>
            <el-table-column label="小计" width="120">
              <template #default="scope">
                ¥ {{ scope.row.price * scope.row.quantity }}
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="danger" size="small" @click="cartStore.removeFromCart(scope.row.id)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div v-if="cartStore.items.length > 0" style="margin-top: 30px; display: flex; justify-content: flex-end; align-items: center; gap: 20px;">
            <div style="font-size: 18px;">
              合计: <span style="color: #f56c6c; font-weight: bold; font-size: 24px;">¥ {{ cartStore.totalPrice }}</span>
            </div>
            <el-button type="primary" size="large" @click="handleCheckout">去结算</el-button>
          </div>
          <el-empty v-else description="购物车空空如也" />
          
          <el-dialog v-model="checkoutVisible" title="填写收货信息" width="500px">
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
          
          <Footer />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from '../components/Sidebar.vue'
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
        productId: it.id,
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
