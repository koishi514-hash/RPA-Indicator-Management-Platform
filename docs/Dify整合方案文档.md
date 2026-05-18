# Dify 整合方案文档

## 一、项目概述

### 1.1 整合目标

本文档描述如何在 RPA 运营平台中整合 Dify AI 能力，实现智能额度审核功能。通过 Dify 的 LLM（大型语言模型）能力，实现复杂的业务规则判断和额度计算。

### 1.2 整合范围

- 指标计算模块：定义和管理业务指标
- 指标审核模块：配置审核规则，调用 Dify 执行额度计算

### 1.3 技术栈

- 后端框架：Spring Boot 3.2.x
- 数据库：MySQL 8.0+
- ORM：MyBatis Plus 3.5.x
- HTTP 客户端：RestTemplate
- Dify 版本：v1.x（云端或私有化部署）

---

## 二、整合架构

### 2.1 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端应用                                  │
│  ┌──────────────┐    ┌──────────────┐                          │
│  │  指标计算页面  │    │  指标审核页面  │                          │
│  └──────┬───────┘    └──────┬───────┘                          │
│         │                   │                                  │
└─────────┼───────────────────┼──────────────────────────────────┘
          │                   │
          ▼                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                       API 网关                                  │
│  ┌────────────────────┐  ┌────────────────────┐               │
│  │ IndicatorController │  │ QuotaRuleController │               │
│  └─────────┬──────────┘  └─────────┬──────────┘               │
│            │                        │                           │
└────────────┼────────────────────────┼───────────────────────────┘
             │                        │
             ▼                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Service 层                                  │
│  ┌────────────────────┐  ┌────────────────────┐               │
│  │ IndicatorService    │  │ QuotaRuleService   │               │
│  └─────────┬──────────┘  └─────────┬──────────┘               │
│            │                        │                           │
│            │                 ┌──────┴───────┐                   │
│            │                 │DifyClientService│                 │
│            │                 └──────┬───────┘                   │
└────────────┼────────────────────────┼───────────────────────────┘
             │                        │
             ▼                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                       数据层                                      │
│  ┌────────────────┐    ┌────────────────┐                       │
│  │   indicator     │    │   quota_rule   │                       │
│  │   (指标表)      │    │  (审核规则表)   │                       │
│  └────────────────┘    └────────────────┘                       │
└─────────────────────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Dify 云服务                                 │
│  ┌────────────────────────────────────────────────┐             │
│  │                 Dify API                        │             │
│  │  - Chat Messages API (聊天接口)                 │             │
│  │  - Workflow Run API (工作流接口)               │             │
│  └────────────────────────────────────────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 数据流程

```
1. 定义指标
   前端 → 创建指标 → indicator 表（关联 task_id）

2. 配置审核规则
   前端 → 选择指标编码 → 输入判断条件/计算公式 → quota_rule 表

3. 执行额度计算
   前端 → 传入业务数据 → 后端 → 构建 Dify 请求 → Dify API
   → Dify 执行计算 → 返回结果 → 后端解析 → 前端展示
```

---

## 三、数据库设计

### 3.1 指标表 (indicator)

```sql
CREATE TABLE `indicator` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `indicator_name` VARCHAR(100) NOT NULL COMMENT '指标名称',
    `indicator_code` VARCHAR(50) NOT NULL COMMENT '指标编码（唯一标识）',
    `indicator_logic` VARCHAR(500) NOT NULL COMMENT '指标逻辑描述',
    `task_id` BIGINT NULL COMMENT '关联的任务ID（数据来源）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_indicator_code` (`indicator_code`),
    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标表';
