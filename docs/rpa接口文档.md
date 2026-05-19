# API 接口文档

## 0. 认证与登录 (Authentication)

### 0.1 用户登录

用户通过用户名和密码进行登录，登录成功后返回 token 信息。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/login`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**请求字段说明：**

| 字段     | 类型   | 必填 | 说明                           |
| -------- | ------ | ---- | ------------------------------ |
| username | string | 是   | 用户名                         |
| password | string | 是   | 密码（明文传输，建议使用 HTTPS） |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJsb2dpbklkIjoiMTIzNDU2Nzg5MCIsInVzZXJuYW1lIjoiYWRtaW4ifQ.xxx",
    "tokenName": "satoken",
    "isLogin": true,
    "loginId": "2",
    "username": "admin",
    "realName": "系统管理员",
    "timeout": 2592000
  }
}
```

**响应字段说明：**

| 字段         | 类型    | 说明                           |
| ------------ | ------- | ------------------------------ |
| code         | number  | 响应状态码，200 表示成功       |
| msg          | string  | 响应消息                       |
| data         | object  | 登录信息对象                   |
| data.token   | string  | 登录凭证 token，前端需在后续请求中携带 |
| data.tokenName | string | token 的名称，默认为 satoken |
| data.isLogin | boolean | 是否已登录                     |
| data.loginId | string  | 登录 ID，通常为 userId         |
| data.username | string | 用户名                         |
| data.realName | string | 真实姓名                       |
| data.timeout | number  | token 有效期（秒），默认 2592000（30 天） |

**错误响应示例 (401 Unauthorized)：**

```json
{
  "code": 401,
  "msg": "用户名或密码错误",
  "data": null
}
```

---

### 0.2 退出登录

用户退出登录，使当前 token 失效。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/logout`
- **请求头：** 需携带 token（通过 Header: satoken=xxx 或 Cookie）
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "退出登录成功",
  "data": null
}
```

---

### 0.3 获取当前登录用户信息

获取当前登录用户的会话信息（用于验证 token 有效性）。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/auth/info`
- **请求头：** 需携带 token
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "loginId": "2",
    "userId": 2,
    "username": "admin",
    "realName": "系统管理员",
    "email": "admin@rpa.com",
    "roleCodes": ["ADMIN"],
    "permissions": ["*"]
  }
}
```

**响应字段说明：**

| 字段              | 类型        | 说明                           |
| ----------------- | ----------- | ------------------------------ |
| code              | number      | 响应状态码，200 表示成功       |
| msg               | string      | 响应消息                       |
| data              | object      | 用户会话信息对象               |
| data.loginId      | string      | 登录 ID                        |
| data.userId       | number      | 用户 ID                        |
| data.username     | string      | 用户名                         |
| data.realName     | string      | 真实姓名                       |
| data.email        | string      | 邮箱                           |
| data.roleCodes    | array       | 角色代码数组                   |
| data.permissions  | array       | 权限列表                       |

**错误响应示例 (401 Unauthorized)：**

```json
{
  "code": 401,
  "msg": "未登录或 token 已过期",
  "data": null
}
```

---

## 1. 个人信息 (Profile)

### 1.1 获取当前登录用户个人信息

获取当前登录用户的个人信息。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/profile`
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "userId": 2,
    "username": "admin",
    "realName": "系统管理员",
    "email": "admin@rpa.com",
    "phone": "13800138000",
    "avatar": "https://example.com/avatar/2.jpg",
    "status": 1,
    "createTime": "2026-03-16T21:09:34"
  }
}
```

### 1.2 更新个人信息（部分更新）

更新当前登录用户的个人信息，支持部分字段更新。

- **请求方法：** `PUT`
- **请求路径：** `/api/v1/system/profile`
- **请求体：** JSON 格式，所有字段均为可选

**请求体示例：**

```json
{
  "realName": "新姓名",
  "email": "newemail@rpa.com",
  "phone": "13900000001",
  "avatar": "https://new-avatar.jpg"
}
```

**请求字段说明：**

| 字段     | 类型   | 必填 | 说明       |
| -------- | ------ | ---- | ---------- |
| realName | string | 否   | 真实姓名   |
| email    | string | 否   | 新邮箱     |
| phone    | string | 否   | 新手机号   |
| avatar   | string | 否   | 新头像 URL |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "个人信息更新成功",
  "data": null
}
```

---

### 1.3 修改密码（需要验证旧密码）

修改当前用户的登录密码，需提供旧密码进行验证。

- **请求方法：** `PUT`
- **请求路径：** `/api/v1/system/profile/password`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "oldPassword": "oldpass123",
  "newPassword": "NewPass456",
  "confirmPassword": "NewPass456"
}
```

**请求字段说明：**

| 字段            | 类型   | 必填 | 说明                                   |
| --------------- | ------ | ---- | -------------------------------------- |
| oldPassword     | string | 是   | 旧密码                                 |
| newPassword     | string | 是   | 新密码                                 |
| confirmPassword | string | 否   | 确认新密码，后端会校验与新密码是否一致 |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "密码修改成功",
  "data": null
}
```

---

---

## 2. 用户管理 (Users)

### 2.1 分页查询用户列表

分页查询用户列表。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/users`
- **请求体：** 无（通过 URL Query 参数传递筛选或分页条件）

**请求参数：**

