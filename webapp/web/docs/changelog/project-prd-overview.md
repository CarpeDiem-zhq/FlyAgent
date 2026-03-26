# Wukong 后端变更日志

## 文档说明
- 本文件只记录后端与业务相关的历史变更过程，不承担项目当前状态说明。
- 项目当前最新业务与架构状态以根目录 `PROJECT_CONTEXT.md` 为准。
- 每次涉及业务逻辑、主流程、模块职责、技术方案、接口语义、校验规则、数据结构的改动，都应在本文件追加记录。

## 2026-02-27 变更同步（标签模型）
- 新增后端接口：`GET /tag/item/coreSellingPoints`
- 参数：`productId`、`featureItemId`
- 返回：指定产品功能下已启用的核心卖点和优势列表
- 后端校验规则：
  - 产品功能必须存在且启用
  - 核心卖点和优势必须归属指定产品功能
- 前端联动点：
  - 生成页选择产品功能后调用后端接口获取核心卖点
  - 不再由前端做归属过滤判断

## 2026-02-27 变更同步（脚本生成历史留存）
- 变更背景：
  - 为支持“生成 -> 保存 -> 使用行为”链路统计，每次生成都需要留存历史
- 后端改动点：
  - 新增生成批次与生成历史的数据模型，按“批次 + 单条”双粒度留存
  - `POST /script/generate` 每条生成结果落历史，并返回 `batchId`、`historyId`、`itemSeq`
  - `POST /script/rerun` 与反馈回炉的新结果也会写入生成历史
  - `POST /scriptAsset/save` 新增 `historyId` 入参，用于建立历史到资产映射
- 数据结构变更：
  - 新增表：`script_generate_batch`、`script_generate_history`
  - DDL 文件：`webapp/web/docs/2026-02-27-script-generate-history.sql`
- 风险与回归点：
  - 批量生成部分失败时，需要核对批次状态与失败信息是否落历史
  - 保存接口需要校验历史归属用户与产品一致，避免误关联

## 2026-03-02 变更同步（优秀脚本结构拆解链路）
- 新增接口：`POST /script/excellent/add`
- 新增服务：`ExcellentScriptStructService`
- 新增客户端：`DifyScriptStructClient`
- 工作流请求 `user` 使用当前登录用户手机号
- 新增参数：`excellentScript`、`productName`、`functionName`
- 新增响应：`structuredText`、`knowledgeSyncSuccess`、`workflowRunId`、`taskId`
- 配置新增：
  - `dify.knowledge.segment-path-template=/v1/datasets/%s/documents/%s/segments`
- 复用配置：
  - `dify.base-url`
  - `dify.qwen.workflow-path`
  - `dify.script-struct.api-key`
  - `dify.knowledge.*`

## 2026-03-04 变更同步（优秀脚本结构记录落库与分页查询）
- `POST /script/excellent/add` 入参新增 `productId` 并加强校验：
  - `productId` 必须存在且未删除
  - `productName` 必须与产品名称一致
  - `functionName` 必须是该产品有效产品功能
- 导入成功后新增落库到 `excellent_script_struct`
- 新增接口：`GET /script/excellent/list`
- `TagService` 新增按产品和功能名称校验有效产品功能的能力
- 数据结构变更：
  - 新增表：`excellent_script_struct`
  - DDL 文件：`webapp/web/docs/2026-03-04-excellent-script-struct.sql`
  - MySQL MCP 执行：已在 `wukong_test` 执行建表并校验字段成功

## 2026-03-04 变更同步（产品功能启用项查询接口）
- 变更背景：
  - 生成页和配置页需要稳定获取产品功能启用项，避免前端从全量标签中做业务过滤
- 后端改动点：
  - `TagService` 新增按产品查询启用产品功能的方法
  - `TagController` 新增接口：`GET /tag/item/features`
- 请求参数：
  - `productId`（必填）
- 响应结构：
  - `TagFeatureVO`，仅包含 `id` 与 `featureName`

## 2026-03-05 变更同步（优秀脚本导入异步化）
- `POST /script/excellent/add` 改为异步受理：
  - 先保存基础记录并置状态 `SYNCING`
  - 随后异步执行结构拆解和知识库同步
- 异步结果回写：
  - 成功：`SUCCESS`
  - 失败：`FAILED`
- `GET /script/excellent/list` 响应新增字段：
  - `syncStatus`
  - `syncErrorMsg`
- 数据结构变更：
  - 新增字段：`sync_status`、`sync_error_msg`
  - `structured_script` 改为可空
  - `segment_id` 改为可空
  - DDL 文件：`webapp/web/docs/2026-03-04-excellent-script-struct-async.sql`