```

**字段说明：**

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| indicator_name | VARCHAR(100) | NOT NULL | 指标名称 |
| indicator_code | VARCHAR(50) | NOT NULL, UNIQUE | 指标编码（唯一） |
| indicator_logic | VARCHAR(500) | NOT NULL | 指标逻辑描述 |
| task_id | BIGINT | NULL | 关联的任务ID（数据来源） |
| create_time | DATETIME | NOT NULL | 创建时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

### 3.2 审核规则表 (quota_rule)

```sql
CREATE TABLE `quota_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `quota_name` VARCHAR(100) NOT NULL COMMENT '额度名称',
    `indicator_codes` VARCHAR(500) NOT NULL COMMENT '指标编码列表（逗号分隔）',
    `conditions` VARCHAR(1000) NOT NULL COMMENT '逻辑判断条件',
    `quota_calculation` VARCHAR(1000) NOT NULL COMMENT '额度计算公式',
    `result_var_name` VARCHAR(50) NOT NULL COMMENT '结果变量名称',
    `output_template` VARCHAR(1000) NOT NULL COMMENT '输出数据模板（JSON）',
    `dify_app_id` VARCHAR(100) NULL COMMENT 'Dify应用ID',
    `dify_api_key` VARCHAR(200) NULL COMMENT 'Dify API密钥',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核规则表';
```

**字段说明：**

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| quota_name | VARCHAR(100) | NOT NULL | 额度名称 |
| indicator_codes | VARCHAR(500) | NOT NULL | 指标编码列表（逗号分隔） |
| conditions | VARCHAR(1000) | NOT NULL | 逻辑判断条件 |
| quota_calculation | VARCHAR(1000) | NOT NULL | 额度计算公式 |
| result_var_name | VARCHAR(50) | NOT NULL | 结果变量名称 |
| output_template | VARCHAR(1000) | NOT NULL | 输出数据模板（JSON） |
| dify_app_id | VARCHAR(100) | NULL | Dify应用ID |
| dify_api_key | VARCHAR(200) | NULL | Dify API密钥 |
| create_time | DATETIME | NOT NULL | 创建时间 |
| update_time | DATETIME | NOT NULL | 更新时间 |

---

## 四、配置说明

### 4.1 应用配置 (application.yml)

```yaml
# Dify 配置
dify:
  base-url: https://api.dify.ai/v1        # Dify API 基础地址
  api-key: ${DIFY_API_KEY:your-api-key}   # 全局 API Key
  connection-timeout: 30000                 # 连接超时时间（毫秒）
  read-timeout: 60000                      # 读取超时时间（毫秒）
```

**配置说明：**

- `base-url`：Dify 服务的 API 地址，云端为 `https://api.dify.ai/v1`，私有化部署请填写实际地址
- `api-key`：Dify 平台的 API Key，可在 Dify 控制台获取
- `connection-timeout`：HTTP 连接超时时间
- `read-timeout`：HTTP 读取超时时间

### 4.2 环境变量配置

建议将敏感的 API Key 配置在环境变量中：

```bash
# Linux/Mac
export DIFY_API_KEY=your-actual-api-key

# Windows
set DIFY_API_KEY=your-actual-api-key
```

---

## 五、API 接口设计

### 5.1 指标管理接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 指标列表 | GET | `/api/v1/indicators/list` | 分页查询指标 |
| 新增指标 | POST | `/api/v1/indicators/create` | 创建指标 |
| 更新指标 | POST | `/api/v1/indicators/update` | 更新指标 |
| 指标详情 | GET | `/api/v1/indicators/detail/{id}` | 查询指标详情 |
| 删除指标 | DELETE | `/api/v1/indicators/delete/{id}` | 删除指标 |
| 所有指标 | GET | `/api/v1/indicators/all` | 获取所有指标（供审核规则使用） |

### 5.2 审核规则接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 规则列表 | GET | `/api/v1/quota-rules/list` | 分页查询审核规则 |
| 新增规则 | POST | `/api/v1/quota-rules/create` | 创建审核规则 |
| 更新规则 | POST | `/api/v1/quota-rules/update` | 更新审核规则 |
| 规则详情 | GET | `/api/v1/quota-rules/detail/{id}` | 查询审核规则详情 |
| 删除规则 | DELETE | `/api/v1/quota-rules/delete/{id}` | 删除审核规则 |
| **执行计算** | POST | `/api/v1/quota-rules/calculate` | **调用 Dify 执行额度计算** |

### 5.3 核心接口详解

#### 5.3.1 执行额度计算

**接口地址：** `POST /api/v1/quota-rules/calculate`