| 参数名      | 类型    | 必填 | 说明                 |
| ----------- | ------- | ---- | -------------------- |
| username    | string  | 否   | 用户名（模糊查询）    |
| realName    | string  | 否   | 真实姓名（模糊查询） |
| roleCode    | string  | 否   | 角色代码（精确查询）  |
| pageNum     | integer | 否   | 页码，默认 1         |
| pageSize    | integer | 否   | 每页条数，默认 10    |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 3,
    "pages": 1,
    "size": 10,
    "current": 1,
    "records": [
      {
        "userId": 1,
        "username": "zmy",
        "realName": "郑明月",
        "email": "342@qq.com",
        "phone": "189996136565",
        "roleNames": "管理员",
        "roleCodes": "ADMIN",
        "status": 1,
        "createTime": "2026-03-16T21:11:55",
        "updateTime": "2026-03-18T10:00:00"
      }
    ]
  }
}
```

**响应字段说明：**

| 字段                      | 类型   | 说明                     |
| ------------------------- | ------ | ------------------------ |
| code                      | number | 响应状态码，200 表示成功 |
| msg                       | string | 响应消息                 |
| data                      | object | 分页数据对象             |
| data.total                | number | 总记录数                 |
| data.pages                | number | 总页数                   |
| data.size                 | number | 每页大小                 |
| data.current              | number | 当前页码                 |
| data.records              | array  | 用户记录数组             |
| data.records[].userId     | number | 用户 ID                  |
| data.records[].username   | string | 用户名                   |
| data.records[].realName   | string | 真实姓名                 |
| data.records[].email      | string | 邮箱                     |
| data.records[].phone      | string | 手机号                   |
| data.records[].roleNames  | string | 角色名称                 |
| data.records[].roleCodes  | string | 角色代码                 |
| data.records[].status     | number | 状态：1-启用，0-禁用     |
| data.records[].createTime | string | 创建时间 (ISO 8601 格式) |
| updateTime                | Date   | 更新时间 (ISO 8601 格式) |

---

### 2.2 获取单个用户详情（编辑回显用）

获取单个用户的详细信息，常用于编辑页面回显数据。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/users/{userId}`
- **路径参数：** `userId` — 用户 ID
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "userId": 2,
    "username": "admin",
    "realName": "系统管理员",
    "email": "admin@rpa.com",
    "phone": "13800138000",
    "avatar": null,
    "status": 1,
    "roleIds": [1],
    "roleNames": "管理员",
    "createTime": "2026-03-16T21:09:34",
    "updateTime": "2026-03-18T10:00:00"
  }
}
```

**响应字段说明：**

| 字段            | 类型        | 说明                     |
| --------------- | ----------- | ------------------------ |
| code            | number      | 响应状态码，200 表示成功 |
| msg             | string      | 响应消息                 |
| data            | object      | 用户详情对象             |
| data.userId     | number      | 用户 ID                  |
| data.username   | string      | 用户名                   |
| data.realName   | string      | 真实姓名                 |
| data.email      | string      | 邮箱                     |
| data.phone      | string      | 手机号                   |
| data.avatar     | string/null | 头像 URL                 |
| data.status     | number      | 状态：1-启用，0-禁用     |
| data.roleIds    | array       | 当前绑定的角色 ID 数组   |
| data.roleNames  | string      | 角色名称                 |
| data.createTime | string      | 创建时间 (ISO 8601 格式) |
| updateTime      | Date        | 更新时间 (ISO 8601 格式) |

---

### 2.3 新增用户

创建新用户。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/users`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "username": "newuser001",
  "realName": "新用户",
  "password": "InitPass123",
  "email": "new@rpa.com",
  "phone": "13800138003",
  "roleIds": [2],
  "status": 1
}
```

**请求字段说明：**

| 字段     | 类型   | 必填 | 说明                               |
| -------- | ------ | ---- | ---------------------------------- |
| username | string | 是   | 用户名                             |
| realName | string | 否   | 真实姓名                           |
| password | string | 是   | 初始密码（明文传输，后端加密存储） |
| email    | string | 否   | 邮箱                               |
| phone    | string | 否   | 手机号                             |
| roleIds  | array  | 否   | 角色 ID 数组，支持多角色           |
| status   | number | 否   | 状态：1-启用，0-禁用              |

**响应示例 (200/201 OK)：**

```json
{
  "code": 200,
  "msg": "用户创建成功",
  "data": {
    "userId": 4
  }
}
```

---

### 2.4 修改用户信息

更新现有用户信息，支持部分字段更新。

- **请求方法：** `PUT`
- **请求路径：** `/api/v1/system/users/{userId}`
- **路径参数：** `userId` — 用户 ID
- **请求体：** JSON 格式，各字段均为可选（部分更新）

**请求体示例：**

```json
{
  "realName": "修改姓名",
  "email": "update@email.com",
  "phone": "13911112222",
  "roleIds": [1],
  "status": 1
}
```

**请求字段说明：**

| 字段     | 类型   | 必填 | 说明                           |
| -------- | ------ | ---- | ------------------------------ |
| realName | string | 否   | 真实姓名                       |
| email    | string | 否   | 邮箱                           |
| phone    | string | 否   | 手机号                         |
| roleIds  | array  | 否   | 角色 ID 数组，全量覆盖原有角色 |
| status   | number | 否   | 状态：1-启用，0-禁用           |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "更新成功",
  "data": null
}
```

