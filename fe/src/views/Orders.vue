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
        <Sidebar :active="isAdmin ? 'all-orders' : 'orders'" />
        <el-main>
          <h2>{{ isAdmin ? '全部订单' : '我的订单' }}</h2>
          <el-table :data="orders" border style="width: 100%">
            <el-table-column label="订单号" prop="orderNo" width="180" />
            <el-table-column v-if="isAdmin" label="用户名" prop="username" width="100" />
            <el-table-column label="总额" width="100">
              <template #default="scope">
                ¥ {{ scope.row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="收货人" prop="receiverName" width="100" />
            <el-table-column label="下单时间" prop="createTime" width="170" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewDetail(scope.row)">详情</el-button>
                <el-button v-if="isAdmin" type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="total > pageSize"
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            v-model:current-page="currentPage"
            @current-change="fetchOrders"
            style="margin-top: 20px; display: flex; justify-content: center;"
          />
          
          <el-dialog v-model="detailVisible" title="订单详情" width="700px">
            <div v-if="selectedOrder">
              <p><strong>订单号:</strong> {{ selectedOrder.orderNo }}</p>
              <p><strong>下单时间:</strong> {{ selectedOrder.createTime }}</p>
              <p><strong>收货信息:</strong> {{ selectedOrder.receiverName }} / {{ selectedOrder.receiverPhone }} / {{ selectedOrder.receiverAddress }}</p>
              
              <el-table :data="selectedOrder.items" border style="margin-top: 15px;">
                <el-table-column label="商品" prop="productName" />
                <el-table-column label="规格" prop="skuSpec" width="100" />
                <el-table-column label="单价" width="100">
                  <template #default="scope">¥ {{ scope.row.price }}</template>
                </el-table-column>
                <el-table-column label="数量" prop="quantity" width="80" />
                <el-table-column label="小计" width="100">
                  <template #default="scope">¥ {{ (scope.row.price * scope.row.quantity).toFixed(2) }}</template>
                </el-table-column>
              </el-table>
              
              <div style="text-align: right; margin-top: 15px; font-size: 18px; font-weight: bold; color: #f56c6c;">
                总计: ¥ {{ selectedOrder.totalAmount }}
              </div>
            </div>
          </el-dialog>
          
          <Footer />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Sidebar from '../components/Sidebar.vue'
import Footer from '../components/Footer.vue'
import { useUserStore } from '../store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const userStore = useUserStore()
const isAdmin = computed(() => userStore.isAdmin)
const orders = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const detailVisible = ref(false)
const selectedOrder = ref(null)

const getStatusType = (status) => {
  switch (status) {
    case 2: return 'success' // 已完成
    case 0: return 'warning' // 待支付
    case 3: return 'info'    // 已取消
    case 1: return 'primary' // 制作中
    default: return ''
  }
}

const getStatusText = (status) => {
  switch (status) {
    case 0: return '待支付'
    case 1: return '制作中'
    case 2: return '已完成'
    case 3: return '已取消'
    default: return '未知'
  }
}

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

const viewDetail = async (order) => {
  try {
    const res = await axios.get(`/api/orders/${order.id}`)
    selectedOrder.value = res.data.data
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

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
