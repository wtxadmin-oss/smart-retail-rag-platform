<template>
  <el-aside width="240px" class="admin-sidebar">
    <div class="sidebar-header">
      <el-icon :size="24" color="var(--el-color-primary)"><Setting /></el-icon>
      <h3>管理后台</h3>
    </div>
    <el-menu 
      :default-active="active" 
      class="admin-menu"
      active-text-color="var(--el-color-primary)"
    >
      <el-menu-item index="all-orders" @click="router.push('/admin/orders')">
        <el-icon><List /></el-icon>
        <span>全部订单</span>
      </el-menu-item>
      <el-menu-item index="menu-mgmt" @click="router.push('/admin/menu')">
        <el-icon><Coffee /></el-icon>
        <span>菜单管理</span>
      </el-menu-item>
      <el-menu-item index="spec-mgmt" @click="router.push('/admin/specs')">
        <el-icon><Setting /></el-icon>
        <span>规格管理</span>
      </el-menu-item>
      <el-menu-item index="store-mgmt" @click="router.push('/admin/stores')">
        <el-icon><Shop /></el-icon>
        <span>门店管理</span>
      </el-menu-item>
      <el-menu-item index="user-mgmt" @click="router.push('/admin/users')">
        <el-icon><User /></el-icon>
        <span>用户管理</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Setting, List, Coffee, Shop, User } from '@element-plus/icons-vue'

const props = defineProps({
  active: String
})

const router = useRouter()
</script>

<style scoped>
.admin-sidebar {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  border: 1px solid var(--el-border-color-light);
  overflow: hidden;
  height: calc(100vh - 140px);
  position: sticky;
  top: 100px;
}

.sidebar-header {
  padding: 24px 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid var(--el-border-color-light);
  background-color: var(--el-color-primary-light-9);
}

.sidebar-header h3 {
  margin: 0;
  font-size: 18px;
  color: var(--el-text-color-primary);
  font-weight: 600;
}

.admin-menu {
  border-right: none;
  padding: 12px 0;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  border-radius: 8px;
  color: var(--el-text-color-regular);
}

:deep(.el-menu-item.is-active) {
  background-color: var(--el-color-primary-light-9);
  font-weight: 600;
}

:deep(.el-menu-item:hover) {
  background-color: var(--el-color-primary-light-9);
}

@media (max-width: 992px) {
  .admin-sidebar {
    width: 100% !important;
    height: auto;
    position: static;
  }

  .sidebar-header {
    padding: 18px 20px;
  }

  .admin-menu {
    display: flex;
    flex-wrap: wrap;
    padding: 12px;
    gap: 8px;
  }

  :deep(.el-menu-item) {
    margin: 0;
    flex: 1 1 140px;
  }
}
</style>