---

### 2.5 重置用户密码

重置指定用户的密码。

- **请求方法：** `PUT`
- **请求路径：** `/api/v1/system/users/{userId}/reset-password`
- **路径参数：** `userId` — 用户 ID
- **请求体：** JSON 格式，可选

**请求体示例：**

```json
{
  "newPassword": "Reset888"
}
```

**请求字段说明：**

| 字段        | 类型   | 必填 | 说明                                         |
| ----------- | ------ | ---- | -------------------------------------------- |
| newPassword | string | 否   | 新密码。如不传递，系统将随机生成并返回新密码 |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "密码重置成功",
  "data": null
}
```

---

### 2.6 删除用户

删除指定用户。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/users/{userId}`
- **路径参数：** `userId` — 用户 ID
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

---

### 2.7 禁用用户

禁用指定用户（将用户状态设置为禁用）。

- **请求方法：** `PATCH`
- **请求路径：** `/api/v1/system/users/{userId}/status`
- **路径参数：** `userId` — 用户 ID
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "status": 0
}
```

**请求字段说明：**

| 字段   | 类型   | 必填 | 说明                   |
| ------ | ------ | ---- | ---------------------- |
| status | number | 是   | 状态：0-禁用，1-启用 |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "状态更新成功",
  "data": null
}
```

---

## 3. 角色管理 (Roles)

### 3.1 分页查询角色列表

分页查询角色列表。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/roles`
- **请求体：** 无（通过 URL Query 参数传递分页条件）

**请求参数：**

| 参数名     | 类型    | 必填 | 说明                 |
| ---------- | ------- | ---- | -------------------- |
| roleName   | string  | 否   | 角色名称（模糊查询） |
| roleCode   | string  | 否   | 角色编码（精确查询） |
| pageNum    | integer | 否   | 页码，默认 1         |
| pageSize   | integer | 否   | 每页条数，默认 10    |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 3,
    "pages": 1,
    "size": 10,
    "current": 1,
    "records": [
      {
        "roleId": 1,
        "roleCode": "ADMIN",
        "roleName": "系统管理员",
        "description": "拥有所有权限",
        "status": 1,
        "createTime": "2026-03-16T21:09:34",
        "updateTime": "2026-03-18T11:00:00"
      }
    ]
  }
}
```

**响应字段说明：**

| 字段                        | 类型   | 说明                     |
| --------------------------- | ------ | ------------------------ |
| code                        | number | 响应状态码，200 表示成功 |
| msg                         | string | 响应消息                 |
| data                        | object | 分页数据对象             |
| data.total                  | number | 总记录数                 |
| data.records                | array  | 角色记录数组             |
| data.records[].roleId       | number | 角色 ID                  |
| data.records[].roleCode     | string | 角色代码                 |
| data.records[].roleName     | string | 角色名称                 |
| data.records[].description  | string | 角色描述                 |
| data.records[].status       | number | 状态：1-启用，0-禁用     |
| data.records[].createTime   | string | 创建时间 (ISO 8601 格式) |
| updateTime                  | Date   | 更新时间 (ISO 8601 格式) |

---

### 3.2 新增角色

创建新角色。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/roles`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "roleCode": "MANAGER",
  "roleName": "部门经理",
  "description": "可管理本部门任务",
  "status": 1
}
```

**请求字段说明：**

| 字段        | 类型   | 必填 | 说明                   |
| ----------- | ------ | ---- | ---------------------- |
| roleCode    | string | 是   | 角色代码               |
| roleName    | string | 是   | 角色名称               |
| description | string | 否   | 角色描述               |
| status      | number | 否   | 状态：1-启用，0-禁用 |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "角色创建成功",
  "data": {
    "roleId": 4
  }
}
```

---

### 3.3 修改角色基本信息（权限分配，动态修改）

修改角色关联的权限资源，采用全量覆盖方式。

- **请求方法：** `PUT`
- **请求路径：** `/api/v1/system/roles/{roleId}`
- **路径参数：** `roleId` — 角色 ID
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "roleName": "查看所有",
  "resourceIds": [1, 2, 3, 10, 11, 12],
  "description": "可查看所有资源"
}
```

**请求字段说明：**

| 字段        | 类型  | 必填 | 说明                           |
| ----------- | ----- | ---- | ------------------------------ |
| roleName    | string | 否   | 角色名称                       |
| resourceIds | array | 否   | 资源 ID 数组，全量覆盖原有权限 |
| description | string | 否   | 角色描述                       |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "权限分配成功",
  "data": null
}
```

---

### 3.4 删除角色

删除指定角色。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/roles/{roleId}`
- **路径参数：** `roleId` — 角色 ID
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "删除成功（若有用户绑定可能提示警告）",
  "data": null
}
```

### 3.5 获取资源信息(用于3.3权限分配使用ID)

获取所有资源信息，用于权限分配时选择资源ID。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/roles/resources`
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": null,
  "data": {
    "resources": [
      {
        "resourceId": 1,
        "resourceCode": "HOME",
        "resourceName": "首页",
        "resourceType": "MENU",
        "parentId": 0,
        "path": "/home",
        "icon": "Home",
        "sortOrder": 1,
        "status": 1,
        "children": []
      },
      {
        "resourceId": 2,
        "resourceCode": "RPA_MANAGE",
        "resourceName": "RPA运营管理",
        "resourceType": "MENU",
        "parentId": 0,
        "path": "/rpa",
        "icon": "Setting",
        "sortOrder": 2,
        "status": 1,
        "children": []
      }
    ]
  }
}
```

---

## 4. 资源管理 (Resources)

### 4.1 分页查询资源列表（支持树形结构）

分页查询资源列表，支持按名称和类型筛选，并可返回树形结构。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/resources`
- **Query 参数：**
  - `tree`（可选）— `true` 返回树形结构，`false` 或不传返回扁平列表
  - `resourceName`（可选）— 资源名称（模糊查询）
  - `resourceType`（可选）— 资源类型（精确查询）
  - `pageNum`（可选）— 页码，默认 1
  - `pageSize`（可选）— 每页条数，默认 10
