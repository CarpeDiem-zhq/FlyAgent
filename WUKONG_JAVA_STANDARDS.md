# Wukong Java 开发规范

## 1. 文档目的
- 本文件是 Wukong 项目内唯一长期维护的 Java/Spring Boot 开发规范文档。
- 适用于本仓库所有 Java 后端代码的新建与修改，包括 Controller、Service、Core、Repository、Mapper、Entity、Param、DTO、VO、配置类。
- 若规范存在歧义，优先遵循当前仓库中已经稳定存在的实现模式。

## 2. 技术栈基线
- Java 21
- Spring Boot 3.4.5
- Jakarta 命名空间（`jakarta.*`）
- MyBatis-Plus 3.5.12
- Knife4j / OpenAPI 3
- Sa-Token 1.44.0
- Redisson 3.51.0
- Hutool 5.8.39
- Druid 1.2.8
- EasyExcel 4.0.3

## 3. 命名与基础编码规范
- 类名使用 PascalCase，例如 `UserController`、`ScriptGenerationService`
- 方法名和变量名使用 camelCase，例如 `productId`、`getUserInfo`
- 常量使用 UPPER_SNAKE_CASE
- 包名全部小写
- 文档、源码、SQL、XML 必须使用 UTF-8（无 BOM）
- 中文内容必须为可读简体中文，禁止乱码，禁止使用 Unicode 转义代替中文

## 4. 分层与包结构规范
- Controller
  - 负责接收请求、调用 Service、返回 `ResultVO<T>`
  - 不承载业务逻辑，不在 Controller 中做复杂分支
- Service
  - 负责业务编排、校验、事务边界
  - 写操作遵循项目已有事务使用习惯
- Core
  - 负责领域行为封装与实体操作
  - 使用项目既有静态工厂与 repository 获取模式
- AI
  - 统一放置模型接入、场景路由、ChatClient 装配与 AI 配置
  - 业务 Service 只能依赖统一 AI 门面，不直接拼接底层 HTTP 请求
- Repository
  - 继承 MyBatis-Plus `IService`
- Mapper
  - 继承 MyBatis-Plus `BaseMapper`
- Entity
  - 作为数据库实体，使用 MyBatis-Plus 注解进行映射
- Model
  - `param` 用于请求入参
  - `dto` 用于内部传输
  - `vo` 用于响应出参

## 5. Lombok 与依赖注入规范
- Entity 使用 `@Getter`、`@Setter`、`@NoArgsConstructor`、`@Accessors(chain = true)`
- VO/DTO/Param 使用 `@Data`、`@AllArgsConstructor`、`@NoArgsConstructor`
- 实体类禁止使用 `@Data`
- 依赖注入优先使用 `@RequiredArgsConstructor` 配合 `final` 字段
- 禁止字段注入 `@Autowired`
- 日志统一使用 `@Slf4j`

## 6. Controller 与接口规范
- Controller 统一返回 `ResultVO<T>`
- 成功返回使用 `ResultVO.success(data)` 或 `ResultVO.success()`
- 失败统一通过业务异常交给全局异常处理，不在 Controller 中自行吞异常
- 查询接口优先使用 `GET`
- 新增、修改、删除接口使用 `POST`
- 请求参数统一封装在 Param 类中
- 禁止在 Controller 方法签名中直接暴露零散基础类型参数或滥用 `@RequestParam`
- Param 类命名使用 `[业务] + Param`

## 7. Swagger / OpenAPI 规范
- Controller 类与方法添加 OpenAPI 3 注解
- Controller 方法使用 `@Operation(summary = "...")`
- VO、DTO、Param、Entity 关键字段使用 `@Schema` 描述业务含义

## 8. 返回值与异常处理规范
- 业务异常统一使用 `BusinessException`
- 异常信息必须清晰、简短、可直接给业务或前端使用
- Controller 层不捕获业务异常
- 统一返回、统一异常优先于局部特殊处理

## 9. Entity 与 MyBatis-Plus 规范
- Entity 使用 `@TableName` 指定表名
- 主键使用 `@TableId`，自增主键优先 `IdType.AUTO`
- 数据库字段映射使用 `@TableField`
- 数据库关键字字段使用反引号映射
- Entity 按项目既有模式实现 `EntityGetter`、拷贝构造函数与 `cloneEntity()`
- 逻辑删除遵循项目全局配置：
  - `logic-delete-field=del`
  - `logic-delete-value=1`
  - `logic-not-delete-value=0`
- 数据库布尔字段避免 `is` 前缀
- Java 实体中的布尔值使用 `Boolean`

## 10. 查询与事务规范
- 查询必须优先使用 `LambdaQueryWrapper`、`LambdaUpdateWrapper`
- 禁止字符串拼接字段名
- 写操作遵循 Service 层事务边界
- 方法命名应与事务边界、业务语义一致

## 11. Core 领域模型规范
- Core 类使用 `@RequiredArgsConstructor` 注入 `final entity`
- 按项目既有模式提供静态 repository 字段与静态工厂方法
- 业务逻辑优先封装在 Core 实例方法中
- 查询条件使用 Lambda 构造器，避免字符串字段拼接

## 12. 时间、缓存与配置规范
- 时间统一使用 `LocalDateTime`
- 禁止使用 `Date` 与 `Timestamp`
- 时间格式化统一使用 `DateTimeUtil`
- Redis 仅在明确需要缓存时使用，并遵循现有 key 命名与 TTL 习惯
- `webapp/web` 模块环境配置统一维护在 `application*.yml`
- AI 模型接入优先使用 Spring AI 官方 `ChatModel`、`ChatClient` 与官方 starter，避免继续新增手写 HTTP 大模型客户端
- AI 配置分层保持明确：
  - `spring.ai.*` 用于模型接入与默认参数
  - 若当前只有单一模型，不额外维护冗余的自定义 provider 路由配置

## 13. 注释规范
- 类级别只保留必要的简短说明
- 公共 API 方法可增加必要说明
- 逻辑清晰时不补充低价值行内注释

## 14. DDL 变更规范
- 所有新增或变更 DDL 固定放在 `webapp/web/docs/`
- 禁止修改历史 DDL 内容，只允许：
  - 在已有 DDL 文件末尾追加
  - 或新增一个新的 DDL 文件
- 新增 DDL 必须保证从当前数据库状态顺序执行不报错
- 若实现需求必须修改历史 DDL，必须停止并显式提示风险

## 15. 文档同步规范
- 当后端改动影响业务逻辑、主流程、模块职责、技术方案、接口语义、校验规则、数据结构时：
  - 必须同步更新 `PROJECT_CONTEXT.md`
  - 必须追加 `webapp/web/docs/changelog/project-prd-overview.md`
- 当改动涉及以下任一内容时，还必须追加 `webapp/web/docs/changelog/frontend-api-change-log.md`
  - Controller 请求路径变化
  - Param 入参字段新增、删除、必填性变化或语义变化
  - VO 出参字段新增、删除或语义变化
  - 会影响前端交互分支的业务异常信息变化
