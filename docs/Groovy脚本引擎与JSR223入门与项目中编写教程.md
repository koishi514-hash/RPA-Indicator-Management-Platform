# Groovy 脚本引擎与 JSR-223 入门教程与本项目中编写教程

本文档分为两部分：**一、Groovy 与 JSR-223 入门**（概念、基本用法）；**二、本项目中如何编写与运行 Groovy 脚本**（注入变量、四步流程示例、注意事项）。

---

## 一、Groovy 脚本引擎与 JSR-223 入门教程

### 1.1 什么是 Groovy？

**Groovy** 是一种基于 JVM 的**动态语言**，与 Java 高度兼容：

- 语法上可以几乎直接写 Java 代码，也可使用 Groovy 的简写（如可选分号、可选类型、GString、闭包等）。
- 与 Java 无缝互操作：可调用任意 Java 类和方法，Java 也可调用 Groovy 编译后的类或通过脚本引擎执行 Groovy 脚本。
- 适合作为**脚本**使用：无需先编译成 class，在运行时通过脚本引擎执行字符串形式的代码，便于“配置化逻辑”（如流程中的采集、解析、加工、落库步骤）。

在本项目中，流程步骤类型为 **java** 时，步骤内的代码就是 **Groovy 脚本**，由 JSR-223 引擎在 JVM 内执行。

### 1.2 什么是 JSR-223？

**JSR-223**（Java Specification Request 223）是 Java 平台自 1.6 起提供的**脚本引擎标准 API**，用于在 Java 程序中嵌入多种脚本语言（如 JavaScript、Groovy、Python 等）。

核心类和接口：

| 类/接口 | 说明 |
|---------|------|
| **javax.script.ScriptEngineManager** | 脚本引擎管理器，根据名称或扩展名获取某种语言的 ScriptEngine。 |
| **javax.script.ScriptEngine** | 某种语言的脚本引擎，可执行脚本字符串。 |
| **javax.script.ScriptContext** / **Bindings** | 脚本的“作用域”：在 Java 端放入的键值对，在脚本中可作为全局变量使用。 |
| **engine.eval(String script)** | 执行一段脚本，返回脚本最后一行的值或 `return` 的值。 |

基本用法示例（与语言无关）：

```java
ScriptEngineManager manager = new ScriptEngineManager();
ScriptEngine engine = manager.getEngineByName("groovy");
if (engine == null) {
    throw new RuntimeException("未找到 Groovy 引擎，请确认已引入 groovy-jsr223 依赖");
}

// 注入变量：脚本中可直接使用变量名 a、b
engine.put("a", 10);
engine.put("b", 20);

Object result = engine.eval("a + b");
System.out.println(result);  // 30
```

脚本中也可以调用 Java 类：

```java
engine.put("name", "World");
engine.eval("println \"Hello, \" + name");
engine.eval("return new java.util.Date().toString()");
```

### 1.3 在 Java 中获取 Groovy 引擎

要使用 Groovy 作为 JSR-223 脚本语言，需要引入 **groovy-jsr223** 依赖。引擎名称一般为 **"groovy"**：

```java
ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
```

若返回 `null`，说明 classpath 中没有 Groovy 的 JSR-223 实现，请检查依赖中是否包含：

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-jsr223</artifactId>
    <version>3.0.9</version>
</dependency>
```

### 1.4 向脚本注入变量（Bindings）

脚本执行前，把 Java 对象放入引擎的 Bindings，脚本里就可以直接用同名变量：

```java
SimpleBindings bindings = new SimpleBindings();
bindings.put("page", playwrightPage);      // Playwright 的 Page 对象
bindings.put("jdbcTemplate", jdbcTemplate);
bindings.put("taskId", "123");
bindings.put("baseUrl", "http://localhost:3000");
engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
Object result = engine.eval(userScript);
```

脚本中：

```groovy
if (page == null) throw new RuntimeException("未注入 page")
page.navigate(baseUrl + "/some-path")
String html = page.content()
// 使用 jdbcTemplate 执行 SQL、taskId 等
return "{\"collectionId\":1}"
```

- 注入的是 **对象引用**，脚本内对可变形对象的修改在 Java 端可见。  
- 脚本返回值：`eval` 返回脚本最后一条表达式的值或 `return` 的值；若希望把结果传给“下一步”，可约定返回 JSON 字符串，由调用方解析并合并到 context。

### 1.5 Groovy 与 Java 的少量语法差异（脚本中常用）

- **可选分号**：行尾分号可省略。  
- **可选类型**：可写 `def x = 1` 或 `String x = "a"`。  
- **字符串**：双引号 `"..."` 为 GString，可 `${var}` 插值；单引号 `'...'` 为普通 String。  
- **判空与安全导航**：`if (x == null)` 或 `x?.trim()`。  
- **闭包与集合**：可用 `list.each { ... }` 等，脚本中若需复杂集合操作可简化写法。  
- **与 Java 互操作**：可直接 `new java.util.ArrayList()`、`java.sql.Timestamp.valueOf(now)` 等。

在“流程步骤”的 Groovy 脚本中，建议保持**偏 Java 风格**，减少对 Groovy 特有语法的依赖，便于团队协作和排查问题；且运行环境可能只提供 groovy-jsr223，不包含完整 Groovy 库。

### 1.6 小结：入门必记

1. **JSR-223**：`ScriptEngineManager` 按名称取 `ScriptEngine`，用 `engine.put(key, value)` 注入变量，`engine.eval(script)` 执行脚本。  
2. **Groovy 引擎**：依赖 `groovy-jsr223`，`getEngineByName("groovy")`。  
3. **脚本与 Java 互通**：通过 Bindings 注入和返回值；脚本可调用任意 Java 类。  
4. **本项目**：流程步骤 type=java 时，步骤的 `code` 就是 Groovy 脚本，由执行器注入 `page`、`jdbcTemplate`、`taskId` 等后执行。

---

## 二、本项目中如何编写与运行 Groovy 脚本

### 2.1 依赖与入口

- **依赖**（`backend/rpa-spider-management-system/pom.xml`）：

```xml
<dependency>
    <groupId>org.codehaus.groovy</groupId>
    <artifactId>groovy-jsr223</artifactId>
    <version>3.0.9</version>
