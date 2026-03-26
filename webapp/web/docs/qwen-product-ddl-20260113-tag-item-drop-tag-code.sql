-- 删除 tag_item.tag_code 字段及唯一索引
ALTER TABLE `tag_item` DROP INDEX `uk_tag_item_product_code`;
ALTER TABLE `tag_item` DROP COLUMN `tag_code`;