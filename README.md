# SmartCoffee 智能咖啡店管理系统

这是一个为技术小白准备的入门级全栈项目（前后端分离）。它模拟了一个真实的智能咖啡店的业务流程，包含了用户浏览、点单、购物车、管理员后台管理等功能。

项目分为两大部分：
1. **`bg` (Backend / 后端)**: 使用 Java + Spring Boot + MyBatis 编写，负责处理数据和业务逻辑。
2. **`fe` (Frontend / 前端)**: 使用 Vue 3 + Vite + Element Plus 编写，负责页面展示和用户交互。

---

## 📂 整体项目目录结构

```text
SmartCoffee_Demo/           # 项目根目录
├── bg/                     # 后端代码目录 (Spring Boot)
│   ├── src/main/java/      # Java 核心源代码
│   ├── src/main/resources/ # 配置文件和 SQL 映射文件
│   └── pom.xml             # Maven 依赖配置文件
├── fe/                     # 前端代码目录 (Vue 3)
│   ├── src/                # 前端核心源代码 (组件、页面等)
│   ├── public/             # 静态资源 (图片等)
│   ├── package.json        # Node.js 依赖配置文件
│   └── vite.config.ts      # Vite 构建工具配置
└── smart_coffee.sql        # 数据库初始化脚本 (用来建表和插入初始数据)
```

---

## ☕ `bg` (后端) 结构详解

后端采用经典的 **MVC（Model-View-Controller）** 三层架构，这种架构就像是一个餐厅的厨房分工，职责明确。

```text
bg/src/main/java/com/smartcoffee/
├── SmartCoffeeApplication.java    # 【核心】Spring Boot 的启动类，程序的入口 (就像餐厅的店长)
│
├── config/                        # 【配置层】存放系统配置 (如拦截器、跨域设置)
│   ├── AuthInterceptor.java       # 权限拦截器：检查用户是否登录 (就像餐厅门口的保安)
│   └── WebCorsConfig.java         # 跨域配置：允许前端(5173端口)访问后端(8080端口)
│
├── controller/                    # 【控制层】接收前端的请求，并返回结果 (就像餐厅的服务员)
│   ├── AuthController.java        # 处理登录、注册请求
│   ├── ProductController.java     # 处理商品展示请求
│   ├── CartController.java        # 处理购物车相关请求
│   └── ...                        # 其他控制器
│
├── service/                       # 【业务层】处理具体的业务逻辑 (就像餐厅的大厨)
│   ├── impl/                      # 接口的实现类 (具体做菜的步骤)
│   ├── UserService.java           # 用户相关业务
│   ├── ProductService.java        # 商品相关业务
│   └── ...
│
├── mapper/                        # 【持久层】直接与数据库打交道 (就像餐厅的采购员去仓库拿菜)
│   ├── UserMapper.java            # 操作用户表
│   ├── ProductMapper.java         # 操作商品表
│   └── ...
│
├── entity/                        # 【实体层】对应数据库里的表结构 (定义了数据的长什么样)
│   ├── User.java                  # 用户实体类 (包含用户名、密码等属性)
│   ├── Product.java               # 商品实体类
│   └── ...
│
├── utils/                         # 【工具类】存放一些通用的方法
│   └── JwtUtils.java              # JWT 工具类：用于生成和解析登录 Token (就像制作和验证会员卡)
│
└── common/                        # 【公共类】
    └── Result.java                # 统一的返回结果封装类 (保证给前端的数据格式都长得一样)
```

### 🗄️ 后端资源文件 (`src/main/resources/`)

```text
bg/src/main/resources/
├── application.yml                # 【核心配置】配置数据库连接、服务器端口(8080)等信息
└── mapper/                        # MyBatis 的 XML 映射文件 (写具体 SQL 语句的地方)
    ├── UserMapper.xml             # 对应 UserMapper.java 的 SQL 语句
    ├── ProductMapper.xml          # 对应 ProductMapper.java 的 SQL 语句
    └── ...
```

---

## 🎨 `fe` (前端) 结构详解

前端基于 Vue 3 框架，采用组件化开发，把页面拆分成一个个小块(组件)进行管理。

