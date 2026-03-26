# Wukong Service 后端总览

## 项目定位

`wukong-service` 是面向 OpenClaw 的脚本生成后端。系统只对外暴露高层能力，不再让调用方直接拼装旧版 `tagGroup/tagItem` 入参。

当前主链路固定为：

1. 识别产品
2. 选择产品功能
3. 选择核心卖点
4. 选择策略
5. 生成脚本并返回质检结果

## 模块职责

- `product`：维护产品基础信息
- `product_feature`：维护产品下的功能
- `feature_selling_point`：维护功能对应的核心卖点
- `product_prompt`：维护产品默认提示词
- `strategy`：维护挂在 `product + feature + coreSellingPoint` 下的完整策略模板
- `/internal/openclaw/script/*`：供 OpenClaw 调用的内网高层接口

## OpenClaw 接入规则

- OpenClaw 只负责自然语言入口、会话补参、接口调度和结果回显
- 产品、功能、核心卖点、策略的候选与匹配全部由后端返回
- `strategy` 表主键固定为 `id`
- 其他表如需关联策略，外键字段名统一使用 `strategy_id`
- 第一版不再使用旧版 `tagGroup/tagItem` 作为生成入参模型
- 内网接口统一使用 `Authorization: Bearer <token>` 鉴权

## 变更记录

- `2026-03-26`：重建 FlyAgent 脚本生成主链路，新增产品、功能、核心卖点、策略模型与 OpenClaw 内网接口；内网接口改为 Bearer Token 鉴权。
