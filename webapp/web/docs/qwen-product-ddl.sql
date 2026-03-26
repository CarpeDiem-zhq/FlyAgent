-- 千问脚本生产系统闭环表结构

CREATE TABLE IF NOT EXISTS category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  category_name VARCHAR(64) NOT NULL COMMENT '类目名称',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品类目';

CREATE TABLE IF NOT EXISTS product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_name VARCHAR(128) NOT NULL COMMENT '产品名称',
  category_id BIGINT NULL COMMENT '类目id',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_product_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品';

CREATE TABLE IF NOT EXISTS rule_global (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  rule_content TEXT NOT NULL COMMENT '规则内容',
  version INT NOT NULL DEFAULT 1 COMMENT '版本号',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='全局规则';

CREATE TABLE IF NOT EXISTS rule_product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  rule_content TEXT NOT NULL COMMENT '规则内容',
  version INT NOT NULL DEFAULT 1 COMMENT '版本号',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_rule_product_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品规则';

CREATE TABLE IF NOT EXISTS tag_group (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  group_code VARCHAR(64) NOT NULL COMMENT '标签组编码',
  group_name VARCHAR(128) NOT NULL COMMENT '标签组名称',
  input_type VARCHAR(32) NOT NULL COMMENT '输入类型',
  required TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否必填：0.否 1.是',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_tag_group_product (product_id),
  UNIQUE KEY uk_tag_group_product_code (product_id, group_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签组';

CREATE TABLE IF NOT EXISTS tag_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  group_id BIGINT NOT NULL COMMENT '标签组id',
  tag_code VARCHAR(64) NOT NULL COMMENT '标签编码',
  tag_name VARCHAR(128) NOT NULL COMMENT '标签名称',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_tag_item_product (product_id),
  INDEX idx_tag_item_group (group_id),
  UNIQUE KEY uk_tag_item_product_code (product_id, tag_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签项';

CREATE TABLE IF NOT EXISTS template (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
  template_desc VARCHAR(256) NULL COMMENT '模板说明',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_template_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板';

CREATE TABLE IF NOT EXISTS template_version (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  template_id BIGINT NOT NULL COMMENT '模板id',
  version INT NOT NULL COMMENT '版本号',
  duration_type VARCHAR(16) NOT NULL COMMENT '时长类型',
  structure_tag VARCHAR(64) NULL COMMENT '结构标签',
  parent_version_id BIGINT NULL COMMENT '父版本id',
  revision_note VARCHAR(256) NULL COMMENT '修订说明',
  copy_count INT NOT NULL DEFAULT 0 COMMENT '复制次数',
  like_count INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
  positive_feedback_count INT NOT NULL DEFAULT 0 COMMENT '正向反馈次数',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_template_version_template (template_id),
  UNIQUE KEY uk_template_version (template_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板版本';

CREATE TABLE IF NOT EXISTS template_module (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  template_version_id BIGINT NOT NULL COMMENT '模板版本id',
  module_order INT NOT NULL COMMENT '模块顺序',
  module_name VARCHAR(128) NOT NULL COMMENT '模块名称',
  required TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否必填：0.否 1.是',
  guideline TEXT NULL COMMENT '编写要点',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_template_module_version (template_version_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板模块';

CREATE TABLE IF NOT EXISTS template_tag_mapping (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  template_id BIGINT NOT NULL COMMENT '模板id',
  duration_type VARCHAR(16) NOT NULL COMMENT '时长类型',
  must_tags TEXT NULL COMMENT '必选标签',
  boost_tags TEXT NULL COMMENT '加权标签',
  exclude_tags TEXT NULL COMMENT '排除标签',
  priority INT NOT NULL DEFAULT 0 COMMENT '优先级',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_template_tag_mapping_product (product_id),
  INDEX idx_template_tag_mapping_template (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模板标签映射';

CREATE TABLE IF NOT EXISTS rule_patch (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  patch_content TEXT NOT NULL COMMENT '补丁内容',
  version INT NOT NULL COMMENT '版本号',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_rule_patch_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则补丁';

CREATE TABLE IF NOT EXISTS prompt_patch (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  tags_json TEXT NULL COMMENT '标签条件',
  patch_content TEXT NOT NULL COMMENT '补丁内容',
  version INT NOT NULL COMMENT '版本号',
  enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：0.否 1.是',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_prompt_patch_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词补丁';

CREATE TABLE IF NOT EXISTS script_asset (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  user_id BIGINT NOT NULL COMMENT '用户id',
  template_id BIGINT NULL COMMENT '模板id',
  template_version INT NULL COMMENT '模板版本号',
  rule_snapshot TEXT NULL COMMENT '规则快照',
  input_snapshot TEXT NULL COMMENT '输入快照',
  tag_snapshot TEXT NULL COMMENT '标签快照',
  case_snapshot TEXT NULL COMMENT '案例快照',
  model_name VARCHAR(64) NULL COMMENT '模型名称',
  route_strategy VARCHAR(64) NULL COMMENT '路由策略',
  output_content LONGTEXT NULL COMMENT '生成内容',
  parent_asset_id BIGINT NULL COMMENT '父资产id',
  revision_seq INT NOT NULL DEFAULT 0 COMMENT '修订序号',
  copy_count INT NOT NULL DEFAULT 0 COMMENT '复制次数',
  like_count INT NOT NULL DEFAULT 0 COMMENT '点赞次数',
  favorite_count INT NOT NULL DEFAULT 0 COMMENT '收藏次数',
  positive_feedback_count INT NOT NULL DEFAULT 0 COMMENT '正向反馈次数',
  negative_feedback_count INT NOT NULL DEFAULT 0 COMMENT '负向反馈次数',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_script_asset_product (product_id),
  INDEX idx_script_asset_user (user_id),
  INDEX idx_script_asset_template (template_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脚本资产';

CREATE TABLE IF NOT EXISTS script_copy_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  asset_id BIGINT NOT NULL COMMENT '资产id',
  user_id BIGINT NOT NULL COMMENT '用户id',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_script_copy_asset (asset_id),
  INDEX idx_script_copy_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脚本复制日志';

CREATE TABLE IF NOT EXISTS script_like_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  asset_id BIGINT NOT NULL COMMENT '资产id',
  user_id BIGINT NOT NULL COMMENT '用户id',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_script_like_user_asset (user_id, asset_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脚本点赞日志';

CREATE TABLE IF NOT EXISTS script_favorite_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  asset_id BIGINT NOT NULL COMMENT '资产id',
  user_id BIGINT NOT NULL COMMENT '用户id',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_script_favorite_user_asset (user_id, asset_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脚本收藏日志';

CREATE TABLE IF NOT EXISTS script_feedback (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  asset_id BIGINT NOT NULL COMMENT '资产id',
  user_id BIGINT NOT NULL COMMENT '用户id',
  satisfied TINYINT(1) NOT NULL COMMENT '是否满意：0.否 1.是',
  reason_codes TEXT NULL COMMENT '原因编码列表',
  suggestion TEXT NULL COMMENT '建议',
  rerun TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否回炉：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_script_feedback_asset (asset_id),
  INDEX idx_script_feedback_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脚本反馈';

CREATE TABLE IF NOT EXISTS excellent_case (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  asset_id BIGINT NOT NULL COMMENT '资产id',
  product_id BIGINT NOT NULL COMMENT '产品id',
  promote_type VARCHAR(16) NOT NULL COMMENT '晋级类型',
  usage_mode VARCHAR(16) NOT NULL COMMENT '使用模式',
  case_snapshot TEXT NULL COMMENT '案例快照',
  del TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0.否 1.是',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_excellent_case_product (product_id),
  UNIQUE KEY uk_excellent_case_asset (asset_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优秀案例';

CREATE TABLE IF NOT EXISTS correction_input (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  product_id BIGINT NOT NULL COMMENT '产品id',
  asset_id BIGINT NULL COMMENT '资产id',
  input_type VARCHAR(32) NOT NULL COMMENT '输入类型',
  reason_codes TEXT NULL COMMENT '原因编码列表',
  suggestion TEXT NULL COMMENT '建议',
  metric_snapshot TEXT NULL COMMENT '指标快照',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  INDEX idx_correction_input_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='纠偏输入';
