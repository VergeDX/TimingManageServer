# TimingManageServer
一个 “时间管理” 服务器 API，方便规划个人作息

## 部署
1. 请去 [releases](https://github.com/VergeDX/TimingManageServer/releases) 下载最新版本的 jar 包
2. 命令行执行 `java -jar TimingManageServer-x.x.x-jar-with-dependencies.jar`
3. 服务器即在 `http://localhost:2333` 启动

## Api 节点与功能
请求参数：

|   字段   |   字段含义   |  说明  |
|  -------  |  -------  |  -------  |
|  `username`  |  用户名  |  长度 < 32，字母 + 数字 + 下划线，大小写敏感  |
|  `password`  |  用户密码  |  只会保存 Md5 后的用户密码  |
|  `userAccessToken`  |  用户访问 Token  |  /login 时会根据时间生成一个新的 Token，旧 Token 作废  |
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
#### 返回值：`userAccessToken`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户名（username）或密码（password）参数  
2. 用户名太长，它只能小于 32 个字符  
3. 用户名不合法，它必须由字母、数字或下划线组成  
4. 该用户名已被占用

### 用户登录：`/login`
#### 参数：`username` & `password`
#### 返回值：`userAccessToken`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户名（username）或密码（password）参数
2. 用户名或密码错误。请注意，用户名区分大小写

### 获取用户信息：`/getUserInfo`
#### 参数：`userAccessToken`
#### 返回值：`User 对象的 Json`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户访问 Token（userAccessToken）参数
2. 用户访问 Token 不存在

### 开启事件：`/startEvent`
#### 参数：`userAccessToken`
#### 返回值：`eventToken`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户访问 Token（userAccessToken）参数
2. 用户访问 Token 不存在
3. 你有未完成的事件，请先结束该事件（/endEvent）

### 结束事件：`/endEvent`
#### 参数：`userAccessToken`
#### 返回值：`Event 对象的 Json`
以下情况将返回错误信息：
1. 请求头（Header）中没有用户访问 Token（userAccessToken）参数
2. 用户访问 Token 不存在
3. 你没有未完成的事件，请先开始一个事件（/startEvent）

### 移除事件：`/removeEvent`
#### 参数：`eventToken` & `userAccessToken`
#### 返回值：`"事件已移除"`
以下情况将返回错误信息：
1. 请求头（Header）中没有事件 Token（eventToken）或用户访问 Token（userAccessToken）参数
2. 事件 Token 或用户访问 Token 不存在/不匹配，您可能未结束该事件（/endEvent）

### 设置事件说明：`/setDescription`
#### 参数：`eventToken` & `description`
#### 返回值：`Event 对象的 Json`
以下情况将返回错误信息：
1. 请求头（Header）中没有事件 Token（eventToken）或事件说明（description）参数
2. 事件 Token 不存在，您可能没有结束该事件（/endEvent）

### 获取事件信息：`/getEventInfo`
#### 参数：`eventToken`
#### 返回值：`Event 对象的 Json`
以下情况将返回错误信息：
1. 请求头（Header）中没有事件 Token（eventToken）参数
2. 事件 Token 未找到，您可能没有结束该事件（/endEvent）