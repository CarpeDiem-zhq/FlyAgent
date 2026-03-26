-- 提示词配置替换模板结构
DROP TABLE IF EXISTS template_tag_mapping;
DROP TABLE IF EXISTS template_module;
DROP TABLE IF EXISTS template_version;
DROP TABLE IF EXISTS template;

CREATE TABLE IF NOT EXISTS prompt_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  prompt_name VARCHAR(128) NOT NULL COMMENT '提示词名称',
  prompt_desc VARCHAR(256) NULL COMMENT '提示词说明',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_prompt_config_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词配置';

CREATE TABLE IF NOT EXISTS prompt_version (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  prompt_id BIGINT NOT NULL COMMENT '提示词配置id',
  version INT NOT NULL COMMENT '版本号',
  system_prompt LONGTEXT NOT NULL COMMENT '系统提示词',
  input_prompt LONGTEXT NULL COMMENT '输入提示词',
  output_prompt LONGTEXT NULL COMMENT '输出提示词',
  example_prompt LONGTEXT NULL COMMENT '示例提示词',
  revision_note VARCHAR(256) NULL COMMENT '修订说明',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_prompt_version_prompt (prompt_id),
  UNIQUE KEY uk_prompt_version (prompt_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词版本';

ALTER TABLE script_asset DROP INDEX idx_script_asset_template;
ALTER TABLE script_asset DROP COLUMN template_id;
ALTER TABLE script_asset DROP COLUMN template_version;
ALTER TABLE script_asset ADD COLUMN prompt_id BIGINT NULL COMMENT '提示词配置id' AFTER user_id;
ALTER TABLE script_asset ADD COLUMN prompt_version INT NULL COMMENT '提示词版本号' AFTER prompt_id;
ALTER TABLE script_asset ADD COLUMN prompt_snapshot LONGTEXT NULL COMMENT '提示词快照' AFTER prompt_version;
ALTER TABLE script_asset ADD INDEX idx_script_asset_prompt (prompt_id);