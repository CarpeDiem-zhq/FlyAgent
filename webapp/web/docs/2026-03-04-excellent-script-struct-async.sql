ALTER TABLE `excellent_script_struct`
  ADD COLUMN `sync_status` VARCHAR(16) NOT NULL DEFAULT 'SYNCING' COMMENT '同步状态：SYNCING/SUCCESS/FAILED' AFTER `segment_id`,
  ADD COLUMN `sync_error_msg` VARCHAR(512) NULL COMMENT '同步失败原因' AFTER `sync_status`,
  MODIFY COLUMN `structured_script` LONGTEXT NULL COMMENT '解析后的脚本结构',
  MODIFY COLUMN `segment_id` VARCHAR(128) NULL COMMENT '文档块id';

ALTER TABLE `excellent_script_struct`
  ADD INDEX `idx_sync_status` (`sync_status`, `update_time`);

