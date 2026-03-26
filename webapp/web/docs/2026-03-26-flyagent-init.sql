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
  `prompt_id` BIGINT DEFAULT NULL COMMENT '提示词ID',
  `enabled` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0否 1是',
  `del` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_feature_id` (`feature_id`),
  KEY `idx_core_selling_point_id` (`core_selling_point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='策略表';
