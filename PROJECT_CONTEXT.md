# FlyAgent 项目上下文

## 1. 文档说明
- 本文件记录 FlyAgent 项目当前最新业务状态，是项目业务逻辑、流程、模块职责与技术架构的统一上下文入口。
- 本文件只描述当前实现，不记录历史演进过程；当代码或方案变化时，应直接改写对应章节。
- 后端与前端联动的历史变更记录统一维护在：
  - `webapp/web/docs/changelog/project-prd-overview.md`
  - `webapp/web/docs/changelog/frontend-api-change-log.md`

## 2. 项目定位与业务目标
- 项目定位：FlyAgent 是一个围绕广告脚本生成的后端服务，核心职责是管理生成所需主数据，并基于结构化参数调用大模型生成脚本。
- 当前业务目标：
  - 让运营或配置人员维护产品、提示词、功能、核心卖点、策略
  - 让业务端或 OpenClaw 通过统一参数模型生成脚本
  - 让生成结果直接沉淀为脚本资产，便于后续查询和复用
- 当前项目只保留用户登录体系与新版脚本生成主链路，不再承载旧版标签、规则、优秀案例、生成历史、应用埋点等业务。

## 3. 当前核心业务对象
- 产品（`product`）
  - 脚本生成的顶层业务对象，维护产品名称、产品描述、启停状态。
- 系统提示词（`prompt_config`）
  - 每个产品仅维护一条当前系统提示词，用于生成时拼接系统提示。
- 产品功能（`product_feature`）
  - 产品下的功能维度，是卖点和策略的上游归属对象。
- 功能核心卖点（`feature_selling_point`）
  - 归属于某个产品功能，用于指定本次脚本重点强调的卖点。
- 生成策略（`strategy`）
  - 归属于单个产品功能，可同时绑定多个核心卖点。
  - 除了保存关系 ID，还保存功能名称和核心卖点名称快照。
- 脚本资产（`script_asset`）
  - 每次成功生成脚本后直接落库的结果实体，保存脚本正文以及提示词、功能、多卖点、策略快照。
- 用户（`user`）
  - 登录、注册、鉴权的基础主体；普通脚本生成接口依赖当前登录用户身份，OpenClaw 内网生成接口显式传入 `userId`。

## 4. 核心业务流程

### 4.1 后台主数据维护流程
1. 创建产品
2. 为产品创建系统提示词
3. 为产品创建功能
4. 为功能创建核心卖点
5. 为 `产品 + 功能 + 核心卖点集合` 创建策略
6. 按启停状态维护各层对象是否可继续参与生成

### 4.2 脚本生成主流程
1. 选择产品
2. 选择该产品下的功能
3. 选择该功能下的一个或多个核心卖点
4. 选择与当前卖点集合完整匹配的策略
5. 后端校验产品、功能、卖点、策略之间的归属关系
6. 后端读取该产品当前启用的系统提示词
7. 后端拼接系统提示和结构化用户内容，调用统一 LLM 服务生成脚本
8. 后端解析模型返回的 JSON 结果并执行基础质检
9. 后端将脚本结果和各类快照直接保存到 `script_asset`
10. 返回资产 ID、脚本标题、脚本内容、模型名和质检结果

### 4.3 OpenClaw 接入流程
1. OpenClaw 通过 `/internal/openclaw/script/resolve` 传入自然语言和当前草稿状态
2. 后端按 `product -> feature -> coreSellingPoint -> strategy` 顺序补齐参数
3. 当参数不完整时，返回 `ASK` 状态、当前步骤和候选选项
4. 当参数齐全时，返回 `READY` 状态
5. OpenClaw 调用 `/internal/openclaw/script/generate`
6. 后端复用统一脚本生成服务生成并落库
7. 后端将结果转换为适合对话回显的 `displayText`

## 5. 核心模块职责

### 5.1 用户与鉴权
- `user` 模块负责验证码、登录、注册、登出、登录信息查询。
- 普通业务接口依赖当前登录态获取用户信息。
- OpenClaw 内网接口走单独的 Bearer Token 鉴权，不依赖前台会话。

### 5.2 产品管理
- 提供产品列表、详情、新增、更新能力。
- 产品是提示词、功能、核心卖点、策略的归属根节点。
- 产品停用后，不应再作为可生成对象使用。

### 5.3 提示词管理
- 每个产品只允许存在一条未删除的系统提示词。
- 生成脚本时按产品读取当前启用的提示词。
- 若产品没有可用提示词，生成会直接失败。

