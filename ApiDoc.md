## ApiDoc
请求参数：

|   字段   |   字段含义   |  说明  |
|  -------  |  -------  |  -------  |
|  `username`  |  用户名  |  长度 < 32，字母 + 数字 + 下划线，大小写敏感  |
|  `password`  |  用户密码  |  只会保存 Md5 后的用户密码  |
|  `eventToken`  |  事件 Token  |  /startEvent 和 /endEvent 成功后均会返回，它不可更改  |
|  `description`  |  事件说明  |  已完成事件的描述，可由用户自定义  |

请求方法均为 `GET`，参数应放在 `Header`里  
返回 Json 的格式：

`````Json
{
"status": "OK / ERROR",
"info": "错误信息 或 请求到的内容"
}
`````

### 用户注册：`/register`
#### 参数：`username` & `password`
#### 返回值：`“注册成功”`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户名（username）或密码（password）参数  
2. 用户名太长，它只能小于 32 个字符  
3. 用户名不合法，它必须由字母、数字或下划线组成  
4. 该用户名已被占用

### 用户登录：`/login`
#### 参数：`username` & `password`
#### 返回值：`用户对象 Json`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户名（username）或密码（password）参数
2. 用户名或密码错误。请注意区分大小写

### 开启事件：`/startEvent`
#### 参数：`username` & `password`
#### 返回值：`eventToken`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户名（username）或密码（password）参数
2. 用户名或密码错误。请注意区分大小写
3. 你有未完成的事件，请先结束该事件（/endEvent）

### 结束事件：`/endEvent`
#### 参数：`username` & `password`
#### 返回值：`Event 对象的 Json`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户名（username）或密码（password）参数
2. 用户名或密码错误。请注意区分大小写
3. 你没有未完成的事件，请先开始一个事件（/startEvent）

### 移除事件：`/removeEvent`
#### 参数：`username` & `password` & `eventToken`
#### 返回值：`“事件已移除”`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户名（username）、密码（password）或事件 Token（eventToken）参数
2. 用户名或密码错误
3. 你没有这个事件

### 设置事件说明：`/setEventDescription`
#### 参数：`eventToken` & `description`
#### 返回值：`Event 对象的 Json`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户名（username）、密码（password）、事件 Token（eventToken）或事件说明（description）参数
2. 用户名或密码错误。请注意区分大小写
3. 事件 Token 不存在或不属于你，您可能没有结束该事件（/endEvent）

### 获取事件信息：`/getEventInfo`
#### 参数：`eventToken`
#### 返回值：`Event 对象的 Json`
以下情况将返回错误信息：
1. 请求头（Header）中没有事件 Token（eventToken）参数
2. 事件 Token 未找到，您可能没有结束该事件（/endEvent）
