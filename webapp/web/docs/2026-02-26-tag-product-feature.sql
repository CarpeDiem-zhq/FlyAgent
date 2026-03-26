-- 新增产品功能标签组，并建立核心卖点与产品功能的归属关系

-- 1. tag_item 增加所属产品功能标签项id
ALTER TABLE `tag_item`
    ADD COLUMN `feature_item_id` BIGINT NULL COMMENT '所属产品功能标签项id' AFTER `tag_name`;

ALTER TABLE `tag_item`
    ADD INDEX `idx_tag_item_feature_item` (`feature_item_id`);

-- 2. 为现有产品补齐固定标签组：产品功能（单选）
INSERT INTO `tag_group` (
    `product_id`, `group_code`, `group_name`, `input_type`, `required`, `sort_order`, `del`, `create_time`, `update_time`
)
SELECT p.`id`, 'product_feature', '产品功能', 'single_select', 1, 2, 0, NOW(), NOW()
FROM `product` p
LEFT JOIN `tag_group` tg
    ON tg.`product_id` = p.`id`
   AND tg.`group_code` = 'product_feature'
   AND tg.`del` = 0
WHERE p.`del` = 0
  AND tg.`id` IS NULL;

-- 3. 为每个产品功能标签组初始化一个默认标签项，避免历史数据无法使用
INSERT INTO `tag_item` (
    `product_id`, `group_id`, `tag_name`, `feature_item_id`, `enabled`, `sort_order`, `del`, `create_time`, `update_time`
)
SELECT fg.`product_id`, fg.`id`, '默认功能', NULL, 1, 0, 0, NOW(), NOW()
FROM `tag_group` fg
LEFT JOIN `tag_item` fi
    ON fi.`group_id` = fg.`id`
   AND fi.`del` = 0
WHERE fg.`group_code` = 'product_feature'
  AND fg.`del` = 0
  AND fi.`id` IS NULL;

-- 4. 历史核心卖点标签项，默认归属到该产品的第一个产品功能标签项
UPDATE `tag_item` core
JOIN `tag_group` cg
    ON cg.`id` = core.`group_id`
   AND cg.`group_code` = 'core_selling_points'
   AND cg.`del` = 0
JOIN `tag_group` fg
    ON fg.`product_id` = cg.`product_id`
   AND fg.`group_code` = 'product_feature'
   AND fg.`del` = 0
JOIN (
    SELECT `group_id`, MIN(`id`) AS `feature_item_id`
    FROM `tag_item`
    WHERE `del` = 0
    GROUP BY `group_id`
) first_feature
    ON first_feature.`group_id` = fg.`id`
SET core.`feature_item_id` = first_feature.`feature_item_id`
WHERE core.`del` = 0
  AND core.`feature_item_id` IS NULL;