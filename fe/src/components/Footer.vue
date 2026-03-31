<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-brand">
        <h3>SmartCoffee</h3>
        <p>Premium Quality & Service</p>
      </div>
      
      <div class="footer-nav">
        <router-link to="/brand" class="footer-link">品牌故事</router-link>
        <el-divider direction="vertical" />
        <router-link to="/wiki" class="footer-link">咖啡百科</router-link>
        <el-divider direction="vertical" />
        <router-link to="/wherecoffee" class="footer-link">体验店位置</router-link>
        <el-divider direction="vertical" />
        <router-link to="/customer" class="footer-link">联系客服</router-link>
      </div>

      <div class="footer-links">
        <div style="font-weight: 600; letter-spacing: 1px; color: var(--el-text-color-primary); margin-bottom: 8px;">ZHINENG·智能企业管理（中国）有限公司</div>
        <div style="display: flex; justify-content: center; align-items: center; color: var(--el-text-color-secondary);">
          <a href="https://beian.mps.gov.cn/#/query/webSearch" target="_blank" rel="noopener noreferrer" style="display: inline-flex; align-items: center; color: inherit; text-decoration: none;">
            <svg width="14" height="14" viewBox="0 0 60 60" aria-hidden="true" focusable="false" style="margin-right: 6px;">
              <path d="M32 4c10 8 20 10 28 12v18c0 16-10 24-28 28C14 58 4 50 4 34V16C12 14 22 12 32 4z" fill="#d32f2f"></path>
              <path d="M32 14c6 5 13 6 18 7v13c0 10-6 15-18 18-12-3-18-8-18-18V21c5-1 12-2 18-7z" fill="#ffffff" opacity="0.85"></path>
              <path d="M32 22l4 8 9 1-7 6 2 9-8-5-8 5 2-9-7-6 9-1 4-8z" fill="#d32f2f"></path>
            </svg>
          </a>
          <span style="margin: 0 8px;">沪公网安备 31010402000253号 | 沪ICP备17003747号-1</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  background-color: var(--el-bg-color);
  padding: 40px 20px;
  border-top: 1px solid var(--el-border-color);
  margin-top: 60px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  text-align: center;
}

.footer-brand h3 {
  margin: 0 0 4px 0;
  color: var(--el-color-primary);
  font-family: serif;
  font-size: 24px;
}

.footer-brand p {
  margin: 0;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 2px;
}

.footer-nav {
  display: flex;
  align-items: center;
  gap: 16px;
  margin: 10px 0;
}

.footer-link {
  color: var(--el-text-color-regular);
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.footer-link:hover {
  color: var(--el-color-primary);
}

.footer-links {
  font-size: 12px;
}
</style>
