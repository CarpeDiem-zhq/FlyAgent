
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_name` varchar(64) NOT NULL COMMENT '类目名称',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品类目';

CREATE TABLE `correction_input` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint NOT NULL COMMENT '产品id',
  `asset_id` bigint DEFAULT NULL COMMENT '资产id',
  `input_type` varchar(32) NOT NULL COMMENT '输入类型',
  `reason_codes` text COMMENT '原因编码列表',
  `suggestion` text COMMENT '建议',
  `metric_snapshot` text COMMENT '指标快照',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_correction_input_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='纠偏输入';

CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_name` varchar(128) NOT NULL COMMENT '产品名称',
  `category_id` bigint DEFAULT NULL COMMENT '类目id',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_category` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品';

CREATE TABLE `prompt_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint NOT NULL COMMENT '产品id',
  `prompt_name` varchar(128) NOT NULL COMMENT '提示词名称',
  `system_prompt` text COMMENT '系统提示词',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0.否 1.是',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_prompt_config_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提示词配置';

CREATE TABLE `rule_global` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `rule_content` text NOT NULL COMMENT '规则内容',
  `version` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0.否 1.是',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='全局规则';

CREATE TABLE `rule_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint NOT NULL COMMENT '产品id',
  `rule_content` text NOT NULL COMMENT '规则内容',
  `version` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0.否 1.是',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_rule_product_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='产品规则';

CREATE TABLE `script_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint NOT NULL COMMENT '产品id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `prompt_id` bigint DEFAULT NULL COMMENT '提示词配置id',
  `prompt_snapshot` longtext COMMENT '提示词快照',
  `rule_snapshot` text COMMENT '规则快照',
  `input_snapshot` text COMMENT '输入快照',
  `tag_snapshot` text COMMENT '标签快照',
  `user_input_snapshot` text COMMENT '用户输入快照',
  `case_snapshot` text COMMENT '案例快照',
  `model_name` varchar(64) DEFAULT NULL COMMENT '模型名称',
  `route_strategy` varchar(64) DEFAULT NULL COMMENT '路由策略',
  `output_content` longtext COMMENT '生成内容',
  `parent_asset_id` bigint DEFAULT NULL COMMENT '父资产id',
  `revision_seq` int NOT NULL DEFAULT '0' COMMENT '修订序号',
  `copy_count` int NOT NULL DEFAULT '0' COMMENT '复制次数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞次数',
  `favorite_count` int NOT NULL DEFAULT '0' COMMENT '收藏次数',
  `positive_feedback_count` int NOT NULL DEFAULT '0' COMMENT '正向反馈次数',
  `negative_feedback_count` int NOT NULL DEFAULT '0' COMMENT '负向反馈次数',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_script_asset_product` (`product_id`),
  KEY `idx_script_asset_user` (`user_id`),
  KEY `idx_script_asset_prompt` (`prompt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='脚本资产';

CREATE TABLE `script_copy_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_id` bigint NOT NULL COMMENT '资产id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_script_copy_asset` (`asset_id`),
  KEY `idx_script_copy_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='脚本复制日志';

CREATE TABLE `script_favorite_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_id` bigint NOT NULL COMMENT '资产id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_script_favorite_user_asset` (`user_id`,`asset_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='脚本收藏日志';

CREATE TABLE `script_feedback` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_id` bigint NOT NULL COMMENT '资产id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `satisfied` tinyint(1) NOT NULL COMMENT '是否满意：0.否 1.是',
  `reason_codes` text COMMENT '原因编码列表',
  `suggestion` text COMMENT '建议',
  `rerun` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否回炉：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_script_feedback_asset` (`asset_id`),
  KEY `idx_script_feedback_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='脚本反馈';

CREATE TABLE `script_like_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `asset_id` bigint NOT NULL COMMENT '资产id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_script_like_user_asset` (`user_id`,`asset_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='脚本点赞日志';

CREATE TABLE `tag_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint NOT NULL COMMENT '产品id',
  `group_code` varchar(64) NOT NULL COMMENT '标签组编码',
  `group_name` varchar(128) NOT NULL COMMENT '标签组名称',
  `input_type` varchar(32) NOT NULL COMMENT '输入类型',
  `required` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否必填：0.否 1.是',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_group_product_code` (`product_id`,`group_code`),
  KEY `idx_tag_group_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签组';

CREATE TABLE `tag_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` bigint NOT NULL COMMENT '产品id',
  `group_id` bigint NOT NULL COMMENT '标签组id',
  `tag_name` varchar(128) NOT NULL COMMENT '标签名称',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0.否 1.是',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `del` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0.否 1.是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_tag_item_product` (`product_id`),
  KEY `idx_tag_item_group` (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='标签项';

