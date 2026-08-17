# Smart Retail RAG Platform

面向零售门店场景的全栈智能客服与业务管理平台，包含商品、门店、购物车、订单、后台管理和基于 Redis 的 RAG 问答链路。

项目开发时间：**2026.03–2026.04**

## 项目定位

该项目用于验证 AI 能力与传统业务系统的集成方式：AI 客服负责知识问答，Spring Boot 服务负责确定性业务逻辑，Vue 前端统一承载顾客端与管理端交互。

~~~text
Vue 3 / Element Plus
          │
          ▼
Spring Boot REST API
    ├── 用户与鉴权
    ├── 商品 / SKU / 门店
    ├── 购物车 / 订单
    └── 智能客服
              │
              ├── Redis RAG
              └── 智谱模型 API
~~~

## 核心能力

### RAG 智能客服

- 使用 Redis 保存和检索业务知识。
- 接入智谱模型 API 生成面向顾客的回答。
- 将知识检索与模型生成从订单、商品等确定性业务接口中解耦。

### 门店与商品治理

- 支持门店维度的商品可售状态。
- 商品、SKU 与门店关系独立建模。
- 购物车校验门店是否存在以及商品是否可售。

### 订单流程

- 支持顾客下单、订单详情与状态展示。
- 管理端可更新订单状态和处理订单数据。
- 登录失效统一通过 HTTP 401 拦截并清理本地状态。

### 前后端分离

- 后端：Java、Spring Boot、MyBatis、MySQL、Redis。
- 前端：Vue 3、Vite、Pinia、Element Plus。
- 数据库初始化脚本位于 smart_coffee.sql。

## 目录结构

~~~text
SmartCoffee_Demo/
├── bg/                     Spring Boot 后端
├── fe/                     Vue 3 前端
├── smart_coffee.sql        数据库初始化脚本
└── README.md
~~~

## 工程实践

- 服务端校验用户、门店、SKU 与商品可售关系，避免信任前端参数。
- 购物车与登录状态分离管理，退出登录时清理内存状态。
- API 按顾客端与管理端职责拆分。
- RAG 只处理知识问答，不直接执行高风险业务写操作。

## 技术栈

Java · Spring Boot · MyBatis · MySQL · Redis · RAG · Vue 3 · Vite · Pinia

## 说明

这是独立的个人工程项目，用于展示 AI 应用与零售业务系统的集成能力，不对应任何企业内部系统。

