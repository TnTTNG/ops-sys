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

## 设计思路

### 1. 数据表设计

#### 1.1 系统监控表(system_monitor)
| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | Integer | 主键ID | PRIMARY KEY |
| instance_id | varchar(50) | 实例ID | NOT NULL |
| cpu_usage | decimal(5,2) | CPU使用率(%) | NOT NULL |
| cpu_credit_balance | decimal(10,2) | 突发性能实例积分总数 | |
| cpu_credit_usage | decimal(10,2) | 突发性能实例已使用的积分数 | |
| cpu_notpaid_surplus_credit_usage | decimal(10,2) | 超额未支付积分 | |
| cpu_advance_credit_balance | decimal(10,2) | 超额积分 | |
| bps_read | bigint | 云盘读带宽(Byte/s) | |
| bps_write | bigint | 云盘写带宽(Byte/s) | |
| iops_read | bigint | 云盘读IOPS(次/s) | |
| iops_write | bigint | 云盘写IOPS(次/s) | |
| internet_bandwidth | bigint | 公网带宽(kbits/s) | |
| intranet_bandwidth | bigint | 内网带宽(kbits/s) | |
| internet_rx | bigint | 公网入流量(kbits) | |
| internet_tx | bigint | 公网出流量(kbits) | |
| intranet_rx | bigint | 内网入流量(kbits) | |
| intranet_tx | bigint | 内网出流量(kbits) | |
| monitor_time | datetime | 监控时间 | NOT NULL |
| create_time | datetime | 创建时间 | NOT NULL |
| update_time | datetime | 更新时间 | NOT NULL |

#### 1.2 告警规则表(alert_rule)
| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | Integer | 主键ID | PRIMARY KEY |
| rule_name | varchar(100) | 规则名称 | NOT NULL |
| metric_type | varchar(50) | 指标类型 | NOT NULL |
| threshold | decimal(10,2) | 阈值 | NOT NULL |
| operator | varchar(10) | 操作符 | NOT NULL |
| status | varchar(10) | 状态 | DEFAULT '启用' |
| create_time | datetime | 创建时间 | NOT NULL |
| update_time | datetime | 更新时间 | NOT NULL |
| create_by | Integer | 创建人 | NOT NULL |
| update_by | Integer | 更新人 | NOT NULL |

#### 1.3 告警记录表(alert_record)
| 字段名 | 类型 | 描述 | 约束 |
|--------|------|------|------|
| id | Integer | 主键ID | PRIMARY KEY |
| rule_id | Integer | 规则ID | NOT NULL |
| server_id | varchar(50) | 服务器ID | NOT NULL |
| alert_level | varchar(20) | 告警级别 | NOT NULL |
| alert_content | text | 告警内容 | NOT NULL |
| alert_time | datetime | 告警时间 | NOT NULL |
| status | varchar(10) | 处理状态 | DEFAULT '未处理' |
| handle_time | datetime | 处理时间 | |
| handle_by | Integer | 处理人 | |
| create_time | datetime | 创建时间 | NOT NULL |
| update_time | datetime | 更新时间 | NOT NULL |

### 2. 需要新增的文件

#### 2.1 后端文件
```
backend/src/main/java/ops/example/backend/
├── entity/
│   ├── SystemMonitor.java
│   ├── AlertRule.java
│   └── AlertRecord.java
├── mapper/
│   ├── SystemMonitorMapper.java
│   ├── AlertRuleMapper.java
│   └── AlertRecordMapper.java
├── service/
│   ├── SystemMonitorService.java
│   ├── AlertRuleService.java
│   └── AlertRecordService.java
├── controller/
│   ├── SystemMonitorController.java
│   ├── AlertRuleController.java
│   └── AlertRecordController.java
└── resources/
    └── mapper/
        ├── SystemMonitorMapper.xml
        ├── AlertRuleMapper.xml
        └── AlertRecordMapper.xml
```

#### 2.2 前端文件
```
frontend/src/
├── views/
│   ├── monitor/
│   │   ├── SystemMonitor.vue
│   │   ├── AlertRule.vue
│   │   └── AlertRecord.vue
│   └── components/
│       ├── MonitorChart.vue
│       └── AlertForm.vue
└── router/
    └── index.js (需要添加新路由)
```

### 3. 需要修改的文件

#### 3.1 后端文件
1. `backend/src/main/resources/application.yml`
   - 添加监控相关配置
   - 添加告警相关配置

2. `backend/src/main/java/ops/example/backend/config/WebSocketConfig.java`
   - 添加监控数据推送配置

3. `backend/src/main/java/ops/example/backend/websocket/WebSocketServer.java`
   - 添加监控数据推送方法

#### 3.2 前端文件
1. `frontend/src/router/index.js`
   - 添加监控和告警相关路由

2. `frontend/src/utils/websocket.js`
   - 添加监控数据接收处理

3. `frontend/src/views/Manager.vue`
   - 添加监控和告警菜单项

### 4. 主要功能实现

#### 4.1 系统监控
1. 定时采集服务器性能数据
   - 使用阿里云SDK调用DescribeInstanceMonitorData接口
   - 支持自定义时间范围和采集周期
   - 处理接口限制（最多400条数据，30天内）
   - 异常实例状态处理
2. 数据可视化展示
   - CPU使用率趋势图
   - 网络流量分析图
   - 磁盘IO性能图
   - 突发性能实例积分监控
3. 历史数据查询
   - 支持多条件组合查询
   - 分页展示
   - 数据导出
4. 数据导出功能
   - 支持CSV格式导出
   - 支持自定义时间范围
   - 支持选择导出字段

#### 4.2 告警管理
1. 告警规则配置
2. 告警触发处理
3. 告警通知（邮件、短信）
4. 告警记录查询

#### 4.3 实时监控
1. WebSocket实时推送
2. 性能指标实时展示
3. 告警实时通知

### 5. 技术要点

1. 使用WebSocket实现实时数据推送
2. 使用ECharts实现数据可视化
3. 使用定时任务采集监控数据
   - 使用@Scheduled注解实现定时任务
   - 使用线程池处理并发请求
   - 实现失败重试机制
4. 使用线程池处理告警通知
5. 使用Redis缓存监控数据
   - 缓存最近24小时数据
   - 实现数据过期策略
6. 阿里云SDK集成
   - 处理接口调用限制
   - 实现异常重试机制
   - 优化请求频率

### 6. 注意事项

1. 监控数据采集频率控制
   - 遵循阿里云接口限制
   - 避免频繁调用
   - 合理设置采集周期
2. 数据库性能优化
   - 建立合适的索引
   - 实现数据归档策略
   - 优化查询性能
3. 告警规则合理性验证
   - 考虑实例类型差异
   - 设置合理的阈值
   - 避免告警风暴
4. 系统资源占用控制
   - 控制并发请求数
   - 优化内存使用
   - 实现资源限流
5. 数据安全性保护
   - 加密敏感数据
   - 实现访问控制
   - 记录操作日志
6. 阿里云接口限制处理
   - 处理400条数据限制
   - 处理30天时间限制
   - 处理实例状态异常 