## 2026-03-09 变更同步（优秀案例前端异步导入页面）
- 变更背景：
  - 后端优秀脚本导入已改为“先落库、后异步解析、后同步知识库”，前端需要同步新的展示与交互方式
- 前端改动点：
  - 脚本生成页移除“优秀案例参考”选项，不再传递 `excellentCaseId`
  - 产品工作台“优秀案例”页重构为卡片式导入记录列表
  - 新增案例成功后前端自动轮询列表，直到状态从 `SYNCING` 变为 `SUCCESS` 或 `FAILED`

## 2026-03-12 变更同步（脚本生成切换统一 LLM 层）
- 变更背景：
  - 原脚本生成链路依赖 Dify Workflow，现改为业务端直接调用统一 LLM 接入层
- 后端改动点：
  - `ScriptGenerationService` 去除 Dify Workflow 调用，改为调用统一 LLM service
  - 新增 provider 路由与枚举，后续可扩展 OpenAI、Qwen
  - 新增独立 `PromptBuilder`，拆分首次生成、回炉、反馈回炉三类提示构造逻辑
  - 模型响应统一从 `choices[0].message.content` 读取并按 JSON 协议解析
- 对外接口无路径、入参、出参变化
- 风险与回归点：
  - 模型若未按约定返回合法 JSON，会导致本次生成失败
  - 生产环境切换前需确保 `llm.*` 配置完整

## 2026-03-12 变更同步（脚本字数限制加入系统提示词）
- `ScriptGenerationPromptBuilder` 将 `controlParams.ad_words` 同步写入系统提示词
- 首次生成、回炉、反馈回炉三类提示构造统一生效
- 对外接口无变化
- 风险与回归点：
  - 若 `ad_words` 描述模糊，模型仍可能出现长度边界偏差

## 2026-03-12 变更同步（批量脚本开头策略去同质化）
- 新增 `ScriptGenerationStrategyProperties` 配置类，支持通过配置文件维护开头策略池
- 新增 `OpeningStrategyAssignmentService`，按 `adNumber` 为每条任务预分配 `opening strategy`
- `ScriptGenerationService` 在批量并发提交前完成策略分配
- `ScriptGenerationPromptBuilder` 在系统提示词与用户负载中注入 `opening_strategy`
- 对外接口无变化
- 风险与回归点：
  - 若开头策略池为空或配置不完整，脚本生成会直接失败

## 2026-03-13 变更同步（配置文件统一迁移为 YAML）
- `webapp/web` 模块的 `application*.properties` 全部迁移为对应 `application*.yml`
- 保留原有 profile 结构、配置键语义和 Java 配置绑定方式不变
- `opening strategies` 改为在 `application.yml` 中以 YAML list 维护，规避中文策略文本乱码
- 对外接口无变化
- 风险与回归点：
  - YAML 对缩进和特殊字符更敏感，部署环境需要同步适配

## 2026-03-18 变更同步（Spring AI Alibaba 接入与模型路由升级）
- 变更背景：
  - 为统一模型接入抽象、降低手写 HTTP 客户端维护成本，并为后续接入 Qwen 能力做准备，脚本生成链路切换为 Spring AI 架构。
- 后端改动点：
  - Spring Boot 基线升级到 `3.4.5`
  - 引入 Spring AI OpenAI starter 与 Spring AI Alibaba DashScope starter
  - 脚本生成链路由手写 `DeepSeekLlmProviderClient` 改为 `ChatClient + 场景路由`
  - 新增 `AiChatGateway`、`AiSceneType`、`AiProperties`、`AiChatClientConfig`
  - DeepSeek 通过 OpenAI 兼容模型方式接入，Qwen 通过 DashScope 接入
- 接口与参数变更：
  - 对外 Controller 路径、请求参数、响应结构无变更
- 配置变更：
  - 废弃 `llm.*`
  - 新增 `app.ai.*`
  - 新增 `spring.ai.*`
  - 当前默认排除 `DashScopeChatAutoConfiguration` 与 `DashScopeAgentAutoConfiguration`，未配置 DashScope API Key 时允许 DeepSeek 单独启动
- 风险与回归点：
  - 首次构建需要确保 Spring AI 相关依赖仓库可访问
  - DashScope API Key 未配置时，Qwen 路由不可用；如需启用 Qwen，需恢复 DashScope Chat 自动配置
  - 多模型场景下若路由配置错误，会导致脚本生成调用错误模型

