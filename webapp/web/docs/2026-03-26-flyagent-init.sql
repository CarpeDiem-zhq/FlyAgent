CREATE TABLE IF NOT EXISTS `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `product_desc` TEXT DEFAULT NULL COMMENT '产品描述',
  `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0否 1是',
  `del` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品表';

CREATE TABLE IF NOT EXISTS `prompt_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `system_prompt` TEXT NOT NULL COMMENT '系统提示词',
  `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0否 1是',
  `del` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_prompt` (`product_id`, `del`),
  KEY `idx_prompt_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品系统提示词表';

CREATE TABLE IF NOT EXISTS `product_feature` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `feature_name` VARCHAR(200) NOT NULL COMMENT '功能名称',
  `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0否 1是',
  `del` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品功能表';

CREATE TABLE IF NOT EXISTS `feature_selling_point` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `feature_id` BIGINT NOT NULL COMMENT '功能ID',
  `selling_point_name` VARCHAR(500) NOT NULL COMMENT '核心卖点名称',
  `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0否 1是',
  `del` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_feature` (`product_id`, `feature_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='功能核心卖点表';

CREATE TABLE IF NOT EXISTS `strategy` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '策略ID',
  `strategy_name` VARCHAR(200) NOT NULL COMMENT '策略名称',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `feature_id` BIGINT NOT NULL COMMENT '产品功能ID',
  `core_selling_point_id` BIGINT NOT NULL COMMENT '核心卖点ID',
  `target_audience` VARCHAR(500) NOT NULL COMMENT '目标受众',
  `target_scene` VARCHAR(500) NOT NULL COMMENT '目标场景',
  `tone_style` VARCHAR(500) NOT NULL COMMENT '语调风格',
  `call_to_action` VARCHAR(500) NOT NULL COMMENT '行动指令',
  `ad_words` VARCHAR(200) NOT NULL COMMENT '字数限制',
  `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0否 1是',
  `del` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_strategy_product` (`product_id`),
  KEY `idx_strategy_feature` (`feature_id`),
  KEY `idx_strategy_selling_point` (`core_selling_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='策略表';

CREATE TABLE IF NOT EXISTS `script_asset` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `feature_id` BIGINT NOT NULL COMMENT '功能ID',
  `core_selling_point_id` BIGINT NOT NULL COMMENT '核心卖点ID',
  `strategy_id` BIGINT NOT NULL COMMENT '策略ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `prompt_id` BIGINT NOT NULL COMMENT '提示词ID',
  `system_prompt_snapshot` LONGTEXT NOT NULL COMMENT '系统提示词快照',
  `strategy_snapshot` LONGTEXT NOT NULL COMMENT '策略快照',
  `feature_snapshot` LONGTEXT NOT NULL COMMENT '功能快照',
  `selling_point_snapshot` LONGTEXT NOT NULL COMMENT '卖点快照',
  `script_title` VARCHAR(500) NOT NULL COMMENT '脚本标题',
  `script_content` LONGTEXT NOT NULL COMMENT '脚本内容',
  `model_name` VARCHAR(200) DEFAULT NULL COMMENT '模型名称',
  `del` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_asset_product` (`product_id`),
  KEY `idx_asset_feature` (`feature_id`),
  KEY `idx_asset_strategy` (`strategy_id`),
  KEY `idx_asset_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='脚本资产表';

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `phone` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '头像地址',
  `password` char(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '密码',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：1.正常 2.禁用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '备注',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';