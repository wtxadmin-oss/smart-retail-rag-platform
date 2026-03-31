<template>
  <div class="orders-wrapper">
    <Navbar />
    
    <div class="page-container" :class="{ 'admin-layout': isAdmin }">
      <Sidebar v-if="isAdmin" :active="isAdmin ? 'all-orders' : 'orders'" />
      
      <div class="orders-content" :class="{ 'with-sidebar': isAdmin }">
        <h2 class="page-title">{{ isAdmin ? '全部订单' : '我的订单 / 历史订单' }}</h2>
        
        <el-card class="orders-card" shadow="never">
          <el-table :data="orders" style="width: 100%" class="custom-table">
            <el-table-column label="订单号" prop="orderNo" min-width="180" />
            <el-table-column v-if="isAdmin" label="用户名" prop="username" min-width="100" />
            <el-table-column label="总额" width="120">
              <template #default="scope">
                <span class="price-text">¥ {{ scope.row.totalAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)" effect="light" round>{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="收货人" prop="receiverName" width="120" />
            <el-table-column label="下单时间" prop="createTime" width="170" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="scope">
                <el-button type="primary" text @click="viewDetail(scope.row)">详情</el-button>
                <el-button v-if="isAdmin" type="danger" text @click="handleDelete(scope.row)">删除</el-button>
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
              @current-change="fetchOrders"
            />
          </div>
        </el-card>
        
        <el-dialog v-model="detailVisible" title="订单详情" width="700px" custom-class="coffee-dialog">
          <div v-if="selectedOrder" class="order-detail">
            <div class="detail-header">
              <div class="info-item"><span class="label">订单号:</span> {{ selectedOrder.orderNo }}</div>
              <div class="info-item"><span class="label">下单时间:</span> {{ selectedOrder.createTime }}</div>
              <div class="info-item"><span class="label">收货信息:</span> {{ selectedOrder.receiverName }} / {{ selectedOrder.receiverPhone }} / {{ selectedOrder.receiverAddress }}</div>
            </div>
            
            <el-table :data="selectedOrder.items" style="margin-top: 20px;" class="custom-table">
              <el-table-column label="商品" prop="productName" />
              <el-table-column label="规格" prop="skuSpec" width="120" />
              <el-table-column label="单价" width="100">
                <template #default="scope"><span class="price-text">¥ {{ scope.row.price }}</span></template>
              </el-table-column>
              <el-table-column label="数量" prop="quantity" width="80" align="center" />
              <el-table-column label="小计" width="120" align="right">
                <template #default="scope"><span class="subtotal-text">¥ {{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span></template>
              </el-table-column>
            </el-table>
            
            <div class="detail-footer">
              <span>总计:</span>
              <span class="total-price">¥ {{ selectedOrder.totalAmount }}</span>
            </div>
          </div>
        </el-dialog>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue'
import Sidebar from '../components/Sidebar.vue'
import Footer from '../components/Footer.vue'
import { useUserStore } from '../store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const userStore = useUserStore()
// 标记当前登录用户是否为管理员，用于切换前台与后台订单视图。
const isAdmin = computed(() => userStore.isAdmin)
const orders = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const selectedOrder = ref(null)

// 根据订单状态值返回不同的标签颜色，便于用户快速识别。
const getStatusType = (status) => {
  switch (status) {
    case 2: return 'success' // 已完成
    case 0: return 'warning' // 待支付
    case 3: return 'info'    // 已取消
    case 1: return 'primary' // 制作中
    default: return ''
  }
}

// 将后端返回的数字状态转换为页面展示用的中文文案。
const getStatusText = (status) => {
  switch (status) {
    case 0: return '待支付'
    case 1: return '制作中'
    case 2: return '已完成'
    case 3: return '已取消'
    default: return '未知'
  }
}

// 根据当前登录角色拉取订单列表：管理员看全部，普通用户看自己的订单。
const fetchOrders = async () => {
  try {
    const url = isAdmin.value ? '/api/admin/orders' : '/api/user/orders'
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    if (!isAdmin.value) {
      params.userId = userStore.userInfo?.id
    }
    const res = await axios.get(url, { params })
    const pageData = res.data.data
    orders.value = pageData.list
    total.value = pageData.total
  } catch (e) {
    ElMessage.error('获取订单失败，请检查后端服务')
    console.error('Fetch orders failed', e)
  }
}

// 点击订单详情按钮后，查询单个订单的完整信息并打开弹窗。
const viewDetail = async (order) => {
  try {
    const res = await axios.get(`/api/orders/${order.id}`)
    selectedOrder.value = res.data.data
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

// 删除订单前先弹出确认框，避免误操作。
const handleDelete = (order) => {
  ElMessageBox.confirm('确定删除该订单吗？', '提示', { type: 'warning' }).then(() => {
    ElMessage.success('订单已删除！')
    orders.value = orders.value.filter(o => o.orderNo !== order.orderNo)
  })
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color-page);
}

.page-container {
  flex: 1;
  display: flex;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 100px 20px 40px;
  gap: 24px;
  box-sizing: border-box;
  align-items: flex-start;
}

.admin-layout {
  max-width: 1400px;
  padding: 100px 20px 40px;
}

.orders-content {
  flex: 1;
  min-width: 0;
}

.orders-content:not(.with-sidebar) {
  max-width: 1000px;
  margin: 0 auto;
}

.page-title {
  font-size: 28px;
  color: var(--el-text-color-primary);
  margin-bottom: 24px;
  font-weight: 600;
}

.orders-card {
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
  color: var(--el-text-color-regular);
  font-size: 15px;
}

.subtotal-text {
  color: var(--el-color-primary);
  font-weight: 600;
  font-size: 15px;
}

.pagination-wrapper {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
}

.order-detail {
  padding: 0 10px;
}

.detail-header {
  background: var(--el-bg-color-page);
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  font-size: 15px;
  color: var(--el-text-color-primary);
}

.info-item .label {
  color: var(--el-text-color-secondary);
  width: 80px;
  display: inline-block;
}

.detail-footer {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color-light);
  display: flex;
  justify-content: flex-end;
  align-items: baseline;
  gap: 16px;
}

.total-price {
  font-size: 28px;
  font-weight: bold;
  color: var(--el-color-primary);
}

:deep(.coffee-dialog) {
  border-radius: 12px;
}

@media (max-width: 992px) {
  .page-container,
  .admin-layout {
    flex-direction: column;
  }
}
</style>
