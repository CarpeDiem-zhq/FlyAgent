CREATE TABLE IF NOT EXISTS `strategy_selling_point` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `strategy_id` BIGINT NOT NULL COMMENT '策略ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `feature_id` BIGINT NOT NULL COMMENT '功能ID',
  `selling_point_id` BIGINT NOT NULL COMMENT '核心卖点ID',
  `selling_point_name` VARCHAR(500) NOT NULL COMMENT '核心卖点名称快照',
  `del` TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0否 1是',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_strategy_selling_point` (`strategy_id`, `selling_point_id`, `del`),
  KEY `idx_strategy_selling_point_query` (`product_id`, `feature_id`, `selling_point_id`, `del`),
  KEY `idx_strategy_selling_point_strategy` (`strategy_id`, `del`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='策略与核心卖点关联表';

ALTER TABLE `strategy`
  ADD COLUMN `feature_name` VARCHAR(200) DEFAULT NULL COMMENT '功能名称快照' AFTER `feature_id`,
  ADD COLUMN `core_selling_point_names` TEXT DEFAULT NULL COMMENT '核心卖点名称快照(JSON数组)' AFTER `core_selling_point_id`;

ALTER TABLE `script_asset`
  ADD COLUMN `core_selling_point_ids` LONGTEXT DEFAULT NULL COMMENT '核心卖点ID列表(JSON数组)' AFTER `core_selling_point_id`;

INSERT INTO `strategy_selling_point` (`strategy_id`, `product_id`, `feature_id`, `selling_point_id`, `selling_point_name`, `del`)
SELECT
  s.`id`,
  s.`product_id`,
  s.`feature_id`,
  s.`core_selling_point_id`,
  COALESCE(sp.`selling_point_name`, ''),
  0
FROM `strategy` s
LEFT JOIN `feature_selling_point` sp ON sp.`id` = s.`core_selling_point_id` AND sp.`del` = 0
LEFT JOIN `strategy_selling_point` rel
  ON rel.`strategy_id` = s.`id`
  AND rel.`selling_point_id` = s.`core_selling_point_id`
  AND rel.`del` = 0
WHERE s.`del` = 0
  AND s.`core_selling_point_id` IS NOT NULL
  AND rel.`id` IS NULL;

UPDATE `strategy` s
LEFT JOIN `product_feature` pf ON pf.`id` = s.`feature_id` AND pf.`del` = 0
SET
  s.`feature_name` = COALESCE(pf.`feature_name`, s.`feature_name`),
  s.`core_selling_point_names` = (
    SELECT JSON_ARRAYAGG(rel.`selling_point_name`)
    FROM `strategy_selling_point` rel
    WHERE rel.`strategy_id` = s.`id`
      AND rel.`del` = 0
  )
WHERE s.`del` = 0;

UPDATE `script_asset`
SET `core_selling_point_ids` = JSON_ARRAY(`core_selling_point_id`)
WHERE `core_selling_point_id` IS NOT NULL
  AND (`core_selling_point_ids` IS NULL OR `core_selling_point_ids` = '');