### 5.4 产品功能管理
- 功能归属于产品。
- 功能列表按产品维度查询。
- 功能停用后，不应再进入生成链路。

### 5.5 核心卖点管理
- 核心卖点归属于 `product + feature`。
- 后端负责校验卖点是否属于指定功能和产品，前端与 OpenClaw 不承担最终关系校验。
- 卖点停用后，不应再进入生成链路。
- 当前管理端策略配置和公开脚本生成接口都支持多选核心卖点。

### 5.6 策略管理
- 策略归属于 `product + feature + coreSellingPoint[]`。
- 当前策略采用宽表模型，直接维护以下核心字段：
  - `strategy_name`
  - `feature_name`
  - `core_selling_point_names`
  - `target_audience`
  - `target_scene`
  - `tone_style`
  - `call_to_action`
  - `ad_words`
- 策略与多个核心卖点的真实关系由 `strategy_selling_point` 维护。
- 后端负责校验策略所属产品、功能、卖点关系，并按卖点集合完整匹配策略。

### 5.7 脚本生成
- 统一由 `ScriptGenerationService` 编排生成流程。
- 系统提示由产品提示词和固定 JSON 输出约束组成。
- 用户内容由产品、功能、卖点、策略字段组装成 JSON 结构后传给 LLM。
- 卖点在提示负载中按数组传递，要求模型同时覆盖全部已选卖点。
- 模型响应需按约定 JSON 协议返回，至少包含标题、结构化字段和脚本正文。
- 当前生成成功后直接落 `script_asset`，不再拆分生成批次、生成历史、反馈回炉、保存动作。

### 5.8 脚本质检
- 当前质检为基础规则质检，不做复杂审核流程。
- 主要检查项包括：
  - 生成结果是否为空
  - 标题是否为空
  - 正文是否为空
  - `hook/problem/solution/cta` 结构是否完整
  - 正文是否包含代码块
  - 在配置字数限制场景下正文是否明显过短
- 质检结果随生成响应一并返回，同时保留在 OpenClaw 回显文本中。

### 5.9 脚本资产
- 每次生成成功即新增一条脚本资产记录。
- 脚本资产保存：
  - 产品、功能、卖点、策略、用户、提示词关联 ID
  - 核心卖点 ID 列表
  - 系统提示词快照
  - 策略快照
  - 功能快照
  - 多卖点快照
  - 脚本标题、脚本正文、模型名称
- 当前提供“我的资产”“全部资产”“资产详情”查询能力。

### 5.10 OpenClaw 对话补参
- OpenClaw 只负责自然语言入口、多轮补参与结果回显。
- 后端在 `resolve` 阶段支持两类参数识别：
  - 产品名称模糊命中
  - 候选项 ID 或候选项标签命中
- 当用户输入不属于脚本生成意图，且当前草稿尚未选定产品时，返回 `UNSUPPORTED`。

## 6. 关键接口语义
- `GET /product/list`
  - 查询产品列表，支持按产品名模糊搜索。
- `GET /product/detail`
  - 查询单个产品详情，仅返回未删除且启用的产品。
- `POST /product/create`
  - 创建产品，默认启用。
- `POST /product/update`
  - 更新产品名称、描述、启停状态。
- `GET /prompt/list`
  - 查询产品下提示词列表，可按启用状态过滤。
- `GET /prompt/detail`
  - 查询提示词详情。
- `POST /prompt/create`
  - 为产品创建系统提示词；同一产品不允许重复创建未删除提示词。
- `POST /prompt/update`
  - 更新提示词内容与启停状态。
- `GET /productFeature/list`
  - 查询产品下功能列表。
- `POST /productFeature/create`
  - 创建产品功能。
- `POST /productFeature/update`
  - 更新产品功能与启停状态。
- `GET /sellingPoint/list`
  - 查询指定产品功能下的核心卖点列表。
- `POST /sellingPoint/create`
  - 创建核心卖点。
- `POST /sellingPoint/update`
  - 更新核心卖点与启停状态。
- `GET /strategy/list`
  - 查询指定产品下的策略列表，可按功能和核心卖点集合过滤。
  - 当传入核心卖点集合时，按完整匹配规则返回策略。
- `POST /strategy/create`
  - 创建策略，核心卖点入参为多选数组。
- `POST /strategy/update`
  - 更新策略与启停状态，核心卖点入参为多选数组。
