# Wukong 前端接口变更日志

## 文档说明
- 本文件只记录前端联动所需的接口与交互历史变更过程。
- 项目当前最新业务与接口语义以根目录 `PROJECT_CONTEXT.md` 为准。
- 当后端改动涉及请求路径、请求参数、响应结构或会影响前端交互分支的异常语义时，必须在本文件追加记录。

## 2026-02-27（标签模型）
- 新增接口：`GET /tag/item/coreSellingPoints`
- 用途：按产品功能查询对应“核心卖点和优势”候选项
- 前端改造点：
  - 生成页选择产品功能后调用该接口获取核心卖点
  - 不再由前端自行做归属过滤

## 2026-02-28（脚本生成历史留存）
- 生成、回炉、反馈回炉返回新增字段：
  - `batchId`
  - `historyId`
  - `itemSeq`
- 保存接口新增入参：
  - `historyId`
- 前端改造点：
  - 生成结果需保留 `historyId`
  - 保存时透传 `historyId`

## 2026-03-02（优秀脚本结构拆解与知识库同步）
- 新增接口：`POST /script/excellent/add`
- 新增请求参数：
  - `excellentScript`
  - `productName`
  - `functionName`
- 新增响应字段：
  - `structuredText`
  - `knowledgeSyncSuccess`
  - `workflowRunId`
  - `taskId`

## 2026-03-04（优秀脚本结构记录落库与分页查询）
- 变更接口：`POST /script/excellent/add`
- 新增接口：`GET /script/excellent/list`
- 前端影响：
  - `/script/excellent/add` 请求参数新增 `productId`（必填）
  - `/script/excellent/list` 支持按产品分页查询导入记录

## 2026-03-04（产品功能启用项查询）
- 新增接口：`GET /tag/item/features`
- 请求参数：
  - `productId`（必填）
- 响应结构：
  - `TagFeatureVO[]`
- 前端改造点：
  - 产品功能下拉改为直接调用该接口
  - 不再从全量标签中自行过滤产品功能

## 2026-03-05（优秀脚本导入改为异步）
- 变更接口：`POST /script/excellent/add`
- 变更接口：`GET /script/excellent/list`
- 对前端接口影响：
  - `/script/excellent/add` 响应改为异步受理结果：`recordId`、`syncStatus`
  - `/script/excellent/list` 响应新增 `syncStatus`、`syncErrorMsg`

## 2026-03-09（优秀案例前端同步异步导入改版）
- 停止使用：`GET /excellentCase/list`
- 新增使用：
  - `GET /script/excellent/list`
  - `POST /script/excellent/add`
  - `GET /tag/item/features`
- 页面联动点：
  - 产品工作台“生成”页删除优秀案例选择，不再向 `/script/generate` 传 `excellentCaseId`
  - “优秀案例”页改为卡片列表、新增弹窗和详情抽屉
  - 新增案例后自动轮询列表，直到 `syncStatus` 不再是 `SYNCING`
- 兼容性说明：
  - 前端优秀案例页面必须切换到新接口，否则无法展示异步导入记录

## 2026-03-18（下线 workflow / workflowScript 接口）
- 停止使用：`GET /workflow/queryWorkflows`
- 停止使用：
  - `GET /workflowScript/getList`
  - `GET /workflowScript/getListByUserId`
  - `GET /workflowScript/getInfo`
  - `POST /workflowScript/edit`
  - `POST /workflowScript/save`
  - `POST /workflowScript/delete`
- 前端影响：
  - 工作流目录、工作流脚本列表、详情、保存、编辑、删除相关页面或入口必须同步移除
  - 若前端继续调用上述接口，将直接收到 404

## 2026-03-23（新增通用应用使用埋点接口）
- 新增接口：`POST /appUsage/record`
- 新增接口：`GET /appUsage/stats`
- 请求参数：
  - `appCode`（必填，当前支持 `TTS`）
- 响应结构：
  - `/appUsage/record` 无响应数据
  - `/appUsage/stats` 返回 `appCode`、`currentUserCount`、`totalCount`
- 前端改造点：
  - TTS 页面每次用户点击使用动作时，调用 `/appUsage/record`
  - 如需展示使用统计，调用 `/appUsage/stats?appCode=TTS`
- 兼容性说明：
  - 后续接入新的应用统计时，前端只需传新枚举值，无需变更接口路径