## 2026-03-18 变更同步（清理 Spring AI Alibaba，收敛为官方 DeepSeek 接入）
- 变更背景：
  - 当前项目仅使用 DeepSeek，暂不需要 Spring AI Alibaba 的 DashScope/Qwen 能力，因此将接入层收敛为纯 Spring AI 官方 DeepSeek 实现。
- 后端改动点：
  - 删除 Spring AI Alibaba BOM 与 DashScope starter
  - 删除 AI 接入层中的 DashScope、Qwen 与 `app.ai.*` provider 路由配置
  - DeepSeek 从 OpenAI 兼容桥接改为 Spring AI 官方 DeepSeek starter
  - 保留 `AiChatGateway` 与 `AiSceneType` 作为未来多模型扩展入口
- 接口与参数变更：
  - 对外 Controller 路径、请求参数、响应结构无变更
- 配置变更：
  - 删除 `spring.ai.dashscope.*`
  - 删除 `app.ai.*`
  - DeepSeek 统一改为 `spring.ai.deepseek.*`
  - 增加 `spring.ai.model.chat=deepseek`，显式指定当前活跃 chat model
- 风险与回归点：
  - 启动环境需要提供 `spring.ai.deepseek.api-key`
  - 后续新增其他模型时，需要在统一 AI 门面层重新扩展 provider 实现，而不是恢复旧的 DashScope 残留配置

## 2026-03-18 变更同步（增加本地 MySQL MCP 查库能力）
- 变更背景：
  - 为减少数据库状态判断误差，并让 Codex 在协作过程中可以直接核对本地 `dev` 库的表结构与数据，新增项目级 MySQL MCP 配置。
- 协作配置变更：
  - 根目录 `.codex/config.toml` 新增 `mysql` MCP Server
  - 默认连接 `127.0.0.1:3306/wukong_test`
  - 连接账号与 `application-dev.yml` 对齐
- 协作规则变更：
  - 需要核对数据库表结构、数据或 SQL 实际结果时，优先使用 MySQL MCP 做只读查询
  - 不通过 MCP 执行写库、删库或 DDL
- 对外接口无变化

## 2026-03-18 变更同步（删除 workflow / workflow_script 业务）
- 变更背景：
  - `workflow` 与 `workflow_script` 业务已经废弃，不再需要保留工作流目录、工作流脚本管理及对应持久化实现。
- 后端改动点：
  - 删除 `WorkflowController`、`WorkflowScriptController`
  - 删除 `WorkflowService`、`WorkflowScriptService`
  - 删除对应 Param、VO、Core、Entity、Repository、Mapper、Mapper XML
  - 删除已无引用的 `DifyWorkflowClient` 与 `DifyWorkflowParam`
  - 登录白名单移除 `/workflow/queryWorkflows`
- 接口与参数变更：
  - 删除接口：`GET /workflow/queryWorkflows`
  - 删除接口：`GET /workflowScript/getList`
  - 删除接口：`GET /workflowScript/getListByUserId`
  - 删除接口：`GET /workflowScript/getInfo`
  - 删除接口：`POST /workflowScript/edit`
  - 删除接口：`POST /workflowScript/save`
  - 删除接口：`POST /workflowScript/delete`
- 数据结构变更：
  - 新增 DDL 文件：`webapp/web/docs/2026-03-18-drop-workflow-tables.sql`
  - 废弃表：`workflow_script`、`workflow`
- 风险与回归点：
  - 前端若仍调用这组接口，将直接收到 404
  - `dify.qwen.workflow-path` 仍被优秀脚本结构化链路复用，本次不删除该配置

## 2026-03-23 变更同步（通用应用使用埋点）
- 变更背景：
  - 需要为业务应用能力提供通用埋点统计入口，先用于统计 `TTS` 的用户使用次数，后续通过扩展枚举值支持更多应用类型。
- 后端改动点：
  - 新增 `AppUsageController`、`AppUsageService`
  - 新增接口：`POST /appUsage/record`
  - 新增接口：`GET /appUsage/stats`
  - 新增通用应用类型枚举 `AppUsageBizTypeEnum`，当前内置 `TTS`
- 接口语义：
  - `POST /appUsage/record` 每次点击写入一条埋点日志，按当前登录用户和 `appCode` 记录
  - `GET /appUsage/stats` 返回当前登录用户在指定应用下的累计次数，以及该应用的全局累计次数
- 数据结构变更：
  - 新增表：`app_usage_log`
  - DDL 文件：`webapp/web/docs/2026-03-23-app-usage-log.sql`
- 风险与回归点：
  - 当前统计口径为全量累计，不做去重
  - 后续新增应用类型时，必须同步扩展后端枚举值
