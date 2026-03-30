# Wukong Service 新版后端总览

## 2026-03-27 多核心卖点策略升级

- 策略模型从“单功能 + 单核心卖点”升级为“单功能 + 多核心卖点”。
- 新增关联表：`strategy_selling_point`
- `strategy` 新增：
  - `feature_name`
  - `core_selling_point_names`
- `script_asset` 新增 `core_selling_point_ids`，卖点快照改为多卖点数组。
- `GET /strategy/list` 支持按核心卖点集合查询，并按完整匹配规则返回策略。
- `POST /strategy/create`、`POST /strategy/update` 的核心卖点入参改为多选数组。
- `POST /script/generate` 的核心卖点入参改为多选数组。
- 功能名或核心卖点名称修改后，会同步回写策略中的名称快照。

## 项目定位

`wukong-service` 现在按 FlyAgent 新版业务模型重构，只保留用户登录体系与新脚本生成链路。

旧版这些能力已经移除：

- 类目
- 标签组/标签项
- 全局规则/产品规则
- 优秀案例结构
- 生成批次/生成历史/行为反馈
- Dify 对话与应用使用统计

新版后端只围绕这 7 类核心对象工作：

1. `product`：产品基础信息
2. `prompt_config`：产品系统提示词
3. `product_feature`：产品功能
4. `feature_selling_point`：功能核心卖点
5. `strategy`：生成策略模板
6. `strategy_selling_point`：策略与核心卖点关联
7. `script_asset`：脚本资产库

## 业务主流程

后台维护流程：

1. 新增产品
2. 为产品维护系统提示词
3. 为产品维护功能
4. 为功能维护核心卖点
5. 为产品 + 功能 + 核心卖点集合维护策略

脚本生成流程：

1. 选择产品
2. 选择功能
3. 选择一个或多个核心卖点
4. 选择与当前卖点集合完整匹配的策略
5. 后端自动装配产品系统提示词、功能信息、核心卖点和策略信息
6. 调用大模型生成脚本
7. 执行基础质检
8. 保存到 `script_asset`

## OpenClaw 接入

OpenClaw 在新版中只承担两件事：

- 自然语言入口和多轮补参
- 调用后端内网接口并回显结果

后端内网接口保留：

- `POST /internal/openclaw/script/resolve`
- `POST /internal/openclaw/script/generate`

约束说明：

- OpenClaw 不再处理旧版 `tagGroup/tagItem` 模型
- OpenClaw 不再拼装规则、模板、案例
- 管理端生成参数主模型固定为 `productId + featureId + coreSellingPointIds + strategyId`
- 内网接口统一使用 `Authorization: Bearer <token>` 鉴权

## 数据模型说明

### product

- 维护产品名称、产品描述、启停状态
- 不再带类目语义

### prompt_config

- 每个产品只维护一条当前系统提示词
- 生成脚本时按 `product_id` 直接读取

### product_feature

- 维护产品下可选功能
- 一个产品对应多个功能

### feature_selling_point

- 维护功能下核心卖点
- 一个功能对应多个核心卖点

### strategy

- 主键固定为 `id`
- 用宽表保存完整策略模板
- 挂载在 `product_id + feature_id` 之下
- 通过 `strategy_selling_point` 关联多个核心卖点
- 当前字段包括：
  - `strategy_name`
  - `feature_name`
  - `core_selling_point_names`
  - `target_audience`
  - `target_scene`
  - `tone_style`
  - `call_to_action`
  - `ad_words`

### script_asset

- 保存每次生成出来的脚本
- 存储产品、功能、卖点、策略、提示词快照和脚本正文
- 卖点按 ID 列表和多卖点快照存储
- 不再拆分批次表、历史表、反馈表

## 版本说明

- `2026-03-26`：FlyAgent 后端按新业务模型重构为第一版正式结构
