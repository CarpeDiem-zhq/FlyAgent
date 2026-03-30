# 前端接口变更日志

## 2026-03-27 多核心卖点策略升级

- `GET /strategy/list`
  - `coreSellingPointId` 改为 `coreSellingPointIds`
- `POST /strategy/create`
  - `coreSellingPointId` 改为 `coreSellingPointIds`
- `POST /strategy/update`
  - `coreSellingPointId` 改为 `coreSellingPointIds`
- `POST /script/generate`
  - `coreSellingPointId` 改为 `coreSellingPointIds`
- `/strategy/list` 的策略响应新增：
  - `featureName`
  - `coreSellingPointIds`
  - `coreSellingPointNames`
- `/script/generate` 响应中的核心卖点改为数组：
  - `coreSellingPointIds`
  - `coreSellingPointNames`
- `/scriptAsset/my`、`/scriptAsset/all`、`/scriptAsset/detail` 的核心卖点字段改为 `coreSellingPointIds`
- 前端工作台改造点：
  - 策略弹窗中“所属功能”切换后重新加载该功能下的核心卖点
  - 策略配置中的核心卖点改为多选
  - 生成页中的核心卖点改为多选
  - 策略列表按核心卖点集合完整匹配筛选

## 2026-03-26 新版 FlyAgent 后端重构

### 保留模块

- `user` 与登录接口保留
- OpenClaw 内网接口保留

### 新版业务接口

- `GET /product/list`
- `GET /product/detail`
- `POST /product/create`
- `POST /product/update`
- `GET /prompt/list`
- `POST /prompt/create`
- `POST /prompt/update`
- `GET /productFeature/list`
- `POST /productFeature/create`
- `POST /productFeature/update`
- `GET /sellingPoint/list`
- `POST /sellingPoint/create`
- `POST /sellingPoint/update`
- `GET /strategy/list`
- `POST /strategy/create`
- `POST /strategy/update`
- `POST /script/generate`
- `GET /scriptAsset/my`
- `GET /scriptAsset/all`
- `GET /scriptAsset/detail`

### OpenClaw 内网接口

- `POST /internal/openclaw/script/resolve`
- `POST /internal/openclaw/script/generate`

### 接口语义变化

- 旧版 `tagGroup/tagItem` 相关接口已移除，不再作为业务主链路
- 旧版规则、优秀案例、生成历史、行为反馈相关接口已移除
- 新版生成请求固定使用：
  - `productId`
  - `featureId`
  - `coreSellingPointIds`
  - `strategyId`
- 新版生成结果直接落到 `script_asset`

### 对接要求

- 管理端按新主数据模型维护产品、提示词、功能、卖点、策略
- OpenClaw 只需对接 `resolve` 和 `generate`
- OpenClaw 最终回显后端返回的 `displayText`
- OpenClaw 内网接口必须带 `Authorization: Bearer <OPENCLAW_AUTH_TOKEN>`