- **请求体：** 无

**请求参数说明：**

| 参数名       | 类型    | 必填 | 说明                                                 |
| ------------ | ------- | ---- | ---------------------------------------------------- |
| tree         | boolean | 否   | 是否返回树形结构，true表示返回包含children的树形结构 |
| resourceName | string  | 否   | 资源名称（模糊查询）                                 |
| resourceType | string  | 否   | 资源类型：MENU-菜单，BUTTON-按钮                     |
| pageNum      | integer | 否   | 页码，默认 1                                         |
| pageSize     | integer | 否   | 每页条数，默认 10                                    |

**响应示例 (树形, 200 OK)：**

````json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 2,
    "pages": 1,
    "size": 10,
    "current": 1,
    "records": [
      {
        "resourceId": 1,
        "resourceCode": "HOME",
        "resourceName": "首页",
        "resourceType": "MENU",
        "parentId": 0,
        "path": "/home",
        "icon": "Home",
        "sortOrder": 1,
        "status": 1,
        "createTime": "2026-03-16T21:09:34",
        "updateTime": "2026-03-18T11:00:00",
        "children": []
      },
      {
        "resourceId": 2,
        "resourceCode": "RPA_MANAGE",
        "resourceName": "RPA运营管理",
        "resourceType": "MENU",
        "parentId": 0,
        "path": "/rpa",
        "icon": "Setting",
        "sortOrder": 2,
        "status": 1,
        "createTime": "2026-03-16T21:09:34",
        "updateTime": "2026-03-18T11:00:00",
        "children": [
          {
            "resourceId": 3,
            "resourceCode": "TASK_LIST",
            "resourceName": "任务列表",
            "resourceType": "MENU",
            "parentId": 2,
            "path": "/task-list",
            "icon": "List",
            "sortOrder": 1,
            "status": 1,
            "createTime": "2026-03-16T21:09:34",
            "updateTime": "2026-03-18T11:00:00",
            "children": []
          }
        ]
      }
    ]
  }
}
````

### **4.2 新增资源**

创建新资源。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/resources`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "resourceCode": "NEW_FEATURE",
  "resourceName": "新功能入口",
  "resourceType": "MENU",
  "parentId": 2,
  "path": "/new-feature",
  "icon": "Star",
  "sortOrder": 15,
  "status": 1
}
```

**请求字段说明：**

| 字段         | 类型   | 必填 | 说明                             |
| ------------ | ------ | ---- | -------------------------------- |
| resourceCode | string | 是   | 资源编码                         |
| resourceName | string | 是   | 资源名称                         |
| resourceType | string | 是   | 资源类型：MENU-菜单，BUTTON-按钮 |
| parentId     | number | 是   | 父级资源 ID，0 表示顶级          |
| path         | string | 否   | 路由路径                         |
| icon         | string | 否   | 图标标识                         |
| sortOrder    | number | 否   | 排序号                           |
| status       | number | 否   | 状态：1-启用，0-禁用            |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "资源创建成功",
  "data": {
    "resourceId": 15
  }
}
```

---

### 4.3 修改资源

更新指定资源信息，支持部分字段更新。

- **请求方法：** `PUT`
- **请求路径：** `/api/v1/system/resources/{resourceId}`
- **路径参数：** `resourceId` — 资源 ID
- **请求体：** JSON 格式，各字段均为可选

**请求体示例：**

```json
{
  "parentId": 5,
  "resourceName": "修改名称",
  "resourceType": "MENU",
  "path": "/updated-path",
  "icon": "NewIcon",
  "sortOrder": 5,
  "status": 1
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "更新成功",
  "data": null
}
```

---

### 4.4 删除资源

删除指定资源。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/resources/{resourceId}`
- **路径参数：** `resourceId` — 资源 ID
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "删除成功（若有子资源或被角色引用可能返回警告）",
  "data": null
}
```

---

## 5. 系统任务管理 (Tasks)

### 5.1 查询任务列表（分页 + 条件筛选）

查询任务列表，支持分页与条件筛选。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/tasks/list`
- **请求体：** 无（Query 参数）

**请求参数：**