- `POST /script/generate`
  - 按 `productId + featureId + coreSellingPointIds + strategyId` 生成脚本。
  - 生成成功后直接写入 `script_asset`。
- `GET /scriptAsset/my`
  - 分页查询当前登录用户自己的脚本资产。
- `GET /scriptAsset/all`
  - 分页查询全量脚本资产。
- `GET /scriptAsset/detail`
  - 查询单条脚本资产详情。
- `POST /internal/openclaw/script/resolve`
  - 根据用户自然语言和当前草稿补齐脚本生成参数。
  - 返回状态包括 `ASK`、`READY`、`UNSUPPORTED`。
- `POST /internal/openclaw/script/generate`
  - 供 OpenClaw 传入完整参数和 `userId` 后直接生成脚本。
- `GET /user/getImgCode`
  - 获取图形验证码。
- `POST /user/login`
  - 用户登录。
- `POST /user/register`
  - 用户注册。
- `POST /user/logout`
  - 用户登出。
- `GET /user/getLoginInfo`
  - 查询当前登录用户信息。

## 7. 数据与持久化要点
- 当前核心业务表：
  - `product`
  - `prompt_config`
  - `product_feature`
  - `feature_selling_point`
  - `strategy`
  - `strategy_selling_point`
  - `script_asset`
  - `user`
- 关键持久化约束：
  - `prompt_config` 通过唯一索引保证每个产品仅一条未删除提示词
  - `feature_selling_point` 通过 `product_id + feature_id` 表达归属关系
  - `strategy` 保存功能名称和卖点名称快照
  - `strategy_selling_point` 表达策略与多个核心卖点的真实关系
  - `script_asset` 通过 `core_selling_point_ids` 和多类快照保存生成时上下文
- DDL 管理原则：
  - DDL 文件放在 `webapp/web/docs/`
  - 历史 DDL 不回改，只追加新的 DDL 文件或在新文件中声明增量变更

## 8. 技术架构与实现约束
- 后端技术栈：
  - Spring Boot 3.4.5
  - Java 21
  - MyBatis-Plus 3.5.12
  - Sa-Token 1.44.0
  - Redisson 3.51.0
- 接口层约束：
  - Controller 统一返回 `ResultVO<T>`
  - 业务异常统一使用 `BusinessException`
- AI 调用架构：
  - 业务通过统一 `LlmService` 调用模型
  - AI 场景使用 `AiSceneType.SCRIPT_GENERATION`
  - 当前仅启用 Spring AI 官方 DeepSeek 模型接入
  - 模型默认配置由 `spring.ai.*` 管理
- 配置管理：
  - `webapp/web` 模块统一使用 `application*.yml`
  - Codex 协作配置使用根目录 `.codex/config.toml`
  - MySQL MCP 默认只读连接本地 `dev` 数据库 `127.0.0.1:3306/wukong_test`
- 编码要求：
  - 源码、SQL、文档统一 UTF-8（无 BOM）
  - 中文必须可读，禁止乱码

## 9. 当前风险与注意事项
- 当前脚本质量依赖模型按 JSON 协议稳定返回；若返回结构不合法，生成会失败。
- 当前质检只覆盖基础完整性与格式规则，不等同于人工审核。
- `ad_words` 当前更多是生成约束提示，不能严格保证最终字数完全命中。
- 生成结果创建即入库，若后续需要“草稿态”“人工确认后保存”语义，需要重新设计资产生命周期。
- OpenClaw 的产品和选项匹配基于名称包含或候选值匹配，存在命中歧义时会回退到人工选择。
- 当前 OpenClaw 仍按单卖点兼容模式工作，管理端与公开脚本生成接口已升级为多卖点。
- 数据结构或关系约束变化后，必须同步更新代码、DDL 和本文件。

## 10. 协作维护规则
- 当业务逻辑、核心流程、模块职责、技术方案、接口语义、校验规则、数据结构发生变化时，必须同步更新本文件对应章节。
- 更新原则是“改写为最新状态”，不是在本文件中追加历史记录。
- 当任务需要核对数据库表结构、数据分布或 SQL 实际结果时，优先通过 MySQL MCP 对本地 `dev` 数据库做只读核对，避免仅根据代码推断数据库状态。
- 历史变更过程统一追加到：
  - `webapp/web/docs/changelog/project-prd-overview.md`
  - `webapp/web/docs/changelog/frontend-api-change-log.md`
