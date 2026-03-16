/*
 * SmartCoffee 数据库完整脚本 (Schema + Data)
 * 包含表结构定义及测试数据
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================
-- 1. 表结构定义 (Schema)
-- ==========================================

-- 1. 用户表 (users)
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名(唯一)',
  `password` VARCHAR(255) NOT NULL COMMENT '加密密码',
  `role` VARCHAR(20) DEFAULT 'CUSTOMER' COMMENT '角色: CUSTOMER/ADMIN',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `status` TINYINT(4) DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 用户收货地址表 (user_address)
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID(主键)',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `detail_address` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_address_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表(1对1)';

-- 3. 产品分类表 (category)
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序优先级',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类表';

-- 4. 产品基础表 (product)
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `category_id` INT(11) DEFAULT NULL COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '产品名称',
  `description` TEXT COMMENT '产品描述',
  `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片路径',
  `is_active` TINYINT(4) DEFAULT 1 COMMENT '是否上架',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品基础信息表';

-- 5. 产品规格表 (product_sku)
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '规格ID',
  `product_id` BIGINT(20) NOT NULL COMMENT '关联产品ID',
  `spec_name` VARCHAR(50) NOT NULL COMMENT '规格名称',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '价格',
  `stock` INT(11) DEFAULT 9999 COMMENT '库存',
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`),
  CONSTRAINT `fk_sku_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品规格价格表';

-- 6. 购物车表 (cart_item)
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `product_id` BIGINT(20) NOT NULL COMMENT '产品ID',
  `sku_id` BIGINT(20) NOT NULL COMMENT '规格ID',
  `quantity` INT(11) DEFAULT 1 COMMENT '数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_product_sku` (`user_id`, `product_id`, `sku_id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_sku` FOREIGN KEY (`sku_id`) REFERENCES `product_sku` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车明细表';

-- 7. 订单主表 (orders)
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(64) NOT NULL COMMENT '订单编号',
  `user_id` BIGINT(20) NOT NULL COMMENT '下单用户ID',
  `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
  `status` INT(11) DEFAULT 0 COMMENT '状态: 0-待支付, 1-制作中, 2-已完成, 3-已取消',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人(快照)',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '电话(快照)',
  `receiver_address` VARCHAR(255) NOT NULL COMMENT '详细地址(快照)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 8. 订单明细表 (order_item)
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` BIGINT(20) NOT NULL COMMENT '关联订单ID',
  `product_id` BIGINT(20) NOT NULL COMMENT '产品ID',
  `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称(快照)',
  `product_image` VARCHAR(255) DEFAULT NULL COMMENT '产品图片(快照)',
  `sku_spec` VARCHAR(50) NOT NULL COMMENT '规格名称(快照)',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '下单时的单价',
  `quantity` INT(11) NOT NULL COMMENT '购买数量',
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  CONSTRAINT `fk_item_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';


-- ==========================================
-- 2. 测试数据 (Test Data)
-- ==========================================

-- 1. 插入用户数据 (10条)
INSERT INTO `users` (`username`, `password`, `role`, `phone`) VALUES
('admin', '123456', 'ADMIN', '13800138000'),
('user01', '123456', 'CUSTOMER', '13900000001'),
('user02', '123456', 'CUSTOMER', '13900000002'),
('user03', '123456', 'CUSTOMER', '13900000003'),
('user04', '123456', 'CUSTOMER', '13900000004'),
('user05', '123456', 'CUSTOMER', '13900000005'),
('user06', '123456', 'CUSTOMER', '13900000006'),
('user07', '123456', 'CUSTOMER', '13900000007'),
('user08', '123456', 'CUSTOMER', '13900000008'),
('user09', '123456', 'CUSTOMER', '13900000009');

-- 2. 插入用户地址数据 (10条)
INSERT INTO `user_address` (`user_id`, `receiver_name`, `receiver_phone`, `detail_address`) VALUES
(1, '管理员', '13800138000', '北京市海淀区中关村科技园1号楼'),
(2, '张三', '13900000001', '上海市浦东新区陆家嘴环路100号'),
(3, '李四', '13900000002', '广州市天河区珠江新城华夏路10号'),
(4, '王五', '13900000003', '深圳市南山区科技园南区W1-B'),
(5, '赵六', '13900000004', '成都市高新区天府大道北段1700号'),
(6, '钱七', '13900000005', '杭州市西湖区文三路90号'),
(7, '孙八', '13900000006', '南京市建邺区江东中路222号'),
(8, '周九', '13900000007', '武汉市洪山区珞喻路1037号'),
(9, '吴十', '13900000008', '西安市雁塔区长安南路88号'),
(10, '郑十一', '13900000009', '重庆市渝中区解放碑步行街1号');

-- 3. 插入产品分类 (4条)
INSERT INTO `category` (`name`, `sort_order`) VALUES
('经典咖啡', 1),
('创意特调', 2),
('精选甜点', 3),
('轻食简餐', 4);

-- 4. 插入产品数据 (10条)
INSERT INTO `product` (`category_id`, `name`, `description`, `image_url`) VALUES
(1, '美式咖啡', '精选阿拉比卡豆，口感纯正', '/static/picture/xuanchuan1.jpg'),
(1, '拿铁', '浓缩咖啡与鲜奶的完美融合', '/static/picture/xuanchuan2.jpg'),
(1, '卡布奇诺', '绵密奶泡，层次丰富', '/static/picture/xuanchuan3.jpg'),
(1, '焦糖玛奇朵', '甜蜜焦糖与香浓咖啡', '/static/picture/xuanchuan4.jpg'),
(2, '生椰拿铁', '冷榨生椰浆，清爽椰香', '/static/picture/xuanchuan5.jpg'),
(2, '燕麦拿铁', '植物基燕麦奶，健康之选', '/static/picture/xuanchuan6.jpg'),
(3, '提拉米苏', '经典意式甜点，入口即化', '/static/picture/xuanchuan7.jpg'),
(3, '纽约芝士蛋糕', '浓郁芝士，口感顺滑', '/static/picture/xuanchuan8.jpg'),
(3, '巧克力麦芬', '香浓巧克力，松软可口', '/static/picture/xuanchuan9.jpg'),
(4, '鸡肉凯撒沙拉', '新鲜蔬菜搭配嫩滑鸡胸肉', '/static/picture/xuanchuan10.jpg');

-- 5. 插入产品规格数据 (30条 - 咖啡分大中小，甜点只有标准)
-- 美式 (ID:1)
INSERT INTO `product_sku` (`product_id`, `spec_name`, `price`, `stock`) VALUES
(1, '中杯', 22.00, 100), (1, '大杯', 25.00, 100), (1, '超大杯', 28.00, 100);
-- 拿铁 (ID:2)
INSERT INTO `product_sku` (`product_id`, `spec_name`, `price`, `stock`) VALUES
(2, '中杯', 25.00, 100), (2, '大杯', 28.00, 100), (2, '超大杯', 31.00, 100);
-- 卡布奇诺 (ID:3)
INSERT INTO `product_sku` (`product_id`, `spec_name`, `price`, `stock`) VALUES
(3, '中杯', 25.00, 100), (3, '大杯', 28.00, 100), (3, '超大杯', 31.00, 100);
-- 焦糖玛奇朵 (ID:4)
INSERT INTO `product_sku` (`product_id`, `spec_name`, `price`, `stock`) VALUES
(4, '中杯', 28.00, 100), (4, '大杯', 31.00, 100), (4, '超大杯', 34.00, 100);
-- 生椰拿铁 (ID:5)
INSERT INTO `product_sku` (`product_id`, `spec_name`, `price`, `stock`) VALUES
(5, '中杯', 29.00, 100), (5, '大杯', 32.00, 100), (5, '超大杯', 35.00, 100);
-- 燕麦拿铁 (ID:6)
INSERT INTO `product_sku` (`product_id`, `spec_name`, `price`, `stock`) VALUES
(6, '中杯', 30.00, 100), (6, '大杯', 33.00, 100), (6, '超大杯', 36.00, 100);
-- 甜点类 (ID:7-10) 只有标准规格
INSERT INTO `product_sku` (`product_id`, `spec_name`, `price`, `stock`) VALUES
(7, '标准', 35.00, 50),
(8, '标准', 32.00, 50),
(9, '标准', 18.00, 50),
(10, '标准', 38.00, 30);

-- 6. 插入购物车数据 (10条)
INSERT INTO `cart_item` (`user_id`, `product_id`, `sku_id`, `quantity`) VALUES
(2, 1, 2, 1), -- user01 买了美式大杯
(2, 7, 19, 1), -- user01 买了提拉米苏
(3, 2, 5, 2), -- user02 买了拿铁大杯x2
(4, 5, 14, 1), -- user03 买了生椰拿铁大杯
(5, 10, 22, 1), -- user04 买了沙拉
(6, 3, 8, 1), -- user05 买了卡布奇诺大杯
(7, 4, 11, 1), -- user06 买了焦糖玛奇朵大杯
(8, 8, 20, 2), -- user07 买了芝士蛋糕x2
(9, 6, 17, 1), -- user08 买了燕麦拿铁大杯
(10, 9, 21, 3); -- user09 买了麦芬x3

-- 7. 插入订单数据 (10条)
INSERT INTO `orders` (`order_no`, `user_id`, `total_amount`, `status`, `receiver_name`, `receiver_phone`, `receiver_address`) VALUES
('ORD202310010001', 2, 57.00, 2, '张三', '13900000001', '上海市浦东新区陆家嘴环路100号'),
('ORD202310010002', 2, 25.00, 2, '张三', '13900000001', '上海市浦东新区陆家嘴环路100号'),
('ORD202310010003', 3, 56.00, 1, '李四', '13900000002', '广州市天河区珠江新城华夏路10号'),
('ORD202310010004', 4, 32.00, 0, '王五', '13900000003', '深圳市南山区科技园南区W1-B'),
('ORD202310010005', 5, 38.00, 2, '赵六', '13900000004', '成都市高新区天府大道北段1700号'),
('ORD202310010006', 6, 28.00, 2, '钱七', '13900000005', '杭州市西湖区文三路90号'),
('ORD202310010007', 7, 31.00, 1, '孙八', '13900000006', '南京市建邺区江东中路222号'),
('ORD202310010008', 8, 64.00, 2, '周九', '13900000007', '武汉市洪山区珞喻路1037号'),
('ORD202310010009', 9, 33.00, 0, '吴十', '13900000008', '西安市雁塔区长安南路88号'),
('ORD202310010010', 10, 54.00, 2, '郑十一', '13900000009', '重庆市渝中区解放碑步行街1号');

-- 8. 插入订单明细数据 (12条 - 部分订单包含多个商品)
INSERT INTO `order_item` (`order_id`, `product_id`, `product_name`, `sku_spec`, `price`, `quantity`) VALUES
(1, 1, '美式咖啡', '中杯', 22.00, 1),
(1, 7, '提拉米苏', '标准', 35.00, 1),
(2, 2, '拿铁', '中杯', 25.00, 1),
(3, 2, '拿铁', '大杯', 28.00, 2),
(4, 5, '生椰拿铁', '大杯', 32.00, 1),
(5, 10, '鸡肉凯撒沙拉', '标准', 38.00, 1),
(6, 3, '卡布奇诺', '大杯', 28.00, 1),
(7, 4, '焦糖玛奇朵', '大杯', 31.00, 1),
(8, 8, '纽约芝士蛋糕', '标准', 32.00, 2),
(9, 6, '燕麦拿铁', '大杯', 33.00, 1),
(10, 9, '巧克力麦芬', '标准', 18.00, 3);

SET FOREIGN_KEY_CHECKS = 1;