| 参数名       | 类型      | 必填 | 说明                             |
|-----------|---------|----|--------------------------------|
| taskCode  | string  | 否  | 任务编码（模糊查询）                     |
| taskName  | string  | 否  | 任务名称（模糊查询）                     |
| status    | number  | 否  | 任务状态：0-待执行，1-执行中，2-成功，3-失败     |
| startTime | string  | 否  | 开始时间（格式：`yyyy-MM-ddTHH:mm:ss`） |
| endTime   | string  | 否  | 结束时间（格式：`yyyy-MM-ddTHH:mm:ss`） |
| pageNum   | integer | 否  | 页码，默认 1                        |
| pageSize  | integer | 否  | 每页条数，默认 10                     |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 2,
    "records": [
      {
        "taskId": 1,
        "taskCode": "2033725144572071170",
        "taskName": "异单账单采集任务_1",
        "taxNo": "91500000MA5U123456",
        "enterpriseName": "重庆某某科技有限公司",
        "status": 3,
        "createTime": "2026-03-17T10:00:33"
      },
      {
        "taskId": 2,
        "taskCode": "2033532254107541505",
        "taskName": "异单账单采集任务_2",
        "taxNo": "91500000MA5U123456",
        "enterpriseName": "重庆某某科技有限公司",
        "status": 2,
        "createTime": "2026-03-16T21:14:05"
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 5.2 新建任务

创建新任务。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/tasks/create`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "taskName": "示例税务采集任务",
  "processId": 2,
  "processVersion": "v1.0",
  "robotId": 1,
  "taxNo": "91500000MA50123456",
  "enterpriseName": "重庆某某科技有限公司",
  "priority": 5,
  "remark": ""
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "taskCode": "TASK_1773712834794"
  }
}
```

---

### 5.3 编辑任务

更新任务信息。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/tasks/update`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "taskId": 1,
  "taskName": "示例税务采集任务",
  "processId": 2,
  "processVersion": "v1.0",
  "robotId": 1,
  "taxNo": "91500000MA50123456",
  "enterpriseName": "重庆某某科技有限公司",
  "priority": 5,
  "remark": ""
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 5.4 查看任务详情

获取指定任务的详细信息。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/tasks/detail/{taskCode}`
- **路径参数：** `taskCode` — 任务编码，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "taskId": 1,
    "taskCode": "TASK_1773714041136",
    "taskName": "示例税务采集任务",
    "taxNo": "91500000MA50123456",
    "enterpriseName": "重庆某某科技有限公司",
    "processId": 2,
    "processCode": "测试流程002",
    "robotId": 1,
    "robotCode": "机器人001",
    "status": 2,
    "createTime": "2026-03-17T10:20:36",
    "startTime": "2026-03-17T11:00:24",
    "endTime": "2026-03-17T11:00:28"
  }
}
```

---

### 5.5 执行任务

执行指定任务。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/tasks/execute/{taskCode}`
- **路径参数：** `taskCode` — 任务编码，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "executionId": 2033789858201284609
  }
}
```

---

### 5.6 删除任务

删除指定任务。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/tasks/delete/{taskCode}`
- **路径参数：** `taskCode` — 任务编码，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

## 6. 机器人管理 (Robots)

### 6.1 查询机器人列表（含统计）

分页查询机器人列表，包含在线/离线/工作中等统计数据。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/robots/list`
- **请求体：** 无（Query 参数）

**请求参数：**

| 参数名      | 类型    | 必填 | 说明                             |
| ----------- | ------- | ---- | -------------------------------- |
| robotName   | string  | 否   | 机器人名称（模糊查询）           |
| robotCode   | string  | 否   | 机器人编码（精确查询）           |
| status      | number  | 否   | 状态：0-离线，1-在线            |
| pageNum     | integer | 否   | 页码，默认 1                    |
| pageSize    | integer | 否   | 每页条数，默认 10               |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "statistics": {
      "total": 3,
      "online": 2,
      "offline": 1
    },
    "total": 3,
    "records": [
      {
        "robotId": 1,
        "robotCode": "机器人001",
        "robotName": "机器人001",
        "robotType": "测试机器人",
        "status": 1,
        "currentTaskId": "全国",
        "lastHeartbeat": "2026-03-16T21:12:20",
        "createTime": "2026-03-16T21:09:34",
        "updateTime": "2026-03-16T21:12:29"
      },
      {
        "robotId": 2,
        "robotCode": "ROBOT_001",
        "robotName": "机器人-001",
        "robotType": "thread",
        "status": 1,
        "currentTaskId": "空闲",
        "lastHeartbeat": "2026-03-16T21:09:34",
        "createTime": "2026-03-16T21:09:34",
        "updateTime": "2026-03-16T21:09:34"
      },
      {
        "robotId": 3,
        "robotCode": "ROBOT_002",
        "robotName": "机器人-002",
        "robotType": "thread",
        "status": 0,
        "currentTaskId": "空闲",
        "lastHeartbeat": "2026-03-16T21:09:34",
        "createTime": "2026-03-16T21:09:34",
        "updateTime": "2026-03-16T21:09:34"
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 6.2 新增机器人

创建新机器人。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/robots/create`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "robotCode": "机器人A002",
  "robotName": "机器人A002",
  "robotType": "thread",
  "description": "",
  "status": 1
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "robotCode": "机器人A002"
  }
}
```

---

### 6.3 编辑机器人

更新机器人信息。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/robots/update`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "robotId": 1,
  "robotCode": "机器人A001",
  "robotName": "机器人A001",
  "robotType": "测试机器人",
  "description": "",
  "status": 1
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 6.4 查看机器人详情

