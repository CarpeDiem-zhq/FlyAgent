# 前端接口变更日志

## 2026-03-26 FlyAgent OpenClaw 接口新增

### 新增接口

- `POST /internal/openclaw/script/resolve`
- `POST /internal/openclaw/script/generate`
- `GET /product/list`
- `GET /product/detail`
- `POST /product/update`
- `GET /productFeature/list`
- `POST /productFeature/create`
- `POST /productFeature/update`
- `GET /sellingPoint/list`
- `POST /sellingPoint/create`
- `POST /sellingPoint/update`
- `GET /strategy/list`
- `POST /strategy/create`
- `POST /strategy/update`

### 对接说明

- OpenClaw 只需要对接 `/internal/openclaw/script/resolve` 与 `/internal/openclaw/script/generate`
- `resolve` 返回当前补参步骤、候选列表和追问文案
- `generate` 返回最终脚本文本、质检结果和后端拼装好的 `displayText`
- 内网接口必须带 `Authorization: Bearer <OPENCLAW_AUTH_TOKEN>`

### 兼容说明

- 旧版表单式 `tagGroup/tagItem` 接口仍可保留在项目内，但不再作为 OpenClaw 接入主链路
- 新链路的参数主模型变为 `productId + featureId + coreSellingPointId + strategyId`
