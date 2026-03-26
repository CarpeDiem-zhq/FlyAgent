# Wukong 项目上下文

## 1. 文档说明
- 本文件用于记录项目当前最新状态，是项目业务、流程、模块职责与技术架构的统一上下文入口。
- 本文件不记录历史变更过程；当代码或方案发生变化时，应直接更新对应章节，使内容始终与当前实现一致。

## 2. 产品目标与业务定位
- 项目目标：为不同产品提供可配置、可持续迭代的广告脚本生成能力。
- 业务方式：业务系统负责流程编排、规则约束与结构化参数组织，统一调用 LLM 接入层生成脚本。
- 核心价值：通过标签、规则、提示词、优秀脚本与知识沉淀能力，持续提升脚本生成效果并降低人工模板维护成本。

## 3. 关键业务概念
- 类目（Category）
  - 产品的归属分类，用于组织产品集合。
- 产品（Product）
  - 脚本生成的业务对象，例如某个 App 或业务线。
- 标签组（Tag Group）
  - 脚本生成结构化输入字段对应的键。
- 标签项（Tag Item）
  - 标签组的可选值，支持单选、多选或输入型结构。
- 规则（Rule）
  - 包括全局规则和产品规则，用于约束模型输出。
- 提示词（Prompt Config）
  - 当前以 `system_prompt` 为核心配置项。
- 脚本资产（Script Asset）
  - 用户确认后保存的脚本结果，支持反馈与回炉。
- 生成历史（Generate History）
  - 每次生成、回炉、反馈回炉的过程留痕，支持“生成 -> 保存”链路追踪。
- 优秀脚本结构记录
  - 优秀脚本导入、结构拆解、知识库同步后的沉淀记录。

## 4. 核心业务流程
1. 创建类目
2. 创建产品，并自动初始化固定标签组
3. 维护标签组、标签项、规则与提示词
4. 在生成页选择产品功能、核心卖点与其他控制参数
5. 后端组合结构化输入、规则、提示词与策略信息，调用统一 LLM 层生成脚本
6. 用户可对未保存结果执行回炉，对已保存资产执行反馈回炉
7. 用户确认后保存为脚本资产，并通过 `historyId` 建立生成历史与资产映射
8. 优秀脚本可异步导入、结构拆解并同步到知识库
9. 用户在业务页面触发应用功能时，可按应用类型记录使用埋点并查询个人与全局累计次数

## 5. 核心模块职责

### 5.1 类目与产品
- 提供类目新增、列表能力
- 提供产品新增、列表、详情能力
- 产品新增后自动初始化固定标签组

### 5.2 标签系统
- 固定标签组包括：
  - `product_service_name`
  - `product_feature`
  - `target_audience`
  - `core_selling_points`
  - `ad_tone_style`
  - `call_to_action`
  - `ad_words`
  - `key_scenes`
- `product_feature` 为单选
- `core_selling_points` 必须归属于已选 `product_feature`
- 后端负责功能项与核心卖点归属校验，前端不承担该规则判断

### 5.3 规则系统
- 当前保留全局规则与产品规则
- 已移除规则补丁（Rule Patch / Correction）模型与链路

### 5.4 提示词系统
- 当前仅保留 `system_prompt`
- 提示词支持启用与禁用
- 生成时使用规则、提示词、结构化输入和策略信息构造提示上下文

### 5.5 脚本生成
- 由业务系统组织结构化数据并调用统一 LLM 接入层
- 当前脚本生成链路已从 Dify Workflow 切换为基于 Spring AI 的统一 LLM 接入层
- 当前接入层仅使用 Spring AI 官方 DeepSeek 模型接入
- DeepSeek 通过 Spring AI 官方 starter 自动装配并由统一网关调用
- 脚本生成当前固定走 DeepSeek；后续若新增其他模型，在统一 AI 门面层扩展
- 批量生成支持 `adNumber` 1 到 10，并通过线程池并发执行
- 批量请求发起前会预分配不同 `opening strategy`，降低同批次开头同质化
- `controlParams.ad_words` 同时作为结构化控制参数和系统提示词中的字数约束
- 模型输出按 JSON 协议解析，脚本文本来自返回结构中的 `script` 字段

### 5.6 AI 接入层
- `cn.yeezi.ai` 负责统一管理 AI 场景、ChatClient 装配与模型配置
- 当前通过 `AiChatGateway` 对业务暴露统一调用入口
- 当前只保留 DeepSeek 实现，不再维护未启用的 provider 分支
- DeepSeek 默认参数统一由 `spring.ai.deepseek.chat.options.*` 管理

### 5.7 回炉与反馈
- 回炉
  - 面向未保存脚本，基于当前脚本内容与修改意见重新生成
- 反馈回炉
  - 面向已保存资产，基于资产脚本与反馈意见重新生成
- 两类链路都会记录新的生成历史

### 5.8 脚本资产与生成历史
- 生成、回炉、反馈回炉都会落历史
- 保存资产时可传 `historyId`，建立历史到资产的关联
- 同一 `historyId` 不允许重复保存
- 后端负责校验历史记录归属用户与产品一致性