获取指定机器人的详细信息。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/robots/detail/{robotCode}`
- **路径参数：** `robotCode` — 机器人编码，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "robotId": 1,
    "robotCode": "机器人001",
    "robotName": "机器人001",
    "robotType": "测试机器人",
    "description": "",
    "status": 1,
    "currentTaskId": "全国",
    "lastHeartbeat": "2026-03-16T21:12:20",
    "createTime": "2026-03-16T21:09:34",
    "updateTime": "2026-03-16T21:12:29"
  }
}
```

---

### 6.5 删除机器人

删除指定机器人。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/robots/delete/{robotCode}`
- **路径参数：** `robotCode` — 机器人编码，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

## 7. 流程管理 (Processes)

### 7.1 查询流程列表

分页查询流程列表，支持条件筛选。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/processes/list`
- **请求体：** 无（Query 参数）

**请求参数：**

| 参数名       | 类型    | 必填 | 说明                 |
| ------------ | ------- | ---- | -------------------- |
| processName  | string  | 否   | 流程名称（模糊查询） |
| processCode  | string  | 否   | 流程编码（精确查询） |
| status       | number  | 否   | 状态：1-启用，0-禁用 |
| pageNum      | integer | 否   | 页码，默认 1         |
| pageSize     | integer | 否   | 每页条数，默认 10    |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 2,
    "records": [
      {
        "processId": 1,
        "processCode": "测试流程002",
        "processName": "测试流程",
        "description": "",
        "stepCount": 5,
        "status": 1,
        "createTime": "2026-03-16T21:12:34",
        "updateTime": "2026-03-16T21:12:34"
      },
      {
        "processId": 2,
        "processCode": "PROCESS_001",
        "processName": "示例流程-仅Java步骤",
        "description": "每一步可绑定 Java/Groovy 爬虫代码片段",
        "stepCount": 1,
        "status": 1,
        "createTime": "2026-03-16T21:09:34",
        "updateTime": "2026-03-16T21:09:34"
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 7.2 新增流程

创建新流程。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/processes/create`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "processCode": "PROCESS_002",
  "processName": "新流程",
  "description": "",
  "status": 1
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "processCode": "PROCESS_002"
  }
}
```

---

### 7.3 编辑流程

更新流程信息。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/processes/update`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "processId": 1,
  "processCode": "测试流程002",
  "processName": "测试流程",
  "description": "",
  "status": 1
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 7.4 查看流程详情

获取指定流程的详细信息。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/processes/detail/{processCode}`
- **路径参数：** `processCode` — 流程编码，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "processId": 1,
    "processCode": "测试流程002",
    "processName": "测试流程",
    "description": "",
    "stepCount": 5,
    "status": 1,
    "createTime": "2026-03-16T21:12:34",
    "updateTime": "2026-03-16T21:12:34"
  }
}
```

---

### 7.5 查询流程步骤

查询指定流程下的步骤列表。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/processes/step/list/{processCode}`
- **路径参数：** `processCode` — 流程编码，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "stepOrder": 1,
      "stepName": "Java代码示例",
      "stepType": "Java爬虫代码",
      "codeContent": "return taxNo + \"_\" + enterpriseName;"
    }
  ]
}
```

---

### 7.6 保存流程步骤

保存流程下的步骤列表（全量覆盖）。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/processes/step/save`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "processCode": "测试流程002",
  "steps": [
    {
      "stepOrder": 1,
      "stepName": "采集(java)",
      "stepType": "Java爬虫代码",
      "codeContent": "return taxNo + \"_\" + enterpriseName;"
    },
    {
      "stepOrder": 2,
      "stepName": "解析(java)",
      "stepType": "Java爬虫代码",
      "codeContent": "..."
    }
  ]
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 7.7 删除流程

删除指定流程。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/processes/delete/{processCode}`
- **路径参数：** `processCode` — 流程编码，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

## 8. 执行记录管理 (Executions)

### 8.1 查询执行记录列表

分页查询执行记录列表，支持条件筛选。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/executions/list`
- **请求体：** 无（Query 参数）

**请求参数：**