**请求参数：**

```json
{
  "quotaRuleId": 1,
  "data": {
    "companyId": "C001",
    "companyName": "测试公司",
    "baseAmount": 100000,
    "taxRate": 0.03,
    "profitRate": 0.15
  }
}
```

**响应示例：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "quotaRuleId": 1,
    "quotaRuleName": "企业信用额度",
    "calculatedResult": 106000,
    "status": "success",
    "output": {
      "companyId": "C001",
      "creditLimit": 106000,
      "status": "approved"
    },
    "calculatedAt": "2026-05-14T10:30:00"
  }
}
```

---

## 六、Dify 集成流程

### 6.1 Dify 应用配置

#### 6.1.1 创建应用

1. 登录 Dify 控制台
2. 点击「创建应用」
3. 选择应用类型（聊天应用/工作流应用）
4. 配置应用名称和描述
5. 选择 AI 模型
6. 配置提示词

#### 6.1.2 配置提示词示例

```
你是一个额度审核专家。请根据以下信息计算企业信用额度：

【审核规则】
- 指标编码: {{indicator_codes}}
- 判断条件: {{conditions}}
- 计算公式: {{quota_calculation}}
- 结果变量: {{result_var_name}}

【企业数据】
{% for key, value in data %}
- {{key}}: {{value}}
{% endfor %}

请按照以下步骤执行：
1. 验证判断条件是否满足
2. 使用计算公式得出额度
3. 按照输出模板格式返回结果

输出格式（JSON）：
{{output_template}}
```

### 6.2 获取 API Key

1. 进入应用设置
2. 点击「API Key」选项卡
3. 点击「创建 API Key」
4. 复制生成的 Key

### 6.3 后端调用流程

```
┌──────────────────────────────────────────────────────────────┐
│                    额度计算调用流程                           │
└──────────────────────────────────────────────────────────────┘

1. 接收请求
   前端 POST /api/v1/quota-rules/calculate
   {
     "quotaRuleId": 1,
     "data": { "baseAmount": 100000, ... }
   }

2. 查询审核规则
   从数据库获取 quota_rule 配置

3. 构建 Dify 请求
   {
     "query": "计算额度...",
     "inputs": {
       "indicatorCodes": "TAX_RATE,PROFIT_RATE",
       "conditions": "税负率 < 5%",
       "quotaCalculation": "baseAmount * (1 + profitRate * 0.5 - taxRate * 0.3)",
       ...
     },
     "response_mode": "blocking",
     "user": "quota-calculation"
   }

4. 调用 Dify API
   POST https://api.dify.ai/v1/chat-messages

5. 解析响应
   Dify 返回：
   {
     "answer": "{\"creditLimit\": 106000, \"status\": \"approved\"}",
     "outputs": { ... }
   }

6. 返回结果
   {
     "calculatedResult": 106000,
     "status": "success",
     "output": { ... }
   }
```

### 6.4 两种调用模式

#### 6.4.1 聊天接口模式（Chat API）

适用于简单的额度计算场景：

```java
DifyChatRequest request = DifyChatRequest.builder()
    .query("根据以下条件计算额度...")
    .inputs(inputs)
    .responseMode("blocking")
    .user("quota-calculation")
    .build();

DifyChatResponse response = difyClientService.chat(request);
```

#### 6.4.2 工作流接口模式（Workflow API）

适用于复杂的审核流程：

```java
Map<String, Object> inputs = new HashMap<>();
inputs.put("indicatorCodes", "TAX_RATE,PROFIT_RATE");
inputs.put("conditions", "税负率 < 5%");
// ... 其他输入

Map<String, Object> response = difyClientService.runWorkflow(
    appId,          // Dify 应用 ID
    inputs,         // 输入参数
    "quota-user",   // 用户标识
    apiKey          // API Key（可选）
);
```

---

## 七、使用说明

### 7.1 业务流程

```
┌─────────────────────────────────────────────────────────────────┐
│                       完整业务流程                               │
└─────────────────────────────────────────────────────────────────┘

1. 指标定义阶段
   ├─ 创建任务（数据查询）
   └─ 创建指标（关联任务ID）

