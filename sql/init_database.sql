-- PetCare 宠物服务预约平台 - 完整数据库脚本
-- 创建日期: 2026-06-05

-- 创建数据库
CREATE DATABASE IF NOT EXISTS pet_care DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE pet_care;

-- ==================== 表结构定义 ====================

-- 用户表
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `phone` VARCHAR(20) COMMENT '手机号',
    `email` VARCHAR(100) COMMENT '邮箱',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `avatar` VARCHAR(255) COMMENT '头像',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, TECHNICIAN-技师, ADMIN-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 宠物表
CREATE TABLE `pet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
    `type` VARCHAR(20) NOT NULL COMMENT '宠物类型: DOG-狗, CAT-猫, BIRD-鸟, OTHER-其他',
    `breed` VARCHAR(50) COMMENT '品种',
    `age` INT COMMENT '年龄',
    `gender` VARCHAR(10) COMMENT '性别: MALE-公, FEMALE-母',
    `weight` DECIMAL(5,2) COMMENT '体重(kg)',
    `color` VARCHAR(30) COMMENT '颜色',
    `photo` VARCHAR(255) COMMENT '照片',
    `health_note` TEXT COMMENT '健康备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物表';

-- 服务分类表
CREATE TABLE `service_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(255) COMMENT '分类描述',
    `icon` VARCHAR(255) COMMENT '图标',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务分类表';

-- 服务项目表
CREATE TABLE `service_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '服务名称',
    `description` TEXT COMMENT '服务描述',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `duration` INT NOT NULL COMMENT '时长(分钟)',
    `cover_image` VARCHAR(255) COMMENT '封面图',
    `detail_images` TEXT COMMENT '详情图片',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务项目表';

-- 技师表
CREATE TABLE `technician` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '技师ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `name` VARCHAR(50) NOT NULL COMMENT '技师姓名',
    `avatar` VARCHAR(255) COMMENT '头像',
    `phone` VARCHAR(20) COMMENT '手机号',
    `introduction` TEXT COMMENT '简介',
    `skills` VARCHAR(500) COMMENT '擅长技能',
    `work_years` INT COMMENT '工作年限',
    `rating` DECIMAL(3,2) NOT NULL DEFAULT 5.00 COMMENT '评分',
    `service_count` INT NOT NULL DEFAULT 0 COMMENT '服务次数',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-休息, 1-工作中, 2-离线',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技师表';

-- 预约表
CREATE TABLE `appointment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `appointment_no` VARCHAR(50) NOT NULL COMMENT '预约编号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
    `service_id` BIGINT NOT NULL COMMENT '服务ID',
    `technician_id` BIGINT COMMENT '技师ID',
    `appointment_time` DATETIME NOT NULL COMMENT '预约时间',
    `remark` VARCHAR(500) COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-已取消, 1-待确认, 2-已确认, 3-服务中, 4-已完成',
    `cancel_reason` VARCHAR(500) COMMENT '取消原因',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_appointment_no` (`appointment_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_appointment_time` (`appointment_time`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 订单表
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `appointment_id` BIGINT NOT NULL COMMENT '预约ID',
    `service_id` BIGINT NOT NULL COMMENT '服务ID',
    `technician_id` BIGINT COMMENT '技师ID',
    `pet_id` BIGINT NOT NULL COMMENT '宠物ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
    `pay_status` TINYINT NOT NULL DEFAULT 0 COMMENT '支付状态: 0-未支付, 1-已支付, 2-已退款',
    `pay_method` VARCHAR(20) COMMENT '支付方式: ALIPAY-支付宝, WECHAT-微信, CASH-现金',
    `pay_time` DATETIME COMMENT '支付时间',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '订单状态: 0-已取消, 1-待支付, 2-已支付, 3-已完成',
    `remark` VARCHAR(500) COMMENT '备注',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_appointment_id` (`appointment_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 评价表
CREATE TABLE `review` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `service_id` BIGINT NOT NULL COMMENT '服务ID',
    `technician_id` BIGINT COMMENT '技师ID',
    `rating` TINYINT NOT NULL COMMENT '评分(1-5星)',
    `content` TEXT COMMENT '评价内容',
    `images` TEXT COMMENT '评价图片',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_id` (`order_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_technician_id` (`technician_id`),
    KEY `idx_rating` (`rating`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- ==================== 初始化数据 ====================

-- 插入默认管理员账号 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `phone`, `real_name`, `role`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '13800138000', '管理员', 'ADMIN', 1);

-- 服务分类数据
INSERT INTO `service_category` (`name`, `description`, `icon`, `sort`, `status`) VALUES
('美容护理', '宠物美容、洗澡、修剪等服务', 'beauty', 1, 1),
('医疗保健', '宠物体检、疫苗接种、疾病治疗', 'health', 2, 1),
('训练教育', '宠物行为训练、服从训练', 'training', 3, 1),
('寄养服务', '宠物短期或长期寄养', 'boarding', 4, 1),
('其他服务', '宠物摄影、宠物殡葬等其他服务', 'other', 5, 1);

-- 服务项目数据
INSERT INTO `service_item` (`category_id`, `name`, `description`, `price`, `duration`, `status`, `sales`) VALUES
(1, '基础洗澡', '包含洗澡、吹干、基础梳理', 80.00, 60, 1, 156),
(1, '全身美容', '包含洗澡、造型修剪、指甲修剪', 150.00, 120, 1, 89),
(1, 'SPA护理', '深层清洁、护肤SPA', 200.00, 90, 1, 45),
(2, '健康体检', '全面身体检查、血液检测', 300.00, 60, 1, 67),
(2, '疫苗接种', '狂犬疫苗、多联疫苗', 120.00, 30, 1, 234),
(3, '基础训练', '坐下、握手等基础指令', 500.00, 60, 1, 34),
(3, '行为纠正', '纠正不良行为习惯', 800.00, 90, 1, 23),
(4, '日托寄养', '白天寄养服务', 100.00, 1440, 1, 78),
(4, '过夜寄养', '含住宿的寄养服务', 180.00, 1440, 1, 56);

-- 说明：
-- 1. 管理员账号信息：
--    用户名: admin
--    密码: admin123
--    角色: ADMIN (管理员)
-- 
-- 2. 密码使用 BCrypt 加密存储
-- 3. 已预置5个服务分类和9个服务项目，可通过管理后台进行管理
-- 4. 技师、普通用户需要通过系统注册或管理后台添加
-- 5. 首次登录后建议修改默认密码

