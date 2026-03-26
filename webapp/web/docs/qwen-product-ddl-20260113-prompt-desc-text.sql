-- 调整提示词说明字段长度
ALTER TABLE prompt_config MODIFY COLUMN prompt_desc TEXT NULL COMMENT '提示词说明';