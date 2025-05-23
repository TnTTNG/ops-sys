# 运维系统(Ops-Sys)

这是一个用于系统运维管理的Web应用系统，基于Spring Boot和MyBatis框架开发。

## 项目概述

运维系统(Ops-Sys)是一个现代化的Web应用系统，旨在提供系统运维管理的解决方案。系统采用前后端分离的架构设计，后端使用Spring Boot框架，前端使用现代化的Web技术栈。

## 技术栈

### 后端技术栈
- 核心框架：Spring Boot 2.x
- ORM框架：MyBatis
- 数据库：MySQL 5.7+
- 项目管理：Maven
- 接口文档：Swagger
- 安全框架：Spring Security
- 缓存：Redis
- 消息队列：RabbitMQ

### 前端技术栈
- 核心框架：Vue 3.x
- 构建工具：Vite
- UI组件库：Element Plus
- 状态管理：Vuex
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
│   │   │   └── com/
│   │   │       └── ops/
│   │   │           ├── controller/    # 控制器层
│   │   │           ├── service/       # 服务层
│   │   │           ├── dao/          # 数据访问层
│   │   │           ├── entity/       # 实体类
│   │   │           ├── dto/          # 数据传输对象
│   │   │           ├── vo/           # 视图对象
│   │   │           ├── config/       # 配置类
│   │   │           ├── exception/    # 异常处理
│   │   │           └── utils/        # 工具类
│   │   └── resources/
│   │       ├── mapper/              # MyBatis映射文件
│   │       ├── application.yml      # 应用配置文件
│   │       └── application-dev.yml  # 开发环境配置
│   └── test/                        # 测试代码
└── pom.xml                          # Maven配置文件
```

### 前端结构(frontend)
```
frontend/
├── src/
│   ├── assets/          # 静态资源
│   ├── components/      # 公共组件
│   ├── views/           # 页面视图
│   ├── router/          # 路由配置
│   ├── store/           # 状态管理
│   ├── api/             # API接口
│   ├── utils/           # 工具函数
│   └── App.vue          # 根组件
├── public/              # 公共资源
└── package.json         # 项目依赖配置
```

## 功能模块

### 1. 用户管理模块
- 用户注册与登录
- 用户信息管理
- 权限控制
- 角色管理

### 2. 系统监控模块
- 服务器状态监控
- 应用性能监控
- 日志管理
- 告警管理

### 3. 运维管理模块
- 服务器管理
- 应用部署
- 配置管理
- 任务调度

## 数据库设计

### 用户表(sys_user)
| 字段名      | 类型            | 描述           | 约束          |
|------------|----------------|---------------|--------------|
| id         | Integer        | 主键ID         | PRIMARY KEY  |
| username   | varchar(50)    | 用户名         | UNIQUE, NOT NULL |
| password   | varchar(100)   | 密码           | NOT NULL     |
| nickname   | varchar(50)    | 昵称           |              |
| email      | varchar(100)   | 邮箱           |              |
| phone      | varchar(20)    | 电话           |              |
| avatar     | varchar(200)   | 头像           |              |
| status     | varchar(10)    | 状态           | DEFAULT '1'  |
| create_time| datetime       | 创建时间        | NOT NULL     |
| update_time| datetime       | 更新时间        | NOT NULL     |
| deleted    | Integer        | 是否删除标记     | DEFAULT 0    |

## API接口文档

### 用户管理接口

#### 1. 用户注册
- 请求路径：`POST /api/v1/admin/register`
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
    "code": 200,
    "message": "success",
    "data": {
        "id": "integer",
        "username": "string"
    }
}
```

#### 2. 用户登录
- 请求路径：`POST /api/v1/admin/login`
- 请求参数：
```json
{
    "username": "string",
    "password": "string"
}
```
- 响应结果：
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "string",
        "userInfo": {
            "id": "integer",
            "username": "string",
            "nickname": "string"
        }
    }
}
```

## 部署说明

### 后端部署
1. 环境要求：
   - JDK 1.8+
   - Maven 3.6+
   - MySQL 5.7+
   - Redis 5.0+

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
   - Node.js 14+
   - npm 6+

2. 部署步骤：
```bash
# 1. 克隆项目
git clone [项目地址]

# 2. 进入项目目录
cd frontend

# 3. 安装依赖
npm install

# 4. 构建项目
npm run build

# 5. 部署到Web服务器
# 将dist目录下的文件部署到Web服务器
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

### Git提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试相关
- chore: 构建过程或辅助工具的变动

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

### 3. 性能优化建议
1. 数据库优化
   - 合理使用索引
   - 优化SQL查询
   - 使用数据库连接池

2. 前端优化
   - 使用路由懒加载
   - 组件按需加载
   - 合理使用缓存

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

## 联系方式

如有任何问题，请联系：
- 邮箱：[项目维护者邮箱]
- 电话：[项目维护者电话] 