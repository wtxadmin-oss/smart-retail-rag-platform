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
            <el-table-column v-if="isAdmin" label="用户" prop="username" min-width="100" />
            <el-table-column label="总额" width="120"><template #default="scope"><span class="price-text">¥ {{ scope.row.totalAmount }}</span></template></el-table-column>
            <el-table-column label="状态" width="100"><template #default="scope"><el-tag :type="getStatusType(scope.row.status)" effect="light" round>{{ getStatusText(scope.row.status) }}</el-tag></template></el-table-column>
            <el-table-column label="收货人" prop="receiverName" width="120" />
            <el-table-column label="下单时间" prop="createTime" width="170" />
            <el-table-column label="门店" prop="storeName" min-width="180" />
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button type="primary" text @click="viewDetail(scope.row)">详情</el-button>
                <el-button v-if="isAdmin" type="warning" text @click="openStatusDialog(scope.row)">订单状态管理</el-button>
                <el-button v-if="isAdmin" type="danger" text @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrapper" v-if="total > pageSize">
            <el-pagination v-model:current-page="currentPage" background layout="prev, pager, next" :total="total" :page-size="pageSize" @current-change="fetchOrders" />
          </div>
        </el-card>
        <el-dialog v-model="statusDialogVisible" title="订单状态管理" width="360px" custom-class="coffee-dialog">
          <el-form label-width="80px">
            <el-form-item label="当前状态"><el-tag :type="getStatusType(editingOrder?.status)" effect="light" round>{{ getStatusText(editingOrder?.status) }}</el-tag></el-form-item>
            <el-form-item label="新状态"><el-select v-model="newStatus" placeholder="请选择"><el-option label="待支付" :value="0" /><el-option label="制作中" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已取消" :value="3" /></el-select></el-form-item>
          </el-form>
          <template #footer><el-button @click="statusDialogVisible = false">取消</el-button><el-button type="primary" @click="submitStatusChange">确认修改</el-button></template>
        </el-dialog>
        <el-dialog v-model="detailVisible" title="订单详情" width="700px" custom-class="coffee-dialog">
          <div v-if="selectedOrder" class="order-detail">
            <div class="detail-header">
              <div class="info-item"><span class="label">订单号:</span> {{ selectedOrder.orderNo }}</div>
              <div class="info-item"><span class="label">下单时间:</span> {{ selectedOrder.createTime }}</div>
              <div class="info-item"><span class="label">门店:</span> {{ selectedOrder.storeName || '未关联门店' }}</div>
              <div class="info-item"><span class="label">收货信息:</span> {{ selectedOrder.receiverName }} / {{ selectedOrder.receiverPhone }} / {{ selectedOrder.receiverAddress }}</div>
            </div>
            <el-table :data="selectedOrder.items" style="margin-top: 20px" class="custom-table">
              <el-table-column label="商品" prop="productName" />
              <el-table-column label="规格" prop="skuSpec" width="120" />
              <el-table-column label="单价" width="100"><template #default="scope"><span class="price-text">¥ {{ scope.row.price }}</span></template></el-table-column>
              <el-table-column label="数量" prop="quantity" width="80" align="center" />
              <el-table-column label="小计" width="120" align="right"><template #default="scope"><span class="subtotal-text">¥ {{ (scope.row.price * scope.row.quantity).toFixed(2) }}</span></template></el-table-column>
            </el-table>
            <div class="detail-footer"><span>总计:</span><span class="total-price">¥ {{ selectedOrder.totalAmount }}</span></div>
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
const isAdmin = computed(() => userStore.isAdmin)
const orders = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const selectedOrder = ref(null)
const statusDialogVisible = ref(false)
const editingOrder = ref(null)
const newStatus = ref(null)

const getStatusType = (status) => ({ 0: 'warning', 1: 'primary', 2: 'success', 3: 'info' }[status] || '')
const getStatusText = (status) => ({ 0: '待支付', 1: '制作中', 2: '已完成', 3: '已取消' }[status] || '未知')

const fetchOrders = async () => {
  try {
    const url = isAdmin.value ? '/api/admin/orders' : '/api/user/orders'
    const params = { pageNum: currentPage.value, pageSize: pageSize.value }
    if (!isAdmin.value) params.userId = userStore.userInfo?.id
    const res = await axios.get(url, { params })
    const pageData = res.data?.data || {}
    orders.value = pageData.list || []
    total.value = pageData.total || 0
  } catch (error) {
    ElMessage.error('获取订单失败，请检查后端服务')
    console.error('Fetch orders failed', error)
  }
}

const viewDetail = async (order) => {
  try {
    const res = await axios.get(`/api/orders/${order.id}`)
    selectedOrder.value = res.data?.data
    detailVisible.value = true
  } catch {
    ElMessage.error('获取详情失败')
  }
}

const openStatusDialog = (order) => {
  editingOrder.value = order
  newStatus.value = order.status
  statusDialogVisible.value = true
}

const submitStatusChange = async () => {
  try {
    await axios.put(`/api/admin/orders/${editingOrder.value.id}/status`, null, { params: { status: newStatus.value } })
    ElMessage.success('订单状态已更新')
    statusDialogVisible.value = false
    fetchOrders()
  } catch {
    ElMessage.error('状态更新失败')
  }
}

const handleDelete = (order) => {
  ElMessageBox.confirm('确定删除该订单吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await axios.delete(`/api/admin/orders/${order.id}`)
      ElMessage.success('订单已删除')
      fetchOrders()
    } catch {
      ElMessage.error('删除失败，请检查后端服务')
    }
  })
}

onMounted(() => { fetchOrders() })
</script>

<style scoped>
.orders-wrapper { min-height: 100vh; display: flex; flex-direction: column; background-color: var(--el-bg-color-page); }
.page-container { flex: 1; display: flex; max-width: 1200px; margin: 0 auto; width: 100%; padding: 100px 20px 40px; gap: 24px; box-sizing: border-box; align-items: flex-start; }
.admin-layout { max-width: 1400px; }
.orders-content { flex: 1; min-width: 0; }
.orders-content:not(.with-sidebar) { max-width: 1000px; margin: 0 auto; }
.page-title { font-size: 28px; margin-bottom: 24px; font-weight: 600; }
.price-text, .subtotal-text, .total-price { color: var(--el-color-primary); font-weight: 600; }
.detail-header, .detail-footer { display: flex; flex-direction: column; gap: 8px; }
.label { color: var(--el-text-color-secondary); }
</style>