| 参数名              | 类型    | 必填 | 说明                                          |
| ------------------- | ------- | ---- | --------------------------------------------- |
| taskId              | string  | 否   | 任务 ID（精确查询）                           |
| status              | number  | 否   | 执行状态：0-执行中，1-成功，2-失败           |
| startTime           | string  | 否   | 执行时间起始（格式：`yyyy-MM-ddTHH:mm:ss`）   |
| endTime             | string  | 否   | 执行时间结束（格式：`yyyy-MM-ddTHH:mm:ss`）   |
| pageNum             | integer | 否   | 页码，默认 1                                  |
| pageSize            | integer | 否   | 每页条数，默认 10                             |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 9,
    "records": [
      {
        "executionId": 2033789858201284699,
        "taskId": 1,
        "processId": 1,
        "robotId": 1,
        "status": 2,
        "startTime": "2026-03-17T14:17:43",
        "endTime": "2026-03-17T14:17:48",
        "duration": 4,
        "errorMsg": null
      },
      {
        "executionId": 2033740201102225410,
        "taskId": 2,
        "processId": 1,
        "robotId": 1,
        "status": 1,
        "startTime": "2026-03-17T11:00:24",
        "endTime": "2026-03-17T11:00:28",
        "duration": 4,
        "errorMsg": null
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 8.2 查看执行记录详情（含步骤）

查看指定执行记录的详情信息，包含步骤明细与每步输出。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/executions/detail/{executionId}`
- **路径参数：** `executionId` — 执行 ID，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "executionId": 2033740201102225410,
    "taskId": 1,
    "processId": 1,
    "robotId": 1,
    "status": 1,
    "duration": 4,
    "startTime": "2026-03-17T11:00:24",
    "endTime": "2026-03-17T11:00:28",
    "errorMsg": null,
    "stepLogs": [
      {
        "stepName": "采集(java)",
        "stepType": "java",
        "output": "{\"collectionId\":2033726326860492804}",
        "executeTime": "2026-03-17T11:00:24"
      },
      {
        "stepName": "解析(java)",
        "stepType": "java",
        "output": "{\"parsingId\":5}",
        "executeTime": "2026-03-17T11:00:26"
      },
      {
        "stepName": "加工(java)",
        "stepType": "java",
        "output": "{\"processingId\":5}",
        "executeTime": "2026-03-17T11:00:26"
      },
      {
        "stepName": "落库(java)",
        "stepType": "java",
        "output": "{\"queryId\":5}",
        "executeTime": "2026-03-17T11:00:27"
      }
    ]
  }
}
```

## 9. 数据采集管理 (RPA Data Collection)

### 9.1 采集数据列表（分页 + 筛选）

分页查询采集数据列表，支持按任务、关键字、状态与采集时间筛选。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/data/collection/list`
- **请求体：** 无（Query 参数）

**请求参数：**

| 参数名              | 类型    | 必填 | 说明                                          |
| ------------------- | ------- | ---- | --------------------------------------------- |
| taskId              | number  | 否   | 任务 ID（精确查询）                           |
| keyword             | string  | 否   | 纳税人识别号 / 企业名称（模糊查询）           |
| status              | string  | 否   | 状态：success/failed/pending                  |
| collectionTimeStart | string  | 否   | 采集时间起始                                  |
| collectionTimeEnd   | string  | 否   | 采集时间结束                                  |
| pageNum             | integer | 否   | 页码，默认 1                                  |
| pageSize            | integer | 否   | 每页条数，默认 10                             |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 16,
    "statistics": {
      "totalCollection": 16,
      "success": 12,
      "pending": 2,
      "failed": 2
    },
    "records": [
      {
        "collectionId": 2033726326860492804,
        "taskId": 1,
        "status": "success",
        "taxNo": "91500000MA5U123456",
        "enterpriseName": "重庆某某科技有限公司",
        "dataSource": "study-spider-demo",
        "collectionTime": "2026-03-17T16:05:52",
        "errorMsg": null
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 9.2 查看采集数据详情

查看指定采集数据的详细信息。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/data/collection/detail/{collectionId}`
- **路径参数：** `collectionId` — 采集数据 ID，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "collectionId": 2033726326860492804,
    "taskId": 1,
    "taxNo": "91500000MA5U123456",
    "enterpriseName": "重庆某某科技有限公司",
    "status": "success",
    "dataSource": "study-spider-demo",
    "collectionTime": "2026-03-17T16:05:52",
    "errorMsg": "",
    "rawData": "{\"source\":\"study-spider-demo\",\"site\":\"http://study.zmyfrank.com:18010/spider/home\"}"
  }
}
```

---

### 9.3 删除采集数据

删除指定采集数据记录。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/data/collection/delete/{collectionId}`
- **路径参数：** `collectionId` — 采集数据 ID，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

### 9.4 新增采集数据

新增采集数据记录。

- **请求方法：** `POST`
- **请求路径：** `/api/v1/system/data/collection/add`
- **请求体：** JSON 格式

**请求体示例：**

```json
{
  "taskId": 1,
  "taxNo": "91500000MA5U123456",
  "enterpriseName": "重庆某某科技有限公司",
  "status": "success",
  "dataSource": "study-spider-demo",
  "collectionTime": "2026-03-17T16:05:52",
  "errorMsg": "",
  "rawData": "{\"source\":\"study-spider-demo\",\"site\":\"http://study.zmyfrank.com:18010/spider/home\"}"
}
```

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "collectionId": 2033726326860492804
  }
}
```

---

## 10. 数据解析管理 (RPA Data Parsing)

### 10.1 解析数据列表（分页 + 筛选）

分页查询解析数据列表，支持按任务、状态与解析时间筛选。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/data/parsing/list`
- **请求体：** 无（Query 参数）

**请求参数：**