2. 规则配置阶段
   ├─ 选择指标编码
   ├─ 配置判断条件
   ├─ 配置计算公式
   ├─ 配置输出模板
   └─ 保存审核规则

3. 执行审核阶段
   ├─ 输入业务数据
   ├─ 调用计算接口
   ├─ Dify 执行计算
   └─ 返回审核结果
```

### 7.2 示例场景

#### 场景：企业信用额度审核

**步骤 1：定义指标**

```json
// 新增指标
POST /api/v1/indicators/create
{
  "indicatorName": "企业税负率",
  "indicatorCode": "TAX_RATE",
  "indicatorLogic": "税负率 = 纳税额 / 营业收入 * 100%",
  "taskId": 1
}
```

**步骤 2：配置审核规则**

```json
// 新增审核规则
POST /api/v1/quota-rules/create
{
  "quotaName": "企业信用额度",
  "indicatorCodes": "TAX_RATE,PROFIT_RATE",
  "conditions": "税负率 < 5%",
  "quotaCalculation": "baseAmount * (1 + profitRate * 0.5 - taxRate * 0.3)",
  "resultVarName": "creditLimit",
  "outputTemplate": "{\"companyId\":\"${companyId}\",\"creditLimit\":\"${creditLimit}\",\"status\":\"${status}\"}"
}
```

**步骤 3：执行额度计算**

```json
// 执行计算
POST /api/v1/quota-rules/calculate
{
  "quotaRuleId": 1,
  "data": {
    "companyId": "C001",
    "baseAmount": 100000,
    "taxRate": 0.03,
    "profitRate": 0.15
  }
}

// 响应
{
  "code": 200,
  "data": {
    "quotaRuleId": 1,
    "quotaRuleName": "企业信用额度",
    "calculatedResult": 106000,
    "status": "success",
    "output": {
      "companyId": "C001",
      "creditLimit": 106000,
      "status": "approved"
    }
  }
}
```

---

## 八、代码结构

### 8.1 文件清单

#### 配置相关
- `rbac-api/src/main/resources/application.yml` - Dify 配置
- `rbac-common/src/main/java/com/rbac/common/config/DifyProperties.java` - 配置属性类

#### DTO 类
- `rbac-common/src/main/java/com/rbac/common/model/dto/dify/DifyChatRequest.java` - Dify 请求
- `rbac-common/src/main/java/com/rbac/common/model/dto/dify/DifyChatResponse.java` - Dify 响应
- `rbac-common/src/main/java/com/rbac/common/model/dto/QuotaCalculateRequest.java` - 额度计算请求
- `rbac-common/src/main/java/com/rbac/common/model/dto/QuotaCalculateResponse.java` - 额度计算响应
- `rbac-common/src/main/java/com/rbac/common/model/dto/AddQuotaRuleRequest.java` - 新增审核规则请求
- `rbac-common/src/main/java/com/rbac/common/model/dto/UpdateQuotaRuleRequest.java` - 更新审核规则请求

#### 实体类
- `rbac-core/src/main/java/com/rbac/core/domain/entity/Indicator.java` - 指标实体
- `rbac-core/src/main/java/com/rbac/core/domain/entity/QuotaRule.java` - 审核规则实体

#### Mapper
- `rbac-core/src/main/java/com/rbac/core/domain/mapper/IndicatorMapper.java` - 指标 Mapper
- `rbac-core/src/main/java/com/rbac/core/domain/mapper/QuotaRuleMapper.java` - 审核规则 Mapper

#### Service
- `rbac-core/src/main/java/com/rbac/core/service/DifyClientService.java` - Dify 客户端服务
- `rbac-core/src/main/java/com/rbac/core/service/QuotaRuleService.java` - 审核规则服务接口
- `rbac-core/src/main/java/com/rbac/core/service/impl/QuotaRuleServiceImpl.java` - 审核规则服务实现

#### Controller
- `rbac-api/src/main/java/com/rbac/api/controller/IndicatorController.java` - 指标管理控制器
- `rbac-api/src/main/java/com/rbac/api/controller/QuotaRuleController.java` - 审核规则控制器

#### 数据库脚本
- `docs/sql/indicator_management.sql` - 数据库建表脚本

### 8.2 核心类说明

#### DifyClientService

Dify 客户端服务类，负责与 Dify API 交互：

```java
@Service
public class DifyClientService {

