# RPA 指标管理平台

基于 RBAC 权限模型的 RPA（机器人流程自动化）运营管理平台，提供任务编排、机器人管理、数据采集/解析/加工全链路管理，以及指标定义与额度审核功能。

---

## 技术栈

### 后端

| 领域 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.2.0 |
| JDK | Java | 17 |
| 构建 | Maven（多模块） | — |
| 数据库 | MySQL | 8.0.33 |
| ORM | MyBatis-Plus | 3.5.5 |
| 连接池 | Druid | 1.2.20 |
| 缓存 | Redis（Lettuce）+ Redisson 3.25.2 + Caffeine 3.1.8 |
| 认证 | JWT（jjwt） | 0.12.3 |
| 加密 | BCrypt（at.favre.lib） | 0.10.2 |
| API 文档 | Knife4j + SpringDoc OpenAPI | 4.4.0 / 2.3.0 |
| 工具库 | Hutool 5.8.23 / Guava 32.1.3 / Commons Lang3 |
| JSON | FastJSON2 | 2.0.43 |
| 监控 | Spring Boot Actuator + Micrometer + Prometheus |
| 熔断降级 | Resilience4j | 2.1.0 |
| 爬虫引擎 | Playwright | 1.40.0 |
| 脚本引擎 | Groovy（JSR223） | 3.0.9 |
| AI 集成 | Coze API（扣子） | v3 |

### 前端

| 领域 | 技术 | 版本 |
|------|------|------|
| 框架 | UmiJS Max（React） | 4.6.33 |
| UI 组件 | Ant Design Pro Components | 2.6.43 |
| 基础 UI | Ant Design | 5.12.8 |
| 图标 | @ant-design/icons | 5.2.6 |
| 语言 | TypeScript | 5.3.3 |
| 包管理 | pnpm | 8.10.0 |
| 大数处理 | json-bigint | 1.0.0 |

---

## 功能模块

### RBAC 权限管理
- **用户管理**：用户 CRUD、个人信息维护、密码修改、头像上传、账号启用/禁用
- **角色管理**：角色 CRUD、角色绑定用户、角色分配资源权限
- **资源管理**：菜单/按钮/API 三级资源树管理，支持动态路由和按钮级权限控制

### RPA 运营管理
- **任务管理**：创建任务、绑定流程与机器人、执行任务、查看执行记录与步骤详情
- **机器人管理**：机器人实例 CRUD，支持 Playwright 驱动的网页自动化
- **流程管理**：流程模板 CRUD，支持 Groovy 脚本驱动的步骤编排

### 数据流水线
- **数据采集**：爬取目标网站数据，入库原始采集记录
- **数据解析**：解析结构化发票/税务数据
- **数据加工**：根据指标规则进行数据加工
- **数据查询**：最终业务数据检索

### 指标管理
- **指标定义**：指标 CRUD
- **审核规则**：额度规则配置、额度计算
- **指标审核**：基于 Coze AI 的指标智能审核

---

## 项目结构

```
rbac-system/
├── rbac-api/                  后端 API 模块（Spring Boot 入口 + 控制器层）
│   └── src/main/java/com/rbac/api/
│       ├── RbacSystemApplication.java   启动类
│       └── controller/                  控制器（16个）
├── rbac-core/                 后端核心模块（业务逻辑层）
│   └── src/main/java/com/rbac/core/
│       ├── domain/entity/              实体类（17个）
│       ├── domain/mapper/              MyBatis-Plus Mapper
│       ├── service/                    服务层接口与实现
│       └── spider/                     Playwright 爬虫引擎
├── rbac-common/               后端公共模块
│   └── src/main/java/com/rbac/common/
│       ├── annotation/                自定义注解（幂等、限流、脱敏等）
│       ├── config/                    Coze AI 配置
│       ├── enums/                     枚举定义
│       ├── exception/                 全局异常处理
│       ├── model/dto/                 DTO（26个）
│       ├── response/                  统一响应体
│       └── utils/                     工具类（JWT、AES、密码等）
├── src/                        前端源码
│   ├── pages/                         页面组件
│   │   ├── Home/                      首页
│   │   ├── Rpa/                       RPA 运营管理
│   │   ├── System/                    系统管理（用户/角色/资源）
│   │   ├── indicator/                 指标管理
│   │   └── login/                     登录页
│   └── services/                      API 请求服务
├── docs/sql/                   数据库脚本
│   └── rbac_system.sql                完整建表与初始数据
├── avatar/                     头像文件上传目录
├── pom.xml                     Maven 父 POM
└── package.json                前端项目配置
```