### 5.9 优秀脚本结构化与知识库同步
- 优秀脚本导入接口先落基础记录，再异步执行结构拆解与知识库同步
- 状态流转为：
  - `SYNCING`
  - `SUCCESS`
  - `FAILED`
- 成功后回写 `structuredScript`、`segmentId`
- 失败后回写 `syncErrorMsg`

### 5.10 应用使用埋点
- 提供通用应用使用埋点能力，按 `appCode` 区分应用类型
- 当前已支持 `TTS`
- 前端只需传入应用类型编码，后端使用当前登录用户身份落库
- 每次点击写入一条埋点日志，不做去重
- 支持查询当前登录用户在某应用下的累计次数，以及该应用的全局累计次数

## 6. 关键接口语义
- `POST /script/generate`
  - 生成广告脚本
  - 返回每条结果对应的 `batchId`、`historyId`、`itemSeq`
- `POST /script/rerun`
  - 对未保存脚本执行回炉生成
- `POST /scriptBehavior/feedback`
  - 对已保存资产执行反馈回炉
- `POST /scriptAsset/save`
  - 保存脚本资产
  - 建议透传 `historyId`，由后端建立历史映射并校验重复保存
- `GET /tag/item/coreSellingPoints`
  - 按产品功能查询可选核心卖点
- `GET /tag/item/features`
  - 按产品查询已启用的产品功能列表
- `POST /script/excellent/add`
  - 异步导入优秀脚本结构记录
  - 响应 `recordId`、`syncStatus`
- `GET /script/excellent/list`
  - 按产品分页查询优秀脚本结构记录及同步状态
- `POST /appUsage/record`
  - 记录当前登录用户某应用类型的一次使用
  - 当前前端只需传 `appCode`，例如 `TTS`
- `GET /appUsage/stats`
  - 查询当前登录用户某应用类型的累计使用次数
  - 同时返回该应用类型的全局累计使用次数

## 7. 数据与持久化要点
- `tag_item`
  - 已移除 `tag_code`
  - 使用 `feature_item_id` 表达核心卖点与产品功能的归属关系
- `prompt_config`
  - 以 `system_prompt` 为主
- `script_generate_batch` 与 `script_generate_history`
  - 用于记录生成批次与单条生成历史
- `excellent_script_struct`
  - 用于记录优秀脚本导入、结构拆解与知识库同步结果
- `app_usage_log`
  - 用于记录用户按应用类型产生的使用埋点明细
- `excellent_case`
  - 已删除，优秀案例不再使用独立表
- `rule_patch`
  - 已删除，相关结构与逻辑不再使用
- DDL 管理原则
  - DDL 文件放在 `webapp/web/docs/`
  - 历史 DDL 不回改，只追加

## 8. 技术架构与实现约束
- 后端技术栈
  - Spring Boot 3.4.5
  - Java 21
  - MyBatis-Plus 3.5.12
  - Sa-Token 1.44.0
  - Redisson 3.51.0
- 接口层约束
  - Controller 统一返回 `ResultVO<T>`
  - 业务异常统一使用 `BusinessException`
- 配置管理
  - `webapp/web` 模块统一使用 `application*.yml`
  - AI 模型配置统一使用 `spring.ai.*`
  - 当前通过 `spring.ai.model.chat=deepseek` 固定活跃 chat model，避免多模型自动装配歧义
  - Codex 项目协作配置统一使用根目录 `.codex/config.toml`
  - 当前已配置 `mysql` MCP Server，默认只读连接本地 `dev` 数据库 `127.0.0.1:3306/wukong_test`
- LLM 架构
  - 业务层通过 Spring AI ChatClient + 统一 AI 门面调用模型
  - 当前仅启用 Spring AI 官方 DeepSeek starter
  - DeepSeek 模型实例由 Spring AI 自动装配，项目内只维护统一调用门面与默认参数读取
  - 当前 provider 仅包括 DeepSeek
- 并发与线程上下文
  - 批量生成与优秀脚本异步导入会使用线程池
  - 子线程需要显式传递必要会话信息，避免上下文丢失
- 编码要求
  - 源码、SQL、文档统一 UTF-8（无 BOM）
  - 中文必须可读

## 9. 当前风险与注意事项
- `ad_words` 仅增强提示词约束，模型仍可能出现字数边界偏差
- 批量生成的开头策略分流只能降低同质化，无法完全消除
- 优秀脚本异步导入与脚本生成共用线程池时，需要关注任务积压
- 数据结构变更后必须同步代码、DDL 与数据库实际结构
- 前端不承担核心业务规则判断，后端接口应保持规则闭环

## 10. 协作维护规则
- 当业务逻辑、核心流程、模块职责、技术方案、接口语义、校验规则、数据结构发生变化时，必须同步更新本文件对应章节
- 更新原则是“改写为最新状态”，而不是追加历史描述
- 当任务需要核对数据库表结构、数据分布或 SQL 实际结果时，优先通过 MySQL MCP 对本地 `dev` 数据库做只读核对，避免仅根据代码推断数据库状态
- 历史变化过程统一记录到：
  - `webapp/web/docs/changelog/project-prd-overview.md`
  - `webapp/web/docs/changelog/frontend-api-change-log.md`