    // 调用聊天接口
    public DifyChatResponse chat(DifyChatRequest request);

    // 调用聊天接口（指定 API Key）
    public DifyChatResponse chat(DifyChatRequest request, String apiKey);

    // 调用工作流接口
    public Map<String, Object> runWorkflow(String appId, Map<String, Object> inputs, String user);

    // 调用工作流接口（指定 API Key）
    public Map<String, Object> runWorkflow(String appId, Map<String, Object> inputs, String user, String apiKey);
}
```

#### QuotaRuleServiceImpl

审核规则服务实现类，负责业务逻辑处理：

```java
@Service
public class QuotaRuleServiceImpl {

    // 执行额度计算（核心方法）
    public Result<QuotaCalculateResponse> calculateQuota(QuotaCalculateRequest request);

    // 解析输出模板
    private Map<String, Object> parseOutputTemplate(String template, Map<String, Object> data);
}
```

---

## 九、常见问题

### 9.1 Dify API 调用失败

**问题描述：** 调用 Dify API 时返回错误

**可能原因：**
- API Key 配置错误
- Dify 服务不可用
- 网络连接超时

**解决方案：**
1. 检查 `application.yml` 中的 `base-url` 和 `api-key` 配置
2. 确认 Dify 服务正常运行
3. 增加超时时间配置

### 9.2 额度计算结果为空

**问题描述：** Dify 返回结果，但解析后为空

**可能原因：**
- 输出模板格式错误
- Dify 返回格式不符合预期

**解决方案：**
1. 检查 `output_template` 是否为有效的 JSON 格式
2. 查看 Dify 原始响应日志
3. 调整 `parseOutputTemplate` 方法的解析逻辑

### 9.3 指标被引用无法删除

**问题描述：** 删除指标时提示被审核规则引用

**解决方案：**
1. 先删除引用该指标的所有审核规则
2. 或者在 `IndicatorServiceImpl.deleteIndicator()` 方法中添加级联删除逻辑

### 9.4 Dify 工作流和聊天应用如何选择？

**建议：**
- 简单额度计算：使用聊天应用（Chat App）
- 复杂审核流程：使用工作流应用（Workflow App）

---

## 十、部署说明

### 10.1 环境要求

- JDK 17+
- MySQL 8.0+
- Dify 云端或私有化部署

### 10.2 部署步骤

1. **执行数据库脚本**
   ```bash
   mysql -u root -p rbac_system < docs/sql/indicator_management.sql
   ```

2. **配置环境变量**
   ```bash
   export DIFY_API_KEY=your-api-key
   ```

3. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

4. **验证接口**
   - 访问 Swagger UI：`http://localhost:8080/swagger-ui.html`
   - 测试指标管理接口
   - 测试审核规则接口
   - 测试额度计算接口

---

## 十一、版本信息

| 版本 | 日期 | 修改人 | 说明 |
|------|------|--------|------|
| V1.0 | 2026-05-14 | AI Assistant | 初始版本，实现基础额度计算功能 |

---

## 附录

### A. Dify API 文档

- 官方文档：https://docs.dify.ai/
- API Reference：https://api.dify.ai/

### B. Spring Boot 配置参考

```yaml
spring:
  application:
    name: rbac-system

# Dify 配置
dify:
  base-url: ${DIFY_BASE_URL:https://api.dify.ai/v1}
  api-key: ${DIFY_API_KEY:}
  connection-timeout: ${DIFY_CONNECTION_TIMEOUT:30000}
  read-timeout: ${DIFY_READ_TIMEOUT:60000}
```

### C. 日志配置

建议在生产环境开启 Dify API 调用的详细日志：

```xml
<!-- logback-spring.xml -->
<logger name="com.rbac.core.service.DifyClientService" level="DEBUG"/>
```
