# 运维系统(Ops-Sys)

这是一个用于系统运维管理的Web应用系统，基于Spring Boot和Vue 3开发。

## 项目概述

运维系统(Ops-Sys)是一个现代化的Web应用系统，旨在提供系统运维管理的解决方案。系统采用前后端分离的架构设计，后端使用Spring Boot框架，前端使用Vue 3技术栈。

## 技术栈

### 后端技术栈
- 核心框架：Spring Boot 3.x
- ORM框架：MyBatis
- 数据库：MySQL 8.0
- 项目管理：Maven
- 安全认证：JWT
- 日志框架：SLF4J + Logback

### 前端技术栈
- 核心框架：Vue 3.x
- 构建工具：Vite
- UI组件库：Element Plus
- 路由管理：Vue Router
- HTTP客户端：Axios
- 代码规范：ESLint

## 项目结构

### 后端结构(backend)
```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ops/
│   │   │       └── example/
│   │   │           └── backend/
│   │   │               ├── controller/    # 控制器层
│   │   │               ├── service/       # 服务层
│   │   │               ├── mapper/        # 数据访问层
│   │   │               ├── entity/        # 实体类
│   │   │               ├── common/        # 公共组件
│   │   │               ├── config/        # 配置类
│   │   │               ├── exception/     # 异常处理
│   │   │               └── utils/         # 工具类
│   │   └── resources/
│   │       ├── mapper/                   # MyBatis映射文件
│   │       └── application.yml           # 应用配置文件
│   └── test/                             # 测试代码
└── pom.xml                               # Maven配置文件
```

### 前端结构(frontend)
```
frontend/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   ├── views/           # 页面视图
│   ├── router/          # 路由配置
│   ├── utils/           # 工具函数
│   └── App.vue          # 根组件
├── public/              # 公共资源
└── package.json         # 项目依赖配置
```

## 已实现功能

### 1. 用户管理模块
- [x] 用户注册与登录
- [x] 用户信息管理
  - [x] 基本信息修改
  - [x] 密码修改
  - [x] 头像上传
- [x] 权限控制
  - [x] JWT token认证
  - [x] 角色区分（超级管理员/普通管理员）
- [x] 用户列表管理
  - [x] 分页查询
  - [x] 条件筛选
  - [x] 批量删除

### 2. 系统功能
- [x] 全局异常处理
- [x] 统一响应格式
- [x] 跨域配置
- [x] 日志记录

## 数据库设计

### 用户表(user)
| 字段名      | 类型            | 描述           | 约束          |
|------------|----------------|---------------|--------------|
| id         | Integer        | 主键ID         | PRIMARY KEY  |
| username   | varchar(50)    | 用户名         | UNIQUE, NOT NULL |
| password   | varchar(100)   | 密码           | NOT NULL     |
| nickname   | varchar(50)    | 昵称           |              |
| email      | varchar(100)   | 邮箱           |              |
| phone      | varchar(20)    | 电话           |              |
| avatar     | varchar(200)   | 头像           |              |
| status     | varchar(10)    | 状态           | DEFAULT '启用'  |
| create_time| datetime       | 创建时间        | NOT NULL     |
| update_time| datetime       | 更新时间        | NOT NULL     |
| deleted    | Integer        | 是否删除标记     | DEFAULT 0    |
| role       | varchar(20)    | 角色           | DEFAULT 'USER' |

### 超级管理员表(sys_user)
| 字段名      | 类型            | 描述           | 约束          |
|------------|----------------|---------------|--------------|
| id         | Integer        | 主键ID         | PRIMARY KEY  |
| username   | varchar(50)    | 用户名         | UNIQUE, NOT NULL |
| password   | varchar(100)   | 密码           | NOT NULL     |
| nickname   | varchar(50)    | 昵称           |              |
| email      | varchar(100)   | 邮箱           |              |
| phone      | varchar(20)    | 电话           |              |
| avatar     | varchar(200)   | 头像           |              |
| status     | varchar(10)    | 状态           | DEFAULT '启用'  |
| create_time| datetime       | 创建时间        | NOT NULL     |
| update_time| datetime       | 更新时间        | NOT NULL     |
| deleted    | Integer        | 是否删除标记     | DEFAULT 0    |
| role       | varchar(20)    | 角色           | DEFAULT 'ADMIN' |

## API接口文档

### 用户管理接口

#### 1. 用户注册
- 请求路径：`POST /register`
- 请求参数：
```json
{
    "username": "string",
    "password": "string",
    "email": "string",
    "phone": "string"
}
```
- 响应结果：
```json
{
    "code": "200",
    "msg": "success",
    "data": null
}
```

#### 2. 用户登录
- 请求路径：`POST /login`
- 请求参数：
```json
{
    "username": "string",
    "password": "string",
    "role": "string"  // ADMIN 或 USER
}
```
- 响应结果：
```json
{
    "code": "200",
    "msg": "success",
    "data": {
        "id": "integer",
        "username": "string",
        "nickname": "string",
        "token": "string",
        "role": "string"
    }
}
```

#### 3. 修改密码
- 请求路径：`PUT /user/updatePassword`
- 请求参数：
```json
{
    "id": "integer",
    "oldPassword": "string",
    "password": "string"
}
```
- 响应结果：
```json
{
    "code": "200",
    "msg": "success",
    "data": null
}
```

## 部署说明

### 后端部署
1. 环境要求：
   - JDK 17+
   - Maven 3.6+
   - MySQL 8.0+

2. 部署步骤：
```bash
# 1. 克隆项目
git clone [项目地址]

# 2. 进入项目目录
cd backend

# 3. 编译打包
mvn clean package

# 4. 运行项目
java -jar target/ops-sys.jar
```

### 前端部署
1. 环境要求：
   - Node.js 16+
   - npm 8+

2. 部署步骤：
```bash
# 1. 克隆项目
git clone [项目地址]

# 2. 进入项目目录
cd frontend

# 3. 安装依赖
npm install

# 4. 开发环境运行
npm run dev

# 5. 生产环境构建
npm run build
```

## 开发规范

### 代码规范
1. 后端代码规范
   - 遵循阿里巴巴Java开发手册
   - 使用统一的代码格式化工具
   - 类名使用大驼峰命名法
   - 方法名使用小驼峰命名法
   - 常量使用全大写，下划线分隔

2. 前端代码规范
   - 遵循Vue.js风格指南
   - 使用ESLint进行代码检查
   - 组件名使用大驼峰命名法
   - 变量名使用小驼峰命名法
   - 常量使用全大写，下划线分隔

## 常见问题及解决方案

### 1. 数据库连接问题
- 问题：无法连接到MySQL数据库
- 解决方案：
  1. 检查数据库配置是否正确
  2. 确认数据库服务是否启动
  3. 检查网络连接是否正常
  4. 验证数据库用户名和密码

### 2. 前端跨域问题
- 问题：前端请求后端API时出现跨域错误
- 解决方案：
  1. 在后端配置CORS
  2. 使用代理服务器
  3. 配置Nginx反向代理

### 3. JWT认证问题
- 问题：token验证失败
- 解决方案：
  1. 检查token是否正确传递
  2. 验证token是否过期
  3. 确认token签名是否正确
  4. 检查用户角色权限

## 项目维护

### 日常维护
1. 定期检查系统日志
2. 监控系统性能
3. 及时更新依赖包
4. 定期备份数据

### 版本更新
1. 遵循语义化版本规范
2. 更新前做好备份
3. 测试环境验证
4. 灰度发布 