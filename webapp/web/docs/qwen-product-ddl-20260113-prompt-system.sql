-- prompt_config 仅保留系统提示词，删除提示词版本与补丁表
ALTER TABLE prompt_config
  CHANGE COLUMN prompt_desc system_prompt TEXT NULL COMMENT '系统提示词';

DROP TABLE IF EXISTS prompt_version;
DROP TABLE IF EXISTS prompt_patch;

ALTER TABLE script_asset
  DROP COLUMN prompt_version;
