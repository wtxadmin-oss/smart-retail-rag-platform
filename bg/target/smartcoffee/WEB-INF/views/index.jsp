<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>SmartCoffee</title>
    <link rel="stylesheet" href="https://unpkg.com/element-plus/dist/index.css" />
    <style>
      /* 走马灯项基础背景展示 */
      .el-carousel__item {
        background-size: cover;
        background-position: center;
        background-repeat: no-repeat;
      }
      /* 文本居中（与高度 400px 对齐） */
      .el-carousel__item h3 {
            position: absolute;
    top: 12px;
    right: 16px;
    font-size: 15px;
    line-height: 1.2;
    margin: 0;
    color: #475669;
    opacity: 0.9;
    text-align: right;
      }
      /* 为第 1-6 项分别设置背景图（假设为 .jpg） */
      .el-carousel__item:nth-child(1) {
        background-image: url('<%= request.getContextPath() %>/static/picture/background1.jpg');
      }
      .el-carousel__item:nth-child(2) {
        background-image: url('<%= request.getContextPath() %>/static/picture/background2.jpg');
      }
      .el-carousel__item:nth-child(3) {
        background-image: url('<%= request.getContextPath() %>/static/picture/background3.jpg');
      }
      .el-carousel__item:nth-child(4) {
        background-image: url('<%= request.getContextPath() %>/static/picture/background4.jpg');
      }
      .el-carousel__item:nth-child(5) {
        background-image: url('<%= request.getContextPath() %>/static/picture/background5.jpg');
      }
      .el-carousel__item:nth-child(6) {
        background-image: url('<%= request.getContextPath() %>/static/picture/background6.jpg');
      }
      .el-row {
  margin-bottom: 20px;
}
.el-row:last-child {
  margin-bottom: 0;
}
.el-col {
  border-radius: 4px;
}

.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
    </style>
</head>
<body>
<div id="app"></div>