</dependency>
```

- **执行入口**：流程步骤执行器实现类  
  `com.rpa.system.executor.impl.ProcessStepExecutorImpl`  
  中的 **runJavaStep** 方法：当步骤类型为 `java` 时，会取当前步骤的 `code`，用 Groovy 引擎执行，并把当前流程的 **Page** 以及一系列上下文变量注入脚本。

### 2.2 执行器为脚本注入的变量

在 **runJavaStep** 中，会向脚本 Bindings 放入以下变量（具体以代码为准，此处为常见集合）：

| 变量名 | 类型 | 说明 |
|--------|------|------|
| **page** | com.microsoft.playwright.Page | 当前流程的 Playwright 页面对象，用于 navigate、fill、click、content 等。 |
| **jdbcTemplate** | org.springframework.jdbc.core.JdbcTemplate | Spring JDBC 模板，可执行 SQL（如 INSERT 并取自增 ID）。 |
| **baseUrl** | String | 基础 URL，如 http://localhost:3000。 |
| **playwrightTimeoutMs** | String | 超时毫秒数（字符串）。 |
| **taskId** | String | 当前任务 ID。 |
| **enterpriseName** | String | 企业名称（来自任务）。 |
| **taxNo** | String | 纳税人识别号（来自任务）。 |
| **categoryId** | String | 分类 ID（若任务有）。 |
| **dataCollectionService** 等 | Service 接口 | 若存在则注入，用于通过 Service 落库。 |
| **pipelineService** | （spider_exc 可选） | 若存在则注入。 |

此外，**上一步的返回值**会被执行器解析为 JSON，并把顶层 key 合并到**下一步**的 context 中，因此在“解析”“加工”“落库”步骤中可以使用 **collectionId**、**parsingId**、**processingId** 等（由前一步脚本返回的 JSON 提供）。

### 2.3 流程步骤类型 java 的触发方式

- 流程的 **processData** 为 JSON，其中 **steps** 为步骤数组。  
- 某一步的 **type** 为 **"java"** 时，该步会走 **runJavaStep**。  
- 该步的 **code** 字段即为 Groovy 脚本字符串（可由前端流程设计器编辑或后端配置）。  
- 执行器在同一流程内共用一个 **Page**：前一步可能是 openUrl/click/fill，下一步 type=java 时，脚本中的 `page` 就是当前已经打开/操作过的页面，可直接继续 `page.content()` 或再做 navigate。

### 2.4 脚本编写规范与推荐写法

1. **判空与参数校验**  
   脚本开头对关键注入变量做判空，避免 NPE 和难以理解的报错：

   ```groovy
   if (page == null) throw new RuntimeException("未注入 page")
   if (jdbcTemplate == null) throw new RuntimeException("未注入 jdbcTemplate")
   ```

2. **从 context 取上一步结果**  
   解析、加工、落库步骤通常依赖上一步返回的 ID，可从绑定中取（执行器会把上一步 outputData 的 JSON 合并到 context，再注入下一步）：

   ```groovy
   String cidStr = (collectionId != null && collectionId.toString().trim().length() > 0) ? collectionId.toString().trim() : null
   if (cidStr == null) throw new RuntimeException("缺少 collectionId，请先执行采集步骤")
   Long cid = Long.parseLong(cidStr)
   ```

3. **返回值约定**  
   若下一步需要本步产生的 ID，建议返回 JSON 字符串，例如：

   ```groovy
   return "{\"collectionId\":" + collectionId + "}"
   return "{\"parsingId\":" + parsingId + "}"
   ```

   执行器会解析该字符串，把 `collectionId`、`parsingId` 等合并到下一步的 context。

4. **尽量使用 JDK 与项目已有类**  
   避免在脚本中依赖脚本环境未提供的第三方库；常用：  
   - JDK：String、Long、Integer、LocalDateTime、Map、List 等  
   - Spring：JdbcTemplate、PreparedStatementCreator、GeneratedKeyHolder  
   - 项目已引入：ObjectMapper（Jackson）、Playwright 的 Page 等  

5. **SQL 与安全**  
   使用 **参数化查询**（PreparedStatement），不要用字符串拼接 SQL，防止注入；自增 ID 可用 `PreparedStatement.RETURN_GENERATED_KEYS` + `KeyHolder` 获取。

### 2.5 四步流程中的 Groovy 示例概要

项目内 **`spider_exc/流程设计四步代码示例.md`** 给出了完整的四步（采集、解析、加工、落库）Groovy 示例，这里仅概括每步在脚本层面的要点：

- **采集（第 1 步）**  
  - 使用 `page`：navigate、waitForLoadState、`page.content()` 得到 HTML 或 JSON。  
  - 使用 `jdbcTemplate`：INSERT 到 `rpa_data_collection`，用 KeyHolder 取自增 ID。  
  - 返回 `"{\"collectionId\":" + collectionId + "}"`。

- **解析（第 2 步）**  
  - 从 context 取 `collectionId`，用 `jdbcTemplate` 查询 `rpa_data_collection` 的 `raw_data`。  
  - 解析（如用 ObjectMapper 或字符串处理）得到结构化数据，INSERT 到 `rpa_data_parsing`。  
  - 返回 `"{\"parsingId\":" + parsingId + "}"`。

- **加工（第 3 步）**  
  - 从 context 取 `parsingId`，查询 `rpa_data_parsing`，按业务规则计算指标。  
  - INSERT 到 `rpa_data_processing`，可返回 `processingId`。

- **落库（第 4 步）**  
  - 从 context 取 `processingId`（或解析/加工结果），写入最终业务表 `rpa_data_query`。

每步的**详细可运行代码**见 **`spider_exc/流程设计四步代码示例.md`**，直接复制到流程设计中的“采集/解析/加工/落库”四段 script 即可使用（注意表结构与字段名与当前库一致）。

### 2.6 执行器中的核心代码片段（参考）

以下为 **ProcessStepExecutorImpl.runJavaStep** 中与 Groovy 相关的核心逻辑（便于理解“本项目中怎么运行 Groovy”）：

```java
ScriptEngine engine = new ScriptEngineManager().getEngineByName("groovy");
if (engine == null) {
    return fail(..., "未找到 Groovy 引擎，请确认已引入 groovy-jsr223 依赖");
}
SimpleBindings bindings = new SimpleBindings();
if (context != null) {
    for (Map.Entry<String, String> e : context.entrySet()) {
        bindings.put(e.getKey(), e.getValue());
    }
}
if (page != null) bindings.put("page", page);
if (dataCollectionService != null) bindings.put("dataCollectionService", dataCollectionService);
// ... 其他 Service、jdbcTemplate
if (jdbcTemplate != null) bindings.put("jdbcTemplate", jdbcTemplate);
engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
Object result = engine.eval(code);
// result 转为 StepExecutionResult 的 outputData，若为 JSON 会合并到下一步 context
```

- **context**：来自流程执行时的任务参数与上一步的 outputData（解析后的键值对）。  
- **code**：当前步骤的 `code` 字段（Groovy 脚本字符串）。

### 2.7 常见问题与排查

- **“未找到 Groovy 引擎”**  
  确认 classpath 中有 `groovy-jsr223`，且版本与 JDK 兼容（如 JDK 17 可用 Groovy 3.x）。

- **脚本中 page 为 null**  
  确认该步骤在流程中为 type=java，且由同一执行器在同一流程、同一 Page 生命周期内执行（即不是单独新开流程只执行一步）。

- **collectionId / parsingId 在下一步取不到**  
  上一步必须返回 JSON 字符串且包含对应 key，且执行器会把该 JSON 解析后合并到下一步的 context；检查上一步是否 `return "{\"collectionId\":...}"` 以及是否有异常导致未返回。

- **脚本里用到的类找不到（ClassNotFoundException 等）**  
  脚本只能使用 JVM classpath 中存在的类；避免依赖仅在开发环境存在的库，或将该逻辑改为 Java 代码在服务中实现，脚本只做简单调用。

- **SQL 或数据格式错误**  
  脚本中打印/日志有限，建议在本地用单元测试（如 **spider_exc** 中的 **FourStepPipelineTest**）跑通四步，再放到流程中执行。

---

## 三、小结

- **入门**：理解 JSR-223 的 ScriptEngineManager、ScriptEngine、Bindings、eval；Groovy 通过 groovy-jsr223 提供引擎；脚本通过注入变量与 Java 互通。  
- **本项目中**：流程步骤 type=java 时，code 为 Groovy 脚本；执行器注入 page、jdbcTemplate、taskId、baseUrl、上一步的 collectionId/parsingId/processingId 等；脚本应返回 JSON 字符串以便下一步使用；完整四步示例见 **`spider_exc/流程设计四步代码示例.md`**。  
- **规范**：判空、参数化 SQL、返回值约定、少依赖冷门库，便于维护和排错。