```text
fe/src/
├── main.js                        # 【核心】前端程序的入口，初始化 Vue 应用并挂载插件
├── App.vue                        # 【根组件】所有页面的最外层壳子
│
├── router/                        # 【路由配置】管理网址路径和页面的对应关系
│   └── index.js                   # 比如: 访问 '/login' 就展示登录页面
│
├── store/                         # 【状态管理】存放全局共享的数据 (使用 Pinia)
│   └── user.js                    # 保存用户的登录状态和信息，任何页面都能读取
│
├── components/                    # 【公共组件】可以在多个页面中重复使用的 UI 部件
│   ├── Navbar.vue                 # 顶部导航栏
│   ├── Footer.vue                 # 底部页脚
│   └── Sidebar.vue                # 管理后台的侧边栏
│
├── views/                         # 【页面视图】具体的网页
│   ├── 👤 用户端页面:
│   │   ├── index.vue              # 首页
│   │   ├── Menu.vue               # 点餐菜单页
│   │   ├── Cart.vue               # 购物车页
│   │   ├── BrandStory.vue         # 品牌故事页
│   │   ├── CoffeeWiki.vue         # 咖啡百科页
│   │   └── ...
│   │
│   └── 🛠️ 管理端页面 (管理员可见):
│       ├── AdminLayout.vue        # 管理后台的外层框架
│       ├── StoreManagement.vue    # 门店管理
│       ├── MenuManagement.vue     # 商品菜单管理
│       └── ...
│
└── utils/                         # 【前端工具类】
    └── request.js                 # 封装了 axios，统一处理发送给后端的网络请求 (比如自动带上登录 Token)
```

---

## 🚀 运行项目指南 (小白必看)

### 1. 准备环境
- 安装 **Java JDK 17** (后端需要)
- 安装 **Node.js** (前端需要)
- 安装 **MySQL 8.0+** 数据库

### 2. 初始化数据库
- 打开你的 MySQL 工具 (如 Navicat 或 DataGrip)
- 新建一个名为 `smart_coffee` 的数据库 (字符集选 utf8mb4)
- 运行项目根目录下的 `smart_coffee.sql` 文件，这会自动建表并插入初始测试数据。

### 3. 启动后端 (Spring Boot)
- 推荐使用 **IDEA** 打开 `bg` 文件夹。
- 找到 `bg/src/main/resources/application.yml`，修改里面的数据库用户名和密码为你自己的。
- 找到并运行 `SmartCoffeeApplication.java` 中的 `main` 方法。
- 成功后，后端会运行在 `http://localhost:8080`。

### 4. 启动前端 (Vue)
- 打开终端 (命令行)，进入 `fe` 目录。
- 运行 `npm install` 下载前端依赖包 (只需执行一次)。
- 运行 `npm run dev` 启动前端服务。
- 成功后，浏览器访问终端里显示的地址 (通常是 `http://localhost:5173`)。

### 💡 默认测试账号
- **管理员账号**: `admin` / 密码: `123456`
- **普通用户账号**: `user` / 密码: `123456`

---

## 🧭 前端页面方法实现过程（`fe/src/views`）

本节按页面说明“方法做了什么、调用链路是什么”，便于快速理解页面业务流程。

### Home.vue
- 当前页面以展示为主，无独立业务方法。

### BrandStory.vue
- 当前页面以展示为主，无独立业务方法。

### CoffeeWiki.vue
- 当前页面以展示为主，无独立业务方法。

### Membership.vue
- 当前页面以展示为主，无独立业务方法。

### Login.vue
- `handleLogin`
  - 读取登录表单参数，调用登录接口。
  - 登录成功后写入用户状态与 Token，并跳转到首页或目标页。
  - 登录失败时给出错误提示。

### Register.vue
- `validatePass2`
  - 校验“确认密码”与“密码”是否一致。
- `submitForm`
  - 先执行前端表单校验，再调用注册接口。
  - 注册成功后提示并跳转登录页，失败则提示原因。

### Profile.vue
- `handleUpdate`
  - 提交用户资料更新请求并回写本地展示状态。