<script src="https://unpkg.com/vue@3/dist/vue.global.js"></script>
<script src="https://unpkg.com/element-plus"></script>
<script>
  const { createApp, ref } = Vue
  const basePath = '<%= request.getContextPath() %>'

  const App = {
    setup() {
      const active = ref("1")
      const goHealth = () => {
        window.location.href = basePath + "/api/health"
      }
      return { active, basePath, goHealth }
    },
    template: `
      <div class="common-layout">
        <el-container style="height: 100vh;">
          <el-header style="display:flex; align-items:center; gap:12px; border-bottom: 1px solid #dcdfe6;">
            <span id="logo" style="cursor:pointer;" @click="goHealth">
              <img :src="basePath + '/static/picture/logo.jpg'" alt="logo" style="height:40px;">
            </span>
            <h2 style="margin:0;">SmartCoffee</h2>
            <span style="opacity:.7;">智能咖啡系统</span>
          </el-header>
          <el-container style="overflow: hidden;">
            <el-aside width="220px" style="border-right: 1px solid #dcdfe6;">
              <el-menu v-model="active" style="border-right: none;">
                <el-menu-item index="1">首页</el-menu-item>
                <el-menu-item index="2">订单管理</el-menu-item>
                <el-menu-item index="3">用户管理</el-menu-item>
              </el-menu>
            </el-aside>
            <el-main>
              <p>欢迎使用 SmartCoffee。</p>
              <el-carousel :interval="4000" type="card" height="400px">
                <el-carousel-item v-for="item in 6" :key="item">
                  <h3 text="2xl" justify="center">广告</h3>
                </el-carousel-item>
              </el-carousel>
              <el-row :gutter="20">
                  <el-col :span="10"><div class="grid-content ep-bg-purple" />
                  <img :src="basePath + '/static/picture/xuanchuan1.jpg'" alt="xuanchuan1" style="height:200px; width:100%; object-fit:cover;">
                    </el-col>
                    <el-col :span="6"><div class="grid-content ep-bg-purple" />
                  <img :src="basePath + '/static/picture/xuanchuan2.jpg'" alt="xuanchuan2" style="height:200px; width:100%; object-fit:cover;">
                    </el-col>
                  <el-col :span="8"><div class="grid-content ep-bg-purple" />
                    <img :src="basePath + '/static/picture/xuanchuan3.jpg'" alt="xuanchuan3" style="height:200px; width:100%; object-fit:cover;">
                    </el-col>
              </el-row>
              <h1 style="text-align:center;">咖啡豆工艺</h1>
              <el-row :gutter="20">
                <el-col :span="8"><div class="grid-content ep-bg-purple" /><img :src="basePath+'/static/picture/xuanchuan4.jpg'" alt="xuanchuan4" style="height:100%; width:100%; object-fit:cover;"></el-col>
                <el-col :span="8"><div class="grid-content ep-bg-purple" /><img :src="basePath+'/static/picture/xuanchuan5.jpg'" alt="xuanchuan5" style="height:100%; width:100%; object-fit:cover;"></el-col>
                <el-col :span="8"><div class="grid-content ep-bg-purple" /><img :src="basePath+'/static/picture/xuanchuan6.jpg'" alt="xuanchuan6" style="height:100%; width:100%; object-fit:cover;"></el-col>
             </el-row>
              <el-row :gutter="20">
                <el-col :span="8"><div class="grid-content ep-bg-purple" /><img :src="basePath+'/static/picture/xuanchuan7.jpg'" alt="xuanchuan7" style="height:100%; width:100%; object-fit:cover;"></el-col>
                <el-col :span="8"><div class="grid-content ep-bg-purple" /><img :src="basePath+'/static/picture/xuanchuan8.jpg'" alt="xuanchuan8" style="height:100%; width:100%; object-fit:cover;"></el-col>
                <el-col :span="8"><div class="grid-content ep-bg-purple" /><img :src="basePath+'/static/picture/xuanchuan9.jpg'" alt="xuanchuan9" style="height:100%; width:100%; object-fit:cover;"></el-col>
              </el-row>
               <el-row :gutter="20">
                <el-col :span="12"><div class="grid-content ep-bg-purple" /><img :src="basePath+'/static/picture/xuanchuan7.jpg'" alt="xuanchuan10" style="height:100%; width:100%; object-fit:cover;"></el-col>
                <el-col :span="12"><div class="grid-content ep-bg-purple" /><img :src="basePath+'/static/picture/xuanchuan8.jpg'" alt="xuanchuan11" style="height:100%; width:100%; object-fit:cover;"></el-col>
              </el-row>
              <div style="margin-top: 24px; padding: 16px 0; border-top: 1px solid #ebeef5; text-align: center; color: #606266; font-size: 12px; line-height: 1.8;">
                <div style="font-weight: 600; letter-spacing: 1px; color: #303133;">STARBUCKS·星巴克企业管理（中国）有限公司</div>
                <div>
                  <a href="https://beian.mps.gov.cn/#/query/webSearch" target="_blank" rel="noopener noreferrer" style="display: inline-flex; align-items: center; color: inherit; text-decoration: none;">
                    <svg width="16" height="16" viewBox="0 0 64 64" aria-hidden="true" focusable="false" style="margin-right: 6px;">
                      <path d="M32 4c10 8 20 10 28 12v18c0 16-10 24-28 28C14 58 4 50 4 34V16C12 14 22 12 32 4z" fill="#d32f2f"></path>
                      <path d="M32 14c6 5 13 6 18 7v13c0 10-6 15-18 18-12-3-18-8-18-18V21c5-1 12-2 18-7z" fill="#ffffff" opacity="0.85"></path>
                      <path d="M32 22l4 8 9 1-7 6 2 9-8-5-8 5 2-9-7-6 9-1 4-8z" fill="#d32f2f"></path>
                    </svg>
                    <span>沪公网安备 31010402000253号</span>
                  </a>
                  <span style="margin: 0 8px;">|</span>
                  <span>沪ICP备17003747号-1</span>
                </div>
              </div>
            </el-main>
          </el-container>
        </el-container>
      </div>
    `
  }

  const app = createApp(App)
  app.use(ElementPlus)
  app.mount("#app")
</script>
</body>
</html>
