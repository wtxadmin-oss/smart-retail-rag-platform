/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : localhost:3306
 Source Schema         : smart_coffee

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 30/03/2026 20:41:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `sku_id` bigint NOT NULL COMMENT '规格ID',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product_sku`(`user_id` ASC, `product_id` ASC, `sku_id` ASC) USING BTREE,
  INDEX `fk_cart_product`(`product_id` ASC) USING BTREE,
  INDEX `fk_cart_sku`(`sku_id` ASC) USING BTREE,
  CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_cart_sku` FOREIGN KEY (`sku_id`) REFERENCES `product_sku` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES (1, 2, 1, 2, 1, '2026-03-09 10:34:21');
INSERT INTO `cart_item` VALUES (2, 2, 7, 19, 1, '2026-03-09 10:34:21');
INSERT INTO `cart_item` VALUES (5, 5, 10, 22, 1, '2026-03-09 10:34:21');
INSERT INTO `cart_item` VALUES (6, 6, 3, 8, 1, '2026-03-09 10:34:21');
INSERT INTO `cart_item` VALUES (7, 7, 4, 11, 1, '2026-03-09 10:34:21');
INSERT INTO `cart_item` VALUES (8, 8, 8, 20, 2, '2026-03-09 10:34:21');
INSERT INTO `cart_item` VALUES (9, 9, 6, 17, 1, '2026-03-09 10:34:21');
INSERT INTO `cart_item` VALUES (10, 10, 9, 21, 3, '2026-03-09 10:34:21');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序优先级',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '经典咖啡', 1);
INSERT INTO `category` VALUES (2, '创意特调', 2);
INSERT INTO `category` VALUES (3, '精选甜点', 3);
INSERT INTO `category` VALUES (4, '轻食简餐', 4);

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称(快照)',
  `product_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品图片(快照)',
  `sku_spec` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规格名称(快照)',
  `price` decimal(10, 2) NOT NULL COMMENT '下单时的单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单明细表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 1, '美式咖啡', NULL, '中杯', 22.00, 1);
INSERT INTO `order_item` VALUES (2, 1, 7, '提拉米苏', NULL, '标准', 35.00, 1);
INSERT INTO `order_item` VALUES (3, 2, 2, '拿铁', NULL, '中杯', 25.00, 1);
INSERT INTO `order_item` VALUES (4, 3, 2, '拿铁', NULL, '大杯', 28.00, 2);
INSERT INTO `order_item` VALUES (5, 4, 5, '生椰拿铁', NULL, '大杯', 32.00, 1);
INSERT INTO `order_item` VALUES (6, 5, 10, '鸡肉凯撒沙拉', NULL, '标准', 38.00, 1);
INSERT INTO `order_item` VALUES (7, 6, 3, '卡布奇诺', NULL, '大杯', 28.00, 1);
INSERT INTO `order_item` VALUES (8, 7, 4, '焦糖玛奇朵', NULL, '大杯', 31.00, 1);
INSERT INTO `order_item` VALUES (9, 8, 8, '纽约芝士蛋糕', NULL, '标准', 32.00, 2);
INSERT INTO `order_item` VALUES (10, 9, 6, '燕麦拿铁', NULL, '大杯', 33.00, 1);
INSERT INTO `order_item` VALUES (11, 10, 9, '巧克力麦芬', NULL, '标准', 18.00, 3);
INSERT INTO `order_item` VALUES (12, 11, 6, '燕麦拿铁', '/uploads/538a8669f2234d0681acd4b8b4cf9b00.jpg', '中杯', 30.00, 1);
INSERT INTO `order_item` VALUES (13, 12, 11, '意式浓缩', '/uploads/7e6a229b1208411ea25b048ce1a6114a.jpg', '一shot', 15.00, 1);
INSERT INTO `order_item` VALUES (14, 12, 10, '鸡肉凯撒沙拉', '/uploads/73019c4f48f04b9aaf3aeb742a2add96.jpg', '标准', 38.00, 1);
INSERT INTO `order_item` VALUES (15, 13, 11, '意式浓缩', '/uploads/7e6a229b1208411ea25b048ce1a6114a.jpg', '一shot', 15.00, 2);
INSERT INTO `order_item` VALUES (16, 13, 10, '鸡肉凯撒沙拉', '/uploads/73019c4f48f04b9aaf3aeb742a2add96.jpg', '标准', 38.00, 2);
INSERT INTO `order_item` VALUES (17, 14, 11, '意式浓缩', '/uploads/7e6a229b1208411ea25b048ce1a6114a.jpg', '一shot', 15.00, 1);
INSERT INTO `order_item` VALUES (18, 14, 10, '鸡肉凯撒沙拉', '/uploads/73019c4f48f04b9aaf3aeb742a2add96.jpg', '标准', 38.00, 1);
INSERT INTO `order_item` VALUES (19, 15, 11, '意式浓缩', '/uploads/7e6a229b1208411ea25b048ce1a6114a.jpg', '一shot', 15.00, 1);
INSERT INTO `order_item` VALUES (20, 15, 10, '鸡肉凯撒沙拉', '/uploads/73019c4f48f04b9aaf3aeb742a2add96.jpg', '标准', 38.00, 1);
INSERT INTO `order_item` VALUES (21, 16, 11, '意式浓缩', '/uploads/7e6a229b1208411ea25b048ce1a6114a.jpg', '一shot', 15.00, 1);
INSERT INTO `order_item` VALUES (22, 16, 10, '鸡肉凯撒沙拉', '/uploads/73019c4f48f04b9aaf3aeb742a2add96.jpg', '标准', 38.00, 1);
INSERT INTO `order_item` VALUES (23, 17, 11, '意式浓缩', '/uploads/7e6a229b1208411ea25b048ce1a6114a.jpg', '一shot', 15.00, 2);
INSERT INTO `order_item` VALUES (24, 18, 9, '巧克力麦芬', '/uploads/b4e387b693a244ddb6178500ec00542e.jpg', '标准', 20.00, 1);
INSERT INTO `order_item` VALUES (25, 18, 8, '纽约芝士蛋糕', '/uploads/9a9a5b28156c4d249887e4985ee1b3ec.jpg', '标准', 32.00, 1);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint NOT NULL COMMENT '下单用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` int NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-制作中, 2-已完成, 3-已取消',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人(快照)',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '电话(快照)',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址(快照)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD202310010001', 2, 57.00, 2, '张三', '13900000001', '上海市浦东新区陆家嘴环路100号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (2, 'ORD202310010002', 2, 25.00, 2, '张三', '13900000001', '上海市浦东新区陆家嘴环路100号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (3, 'ORD202310010003', 3, 56.00, 1, '李四', '13900000002', '广州市天河区珠江新城华夏路10号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (4, 'ORD202310010004', 4, 32.00, 0, '王五', '13900000003', '深圳市南山区科技园南区W1-B', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (5, 'ORD202310010005', 5, 38.00, 2, '赵六', '13900000004', '成都市高新区天府大道北段1700号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (6, 'ORD202310010006', 6, 28.00, 2, '钱七', '13900000005', '杭州市西湖区文三路90号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (7, 'ORD202310010007', 7, 31.00, 1, '孙八', '13900000006', '南京市建邺区江东中路222号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (8, 'ORD202310010008', 8, 64.00, 2, '周九', '13900000007', '武汉市洪山区珞喻路1037号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (9, 'ORD202310010009', 9, 33.00, 0, '吴十', '13900000008', '西安市雁塔区长安南路88号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (10, 'ORD202310010010', 10, 54.00, 2, '郑十一', '13900000009', '重庆市渝中区解放碑步行街1号', '2026-03-09 10:34:21', NULL);
INSERT INTO `orders` VALUES (11, 'SC17733952839594DB8C7', 11, 30.00, 0, 'wtx', '18655542973', '安徽省马鞍山市', '2026-03-13 17:48:03', NULL);
INSERT INTO `orders` VALUES (12, 'SC17736674520439F4EF6', 4, 53.00, 0, 'wxx', '1', '1', '2026-03-16 21:24:12', NULL);
INSERT INTO `orders` VALUES (13, 'SC177366767295560F85F', 3, 106.00, 0, '2', '2', '2', '2026-03-16 21:27:52', NULL);
INSERT INTO `orders` VALUES (14, 'SC1773667903514341F11', 4, 53.00, 0, '1', '1', '1', '2026-03-16 21:31:43', NULL);
INSERT INTO `orders` VALUES (15, 'SC1773667916287329CDA', 4, 53.00, 0, '1', '1', '1', '2026-03-16 21:31:56', NULL);
INSERT INTO `orders` VALUES (16, 'SC17736680770194B1070', 4, 53.00, 0, '3', '3', '3', '2026-03-16 21:34:37', NULL);
INSERT INTO `orders` VALUES (17, 'SC1773835060172A63ED4', 4, 30.00, 0, '2', '22', '2', '2026-03-18 19:57:40', NULL);
INSERT INTO `orders` VALUES (18, 'SC177487441267881FF06', 3, 52.00, 0, '1', '1', '1', '2026-03-30 20:40:12', NULL);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `category_id` int NULL DEFAULT NULL COMMENT '分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '产品描述',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片路径',
  `is_active` tinyint NULL DEFAULT 1 COMMENT '是否上架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 1, '美式咖啡', '精选阿拉比卡豆，口感纯正', '/uploads/b2a099926db94f46a671d247aa53e794.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (2, 1, '拿铁', '浓缩咖啡与鲜奶的完美融合', '/uploads/45a3b027e4074ee2823d8c119e31bdb4.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (3, 1, '卡布奇诺', '绵密奶泡，层次丰富', '/uploads/9982532e71984595b3a3ac26612fc67f.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (4, 1, '焦糖玛奇朵', '甜蜜焦糖与香浓咖啡', '/uploads/cd7cb967416e49cb934681568503399a.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (5, 2, '生椰拿铁', '冷榨生椰浆，清爽椰香', '/uploads/a2476feccf2b4b29b69b1a944e851506.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (6, 2, '燕麦拿铁', '植物基燕麦奶，健康之选', '/uploads/dee55dea36a9451f929730edb0c31098.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (7, 3, '提拉米苏', '经典意式甜点，入口即化', '/uploads/c50898ed3fd44cf9ad50234110bcec0c.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (8, 3, '纽约芝士蛋糕', '浓郁芝士，口感顺滑', '/uploads/9a9a5b28156c4d249887e4985ee1b3ec.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (9, 3, '巧克力麦芬', '香浓巧克力，松软可口', '/uploads/b4e387b693a244ddb6178500ec00542e.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (10, 4, '鸡肉凯撒沙拉', '新鲜蔬菜搭配嫩滑鸡胸肉', '/uploads/66aa3924e517498fbe6deb3713f192b4.jpg', 1, '2026-03-09 10:34:21');
INSERT INTO `product` VALUES (11, 1, '意式浓缩', '口感醇厚绵密，油脂金黄浓郁，苦香平衡、回甘悠长，一口便满是纯粹的咖啡本味。只可热饮。', '/uploads/a57eb4e9978c4723bb69c7c377fea998.jpg', 1, '2026-03-13 16:31:08');

-- ----------------------------
-- Table structure for product_sku
-- ----------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `product_id` bigint NOT NULL COMMENT '关联产品ID',
  `spec_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规格名称',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `stock` int NULL DEFAULT 9999 COMMENT '库存',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product`(`product_id` ASC) USING BTREE,
  CONSTRAINT `fk_sku_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '产品规格价格表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_sku
-- ----------------------------
INSERT INTO `product_sku` VALUES (1, 1, '中杯', 22.00, 100);
INSERT INTO `product_sku` VALUES (2, 1, '大杯', 25.00, 100);
INSERT INTO `product_sku` VALUES (3, 1, '超大杯', 28.00, 100);
INSERT INTO `product_sku` VALUES (4, 2, '中杯', 25.00, 100);
INSERT INTO `product_sku` VALUES (5, 2, '大杯', 28.00, 100);
INSERT INTO `product_sku` VALUES (6, 2, '超大杯', 31.00, 100);
INSERT INTO `product_sku` VALUES (7, 3, '中杯', 25.00, 100);
INSERT INTO `product_sku` VALUES (8, 3, '大杯', 28.00, 100);
INSERT INTO `product_sku` VALUES (9, 3, '超大杯', 31.00, 100);
INSERT INTO `product_sku` VALUES (10, 4, '中杯', 28.00, 100);
INSERT INTO `product_sku` VALUES (11, 4, '大杯', 31.00, 100);
INSERT INTO `product_sku` VALUES (12, 4, '超大杯', 34.00, 100);
INSERT INTO `product_sku` VALUES (13, 5, '中杯', 29.00, 100);
INSERT INTO `product_sku` VALUES (14, 5, '大杯', 32.00, 100);
INSERT INTO `product_sku` VALUES (15, 5, '超大杯', 35.00, 100);
INSERT INTO `product_sku` VALUES (16, 6, '中杯', 30.00, 100);
INSERT INTO `product_sku` VALUES (17, 6, '大杯', 33.00, 100);
INSERT INTO `product_sku` VALUES (18, 6, '超大杯', 36.00, 100);
INSERT INTO `product_sku` VALUES (19, 7, '标准', 35.00, 50);
INSERT INTO `product_sku` VALUES (20, 8, '标准', 32.00, 50);
INSERT INTO `product_sku` VALUES (21, 9, '标准', 20.00, 50);
INSERT INTO `product_sku` VALUES (22, 10, '标准', 38.00, 30);
INSERT INTO `product_sku` VALUES (24, 5, '小杯', 27.00, 3);
INSERT INTO `product_sku` VALUES (25, 11, '一shot', 15.00, 10);

-- ----------------------------
-- Table structure for store
-- ----------------------------
DROP TABLE IF EXISTS `store`;
CREATE TABLE `store`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `lng` decimal(10, 6) NULL DEFAULT NULL,
  `lat` decimal(10, 6) NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of store
-- ----------------------------
INSERT INTO `store` VALUES (1, '门店-上海市浦东新区陆家嘴', '上海市浦东新区陆家嘴环路100号', NULL, 121.505230, 31.236012, 1);
INSERT INTO `store` VALUES (2, '门店-广州市天河区珠江新城', '广州市天河区珠江新城华夏路10号', NULL, 113.321804, 23.117323, 1);
INSERT INTO `store` VALUES (3, '门店-深圳市南山区科技园南', '深圳市南山区科技园南区W1-B', NULL, 113.943685, 22.536845, 1);
INSERT INTO `store` VALUES (4, '门店-成都市高新区天府大道', '成都市高新区天府大道北段1700号', NULL, 104.063780, 30.566541, 1);
INSERT INTO `store` VALUES (5, '门店-杭州市西湖区文三路9', '杭州市西湖区文三路90号', NULL, 120.146060, 30.279242, 1);
INSERT INTO `store` VALUES (6, '门店-南京市建邺区江东中路', '南京市建邺区江东中路222号', NULL, 118.729849, 32.015270, 0);
INSERT INTO `store` VALUES (7, '门店-武汉市洪山区珞喻路1', '武汉市洪山区珞喻路1037号', NULL, 114.402810, 30.511518, 1);
INSERT INTO `store` VALUES (8, '门店-西安市雁塔区长安南路', '西安市雁塔区长安南路88号', NULL, 108.946634, 34.196006, 0);
INSERT INTO `store` VALUES (9, '门店-重庆市渝中区解放碑步', '重庆市渝中区解放碑步行街1号', NULL, 106.577034, 29.557204, 1);

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `user_id` bigint NOT NULL COMMENT '用户ID(主键)',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户收货地址表(1对1)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (1, '管理员', '13800138000', '北京市海淀区中关村科技园1号楼', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (2, '张三', '13900000001', '上海市浦东新区陆家嘴环路100号', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (3, '李四', '13900000002', '广州市天河区珠江新城华夏路10号', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (4, '王五', '13900000003', '深圳市南山区科技园南区W1-B', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (5, '赵六', '13900000004', '成都市高新区天府大道北段1700号', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (6, '钱七', '13900000005', '杭州市西湖区文三路90号', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (7, '孙八', '13900000006', '南京市建邺区江东中路222号', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (8, '周九', '13900000007', '武汉市洪山区珞喻路1037号', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (9, '吴十', '13900000008', '西安市雁塔区长安南路88号', '2026-03-09 10:34:21');
INSERT INTO `user_address` VALUES (10, '郑十一', '13900000009', '重庆市渝中区解放碑步行街1号', '2026-03-09 10:34:21');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名(唯一)',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '加密密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'CUSTOMER' COMMENT '角色: CUSTOMER/ADMIN',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', '123456', 'ADMIN', '13800138000', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (2, 'user01', '123456', 'ADMIN', '13900000001', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (3, 'user02', '123456', 'CUSTOMER', '13900000002', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (4, 'user03', '123456', 'CUSTOMER', '13900000003', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (5, 'user04', '123456', 'CUSTOMER', '13900000004', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (6, 'user05', '123456', 'CUSTOMER', '13900000005', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (7, 'user06', '123456', 'CUSTOMER', '13900000006', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (8, 'user07', '123456', 'CUSTOMER', '13900000007', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (9, 'user08', '123456', 'CUSTOMER', '13900000008', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (10, 'user09', '123456', 'CUSTOMER', '13900000009', 1, '2026-03-09 10:34:21');
INSERT INTO `users` VALUES (11, 'wtx', '123456', 'CUSTOMER', NULL, 1, '2026-03-13 16:36:25');

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Store-aware migration (2026-04-09)
-- ----------------------------
ALTER TABLE `store`
  ADD COLUMN IF NOT EXISTS `business_hours` varchar(100) NULL DEFAULT NULL AFTER `lat`;

ALTER TABLE `cart_item`
  ADD COLUMN IF NOT EXISTS `store_id` bigint NULL DEFAULT NULL AFTER `user_id`;

ALTER TABLE `orders`
  ADD COLUMN IF NOT EXISTS `store_id` bigint NULL DEFAULT NULL AFTER `user_id`;

DROP TABLE IF EXISTS `store_product`;
CREATE TABLE `store_product` (
  `store_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `is_available` tinyint NULL DEFAULT 1,
  PRIMARY KEY (`store_id`, `product_id`) USING BTREE,
  CONSTRAINT `fk_store_product_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_store_product_store` FOREIGN KEY (`store_id`) REFERENCES `store` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

INSERT INTO `store_product` (`store_id`, `product_id`, `is_available`)
SELECT s.`id`, p.`id`, CASE WHEN s.`status` = 1 THEN p.`is_active` ELSE 0 END
FROM `store` s
CROSS JOIN `product` p;