---

## 快速开始

### 环境要求

| 环境 | 版本要求 |
|------|----------|
| JDK | 17+ |
| Maven | 3.8+（项目内置 Maven Wrapper，无需手动安装） |
| MySQL | 8.0+ |
| Redis | 6.0+ |
| Node.js | 18+ |
| pnpm | 8+ |

### 1. 初始化数据库

执行 `docs/sql/rbac_system.sql` 脚本，创建数据库和初始数据：

```bash
mysql -u root -p < docs/sql/rbac_system.sql
```

数据库连接配置可在 [application.yml](rbac-api/src/main/resources/application.yml) 中修改：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rbac_system?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  data:
    redis:
      host: localhost
      port: 6379
```

### 2. 启动后端

在项目根目录执行：

```bash
# Windows
mvnw.cmd clean install -DskipTests
mvnw.cmd -pl rbac-api spring-boot:run

# Linux / macOS
./mvnw clean install -DskipTests
./mvnw -pl rbac-api spring-boot:run
```

后端默认运行在 `http://localhost:8080`。

启动成功后控制台会输出 Knife4j 文档地址：

```
Knife4j文档地址: http://localhost:8080/doc.html
```

### 3. 启动前端

```bash
# 安装依赖
pnpm install

# 启动开发服务器
pnpm dev
```

前端开发服务器默认运行在 `http://localhost:8000`。

前端代理配置在 [.umirc.ts](.umirc.ts) 中，默认将所有 `/api` 请求代理至 `http://10.159.42.150:8080`，如需修改请调整 `proxy` 配置项。

### 4. 访问系统

打开浏览器访问 `http://localhost:8000`，使用以下默认账号登录：

| 用户名 | 角色 | 说明 |
|--------|------|------|
| `admin` | 超级管理员 | 拥有全部权限 |

> 默认密码请联系管理员获取或直接在数据库中重置。密码使用 BCrypt 加密存储于 `sys_user.password_hash` 字段，可通过注册接口或直接操作数据库修改。

---

## API 文档

启动后端后，可通过以下地址访问 API 文档：

| 文档工具 | 地址 |
|----------|------|
| Knife4j（推荐） | http://localhost:8080/doc.html |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI 规范 | http://localhost:8080/v3/api-docs |

所有 API 统一前缀 `/api/v1/`，返回格式为统一的 `Result` 对象。

---

## 监控与运维

| 功能 | 地址 |
|------|------|
| Druid 监控面板 | http://localhost:8080/druid/ |
| Actuator 健康检查 | http://localhost:8080/actuator/health |
| Prometheus 指标 | http://localhost:8080/actuator/prometheus |

Druid 监控面板默认账号：`admin` / `admin123`

---

## 核心业务流程

```
创建任务 → 绑定流程模板 + 机器人
    ↓
执行任务 → 机器人按流程步骤执行（Groovy 脚本 + Playwright 网页自动化）
    ↓
数据采集（rpa_data_collection）→ 数据解析（rpa_data_parsing）
    ↓
数据加工（rpa_data_processing）→ 数据查询（rpa_data_query）
    ↓
指标审核（指标管理模块）→ 额度计算与 AI 审核
```

---

## 数据库表概览

| 模块 | 表名 | 说明 |
|------|------|------|
| RBAC | `sys_user` | 用户表 |
| RBAC | `sys_role` | 角色表 |
| RBAC | `sys_resource` | 资源表（菜单/按钮/API） |
| RBAC | `sys_role_resource` | 角色-资源关联表 |
| RBAC | `sys_user_role` | 用户-角色关联表 |
| RPA | `task` | 任务表 |
| RPA | `process` | 流程模板表 |
| RPA | `process_step` | 流程步骤明细表 |
| RPA | `robot` | 机器人管理表 |
| RPA | `execution_record` | 任务执行记录表 |
| RPA | `execution_step` | 执行步骤日志表 |
| 数据 | `rpa_data_collection` | 数据采集表 |
| 数据 | `rpa_data_parsing` | 数据解析表 |
| 数据 | `rpa_data_processing` | 数据加工表 |
| 数据 | `rpa_data_query` | 最终数据查询表 |
| 指标 | `indicator` | 指标定义表 |
| 指标 | `quota_rule` | 审核规则/额度规则表 |
