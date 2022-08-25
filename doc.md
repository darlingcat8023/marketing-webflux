## 奇妙围棋课对接平台方案

### 1 整体流程

```mermaid
sequenceDiagram
    
    participant A as 用户端
    participant B as 课程平台
    participant C as 围棋课后台

    A ->> B: 1.发起跳转
    B ->> C: 2.请求token
    C -->> B: 
    B -->> A: 3.将token参数拼在跳转连接中
    A ->> C: 4.跳转
    C -->> A: 5.认证鉴权
    
```

流程说明：

- 奇妙围棋课与课程平台对接时，都会以课节形式提供跳转连接，有多少课节就有多少跳转连接

- 每次发起跳转时都要使用新的token，原因是围棋课后台认证时会与token绑定客户端的ip地址



如果第三方平台有自己的鉴权机制也可以使用第三方平台的token，流程可以简化如下

```mermaid
sequenceDiagram
    
    participant A as 用户端
    participant B as 课程平台
    participant C as 围棋课后台

    A ->> B: 1.发起跳转
    B -->> A: 2.将token参数拼在跳转连接中
    A ->> C: 3.跳转
    C -->> A: 4.认证鉴权
    
```

**使用三方平台的token时需要第三方提供鉴权逻辑**

### 2 接口文档

围棋课后台不区分测试和线上环境，提供的接口和地址可以直接用来进行第三方联调和测试以及第三方线上环境的配置

#### 2.1 获取token

接口地址：http://togqmwqkht.goelfin.com/api/client/token/generate

请求方式：GET

请求参数：

| 参数名 | 参数位置 | 参数说明 |                    |
| ------ | -------- | -------- | ------------------ |
| source | url参数  | 平台编号 | 对接时分配平台编号 |

请求示例：

http://togqmwqkht.goelfin.com/api/client/token/generate?source=1

正常响应(HTTPCode = 200):

```
eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJud3B3cWRjIiwidGltZVN0YW1wIjoxNjYwNTMwOTg2MDAzfQ.4BcF1DlX7PpPoVmPY9pldCFbFlO3vUawTql-UqyJfjo
```

错误响应(HTTPCode = 400):

```
error message
```

#### 2.2 课节跳转地址

请求示例：http://togqmwqk.goelfin.com/frame/jump.html?source=1&course=1&token=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJud3B3cWRjIiwidGltZVN0YW1wIjoxNjYwNTMwOTg2MDAzfQ.4BcF1DlX7PpPoVmPY9pldCFbFlO3vUawTql-UqyJfjo



其中` http://togqmwqk.goelfin.com/frame/jump.html?source=1&course=1`部分由我们提供，第三方需要将token拼在路径中完成跳转

`source`参数需要与获取token时的入参保持一致
