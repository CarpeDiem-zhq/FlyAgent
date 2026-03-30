# Wukong Project Agents Guide

## 1. 阅读顺序（进入仓库后必须执行）
1. 先阅读 `AGENTS.md`
2. 再阅读 `PROJECT_CONTEXT.md`
3. 若任务涉及 Java 后端代码、新增接口、DDL、配置或架构约束，再阅读 `WUKONG_JAVA_STANDARDS.md`

## 2. 文档职责边界
- `PROJECT_CONTEXT.md`
  - 记录项目当前最新状态
  - 包含业务描述、核心流程、模块职责、技术架构、关键接口语义、关键数据结构
  - 不记录历史变更过程
- `WUKONG_JAVA_STANDARDS.md`
  - 记录 Java/Spring Boot 开发规范与实现约束
  - 只解决“代码应该怎么写”，不记录业务现状
- `webapp/web/docs/changelog/project-prd-overview.md`
  - 记录后端与业务相关历史变更
- `webapp/web/docs/changelog/frontend-api-change-log.md`
  - 记录前端联动与接口相关历史变更

## 3. 默认执行规则
- 修改前先判断本次任务是否影响以下任一项：
  - 业务逻辑
  - 核心流程
  - 模块职责
  - 技术方案
  - Controller 路径或接口语义
  - Param/DTO/VO 字段
  - 校验规则
  - 数据结构或 DDL
  - 会影响前端分支的异常语义
- 若影响上述任一项，必须同步更新 `PROJECT_CONTEXT.md` 的对应章节，使其反映最新状态
- 若本次改动涉及 Java 后端代码，必须遵守 `WUKONG_JAVA_STANDARDS.md`

## 4. 文档同步规则
- 后端业务逻辑、规则、流程、模块职责、架构方案、关键接口语义变化：
  - 必须更新 `PROJECT_CONTEXT.md`
  - 必须追加 `webapp/web/docs/changelog/project-prd-overview.md`
- 前端联动接口变化：
  - 在满足上条前提下，还必须追加 `webapp/web/docs/changelog/frontend-api-change-log.md`
- 仅技术规范变化：
  - 更新 `WUKONG_JAVA_STANDARDS.md`
  - 若影响项目当前实现现状，再同步更新 `PROJECT_CONTEXT.md`
- 纯重构、纯注释、纯格式调整且不改变行为：
  - 可不更新 `PROJECT_CONTEXT.md`
  - 可不追加 changelog

## 5. changelog 追加标准
- 后端 changelog 适用场景：
  - 业务规则变化
  - 主流程变化
  - 模块职责变化
  - 接口行为变化
  - 校验规则变化
  - 数据结构变化
  - 技术方案变化
- 前端 changelog 适用场景：
  - Controller 路径变化
  - 请求参数新增、删除、必填性变化、语义变化
  - 响应字段新增、删除、语义变化
  - 会影响前端交互分支的异常信息变化

## 6. MCP 查库规则
- 根目录 `.codex/config.toml` 维护 `mysql` MCP Server，默认连接 `application-dev.yml` 对应的本地 `wukong_test`
- 当任务需要核对表结构、验证数据、确认数据库实际状态时，优先使用 MySQL MCP 做只读查询
- 不通过 MySQL MCP 执行写库、删库或 DDL；数据库结构变更仍以仓库中的 SQL/DDL 文件和明确的执行步骤为准
- 若本地数据库账号、密码、库名与 `dev` 配置不一致，应先同步更新 `.codex/config.toml` 的 MySQL MCP 连接参数

## 7. 前后端协作路径
- 与当前后端项目对应的前端项目路径为：`/mnt/d/Agent/FlyAgent-web`
- Windows 路径对应为：`D:\Agent\FlyAgent-web`
- 当任务涉及前端联调、接口对接、页面行为核对或前端文档同步时，应优先参考该前端项目的当前实现

## 8. OpenClaw 接入约束
- OpenClaw 与后端交互的逻辑必须单独收口在专用接入层，不得直接散落到后端核心业务逻辑中
- OpenClaw 相关的参数解析、会话补参、渠道适配、回显文案、内网接口编排，应放在独立的 OpenClaw facade / controller / DTO 中维护
- 后端核心业务服务只负责产品、提示词、功能、核心卖点、策略、脚本生成、资产等通用业务能力，不直接依赖 OpenClaw 渠道语义
- 若 OpenClaw 接口字段、补参流程或回显策略发生变化，应优先修改 OpenClaw 专用适配层，不把渠道判断写入通用 service
- 新增其它渠道接入时，遵循同样规则：渠道交互层与核心业务层解耦，避免渠道逻辑污染通用业务模型

## 9. 输出约束
- 文档与源码统一使用 UTF-8（无 BOM）
- 中文必须为可读简体中文，禁止乱码
- 不新增零散说明文档；优先维护这 4 个根目录入口文件和 `webapp/web/docs/changelog/` 下两份 changelog
