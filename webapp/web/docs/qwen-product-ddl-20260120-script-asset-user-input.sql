-- 增加用户输入快照字段
ALTER TABLE script_asset
  ADD COLUMN user_input_snapshot text NULL COMMENT '用户输入快照' AFTER tag_snapshot;