- `handleChangePassword`
  - 校验新密码参数后调用改密接口。
  - 根据接口结果提示成功/失败并处理后续状态。

### Menu.vue
- `handleImageError`
  - 商品图加载失败时替换为兜底图片。
- `fetchCategories`
  - 拉取分类数据并刷新分类筛选区。
- `fetchProducts`
  - 按关键字、分类、分页条件拉取商品列表。
- `showDetail`
  - 拉取单个商品详情与规格信息并打开详情弹窗。
- `addToCartWithTip`
  - 执行加购逻辑并给出操作结果提示。

### Cart.vue
- `handleImageError`
  - 购物车商品图加载失败时替换默认图。
- `handleCheckout`
  - 校验登录状态与购物车非空后打开结算弹窗。
- `submitOrder`
  - 组装订单与明细数据，调用下单接口。
  - 下单成功后清空购物车并跳转订单页。

### Orders.vue
- `getStatusType`
  - 将订单状态映射为标签颜色类型。
- `getStatusText`
  - 将订单状态码映射为中文文案。
- `fetchOrders`
  - 根据当前角色切换调用用户/管理员订单列表接口。
  - 处理分页参数并回填表格数据。
- `viewDetail`
  - 查询单个订单详情并展示详情弹窗。
- `handleDelete`
  - 二次确认后删除订单（管理端场景）。

### Customer.vue
- `goHome`
  - 返回首页。
- `setInput`
  - 快速填充预设提问内容。
- `scrollToBottom`
  - 在消息更新后滚动聊天区域到底部。
- `sendMessage`
  - 发送用户消息到聊天接口并接收回复，更新会话消息列表。

### WhereCoffee.vue
- `zoomIn`
  - 放大地图视图。
- `zoomOut`
  - 缩小地图视图。
- `locateMe`
  - 尝试定位当前用户位置并更新地图中心点。
- `focusStore`
  - 聚焦指定门店并展示门店信息。
- `fetchStores`
  - 拉取门店列表并用于地图渲染。
- `enableFallback`
  - 地图异常时切换到降级展示逻辑。
- `openExternalMap`
  - 打开外部地图导航链接。
- `renderStoresOnMap`
  - 将门店数据转换为地图标记并渲染。

### MenuManagement.vue（管理端）
- `fetchCategories`
  - 拉取分类用于筛选与编辑表单。
- `fetchProducts`
  - 按条件分页拉取商品列表。
- `handleAdd`
  - 初始化新增商品表单并打开弹窗。
- `handleImageUpload`
  - 上传商品图片并回填图片地址。
- `handleEdit`
  - 回填现有商品数据进入编辑态。
- `handleDelete`
  - 二次确认后删除指定商品。
- `handleSubmit`
  - 根据新增/编辑状态调用对应接口提交商品数据。

### SpecManagement.vue（管理端）
- `fetchProducts`
  - 拉取商品列表用于规格归属选择。
- `fetchSkus`
  - 拉取规格列表并刷新表格。
- `handleAdd`
  - 初始化新增规格表单并打开弹窗。
- `handleEdit`
  - 回填规格数据进入编辑态。
- `handleDelete`
  - 二次确认后删除规格。
- `handleSubmit`
  - 根据当前表单状态提交新增或更新请求。

### StoreManagement.vue（管理端）
- `fetchStores`
  - 拉取门店分页数据并刷新列表。
- `handleInit`
  - 调用门店初始化/补全相关接口并刷新列表。
- `handleAdd`
  - 初始化新增门店表单并打开弹窗。
- `handleEdit`
  - 回填门店数据进入编辑态。
- `handleDelete`
  - 二次确认后删除门店。
- `handleSubmit`
  - 提交门店新增或更新请求。

### UserManagement.vue（管理端）
- `fetchUsers`
  - 拉取用户分页数据并刷新列表。
- `handleAdd`
  - 初始化新增用户表单并打开弹窗。
- `handleEdit`
  - 回填用户数据进入编辑态。
- `handleDelete`
  - 二次确认后删除用户。
- `handleSubmit`
  - 提交用户新增或更新请求。
