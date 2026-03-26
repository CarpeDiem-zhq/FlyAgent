CREATE TABLE IF NOT EXISTS `excellent_script_struct` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `product_id` BIGINT NOT NULL COMMENT '产品id',
  `product_name` VARCHAR(128) NOT NULL COMMENT '产品名称',
  `function_name` VARCHAR(128) NOT NULL COMMENT '功能名称',
  `excellent_script` LONGTEXT NOT NULL COMMENT '优秀脚本',
  `structured_script` LONGTEXT NOT NULL COMMENT '解析后的脚本结构',
  `knowledge_dataset_id` VARCHAR(128) NOT NULL COMMENT '知识库id',
  `knowledge_document_id` VARCHAR(128) NOT NULL COMMENT '知识库文档id',
  `segment_id` VARCHAR(128) NOT NULL COMMENT '文档块id',
  `del` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_time` (`product_id`, `create_time`),
  KEY `idx_segment_id` (`segment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='优秀脚本结构记录';