| 参数名            | 类型    | 必填 | 说明                                          |
| ----------------- | ------- | ---- | --------------------------------------------- |
| taskId            | number  | 否   | 任务 ID（精确查询）                           |
| status            | number  | 否   | 状态：0-待解析，1-已解析，2-解析失败          |
| parsingTimeStart  | string  | 否   | 解析时间起始                                  |
| parsingTimeEnd    | string  | 否   | 解析时间结束                                  |
| pageNum           | integer | 否   | 页码，默认 1                                  |
| pageSize          | integer | 否   | 每页条数，默认 10                             |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 15,
    "statistics": {
      "totalParsing": 15,
      "success": 10,
      "pending": 3,
      "failed": 2
    },
    "records": [
      {
        "parsingId": 11,
        "taskId": 1,
        "collectionId": 2033726326860492814,
        "status": 1,
        "extractedFields": 0,
        "parsingRule": null,
        "parsingTime": "2026-03-17T16:05:52",
        "errorMsg": null
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 10.2 查看解析数据详情

查看指定解析数据的详细信息。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/data/parsing/detail/{parsingId}`
- **路径参数：** `parsingId` — 解析数据 ID，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "parsingId": 11,
    "taskId": 1,
    "collectionId": 2033726326860492814,
    "taxNo": "91500000MA5U123456",
    "enterpriseName": "重庆某某科技有限公司",
    "status": 1,
    "extractedFields": 0,
    "parsingRule": null,
    "parsingTime": "2026-03-17T16:05:52",
    "errorMsg": "",
    "parsedData": "{\"enterpriseName\":\"重庆某某科技有限公司\",\"taxNo\":\"91500000MA5U123456\"}"
  }
}
```

---

### 10.3 删除解析数据

删除指定解析数据记录。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/data/parsing/delete/{parsingId}`
- **路径参数：** `parsingId` — 解析数据 ID，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```

---

## 11. 数据加工管理 (RPA Data Processing)

### 11.1 加工数据列表（分页 + 筛选）

分页查询加工数据列表，支持按任务、状态与加工时间筛选。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/data/processing/list`
- **请求体：** 无（Query 参数）

**请求参数：**

| 参数名               | 类型    | 必填 | 说明                                          |
| -------------------- | ------- | ---- | --------------------------------------------- |
| taskId               | number  | 否   | 任务 ID（精确查询）                           |
| status               | number  | 否   | 状态：0-待加工，1-已加工，2-加工失败          |
| processingTimeStart  | string  | 否   | 加工时间起始                                  |
| processingTimeEnd    | string  | 否   | 加工时间结束                                  |
| pageNum              | integer | 否   | 页码，默认 1                                  |
| pageSize             | integer | 否   | 每页条数，默认 10                             |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 15,
    "statistics": {
      "totalProcessing": 15,
      "success": 10,
      "pending": 3,
      "failed": 2
    },
    "records": [
      {
        "processingId": 5,
        "taskId": 1,
        "parsingId": 16,
        "status": 1,
        "processingTime": "2026-03-17T16:35:35",
        "errorMsg": null
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 11.2 查看加工数据详情

查看指定加工数据的详细信息。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/data/processing/detail/{processingId}`
- **路径参数：** `processingId` — 加工数据 ID，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "processingId": 5,
    "taskId": 1,
    "parsingId": 16,
    "taxNo": "91500000MA5U123456",
    "enterpriseName": "重庆某某科技有限公司",
    "status": 1,
    "processingTime": "2026-03-17T16:35:35",
    "errorMsg": "-",
    "processedData": "{\"indicatorCode\":\"study_invoice_sale_sum_1_12m\",\"enterpriseName\":\"重庆某某科技有限公司\"}",
    "verifyResult": "{\"invoiceTotal\":5,\"invoiceMatched\":0,\"matchedInvoices\":[]}"
  }
}
```

---

## 12. 数据查询管理 (RPA Data Query)

### 12.1 查询数据列表（分页 + 筛选）

分页查询可供展示/导出的结果数据列表，支持条件筛选。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/data/query/list`
- **请求体：** 无（Query 参数）

**请求参数：**

| 参数名            | 类型    | 必填 | 说明                                |
| ----------------- | ------- | ---- | ----------------------------------- |
| keyword           | string  | 否   | 纳税人识别号 / 企业名称（模糊查询） |
| taskId            | number  | 否   | 任务 ID（精确查询）                 |
| taxAreaId         | string  | 否   | 税区 ID（精确查询）                 |
| status            | number  | 否   | 数据状态：1-可用，0-不可用          |
| createTimeStart   | string  | 否   | 创建时间起始                        |
| createTimeEnd     | string  | 否   | 创建时间结束                        |
| pageNum           | integer | 否   | 页码，默认 1                        |
| pageSize          | integer | 否   | 每页条数，默认 10                   |

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "total": 15,
    "records": [
      {
        "queryId": 7,
        "taskId": 1,
        "taxNo": "91500000MA5U123456",
        "enterpriseName": "重庆某某科技有限公司",
        "taxAreaId": null,
        "status": 1,
        "createTime": "2026-03-17T16:35:35"
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  }
}
```

---

### 12.2 查看查询数据详情

查看指定查询数据的详细信息。

- **请求方法：** `GET`
- **请求路径：** `/api/v1/system/data/query/detail/{queryId}`
- **路径参数：** `queryId` — 查询数据 ID，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "queryId": 7,
    "taskId": 1,
    "taxNo": "91500000MA5U123456",
    "enterpriseName": "重庆某某科技有限公司",
    "taxAreaId": null,
    "status": 1,
    "createTime": "2026-03-17T16:35:35",
    "businessData": "{\"source\":\"study-spider-demo\",\"indicatorCode\":\"study_invoice_sale_sum_1_12m\"}"
  }
}
```

---

### 12.3 删除查询数据

删除指定查询数据记录。

- **请求方法：** `DELETE`
- **请求路径：** `/api/v1/system/data/query/delete/{queryId}`
- **路径参数：** `queryId` — 查询数据 ID，必填
- **请求体：** 无

**响应示例 (200 OK)：**

```json
{
  "code": 200,
  "msg": "success",
  "data": null
}
```
