# 无需 Docker 部署方案

由于 Docker 网络问题，提供以下替代方案：

## 方案一：一键启动（推荐）

### 前置要求
1. 安装 Java JDK 8 或更高版本
2. 安装 MySQL 8.0
3. 安装 Redis（Windows 版本）

### 部署步骤

1. **导入数据库**
   - 打开 MySQL，创建数据库 `admission_db`
   - 导入 `sql/admission_db_complete.sql`

2. **修改后端配置**
   编辑 `admission-backend/src/main/resources/application.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/admission_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
       username: root
       password: 你的密码
     redis:
       host: localhost
       port: 6379
   ```

3. **运行一键启动脚本**
   ```
   双击运行：start-without-docker.bat
   ```

## 方案二：最简部署（无需 Redis）

如果 Redis 安装麻烦，可以禁用 Redis：

修改 `application.yml`：
```yaml
spring:
  redis:
    host: localhost
    port: 6379
  cache:
    type: simple  # 改为简单缓存，不使用 Redis
```

## 方案三：使用 H2 内存数据库（开发测试用）

如果只是想快速演示，可以将数据库改为 H2（无需安装 MySQL）：

1. 在 `pom.xml` 中添加 H2 依赖
2. 修改 `application.yml` 使用 H2 数据库
3. 重新编译

**注意**：H2 是内存数据库，重启后数据会丢失，仅适合演示。

## 文件清单

发给对方时需要包含：
```
admission-system/
├── admission-backend/
│   ├── target/
│   │   └── admission-backend-1.0.0.jar  ⭐ 编译好的后端
│   └── src/main/resources/application.yml  ⭐ 需要修改配置
├── admission-frontend/
│   └── dist/                              ⭐ 编译好的前端
├── sql/
│   └── admission_db_complete.sql          ⭐ 数据库脚本
├── start-without-docker.bat               ⭐ 一键启动脚本
└── NO_DOCKER_README.md                    ⭐ 本说明文件
```

## 常见问题

### 1. 端口被占用
- 后端默认端口 8080
- MySQL 默认端口 3306
- Redis 默认端口 6379

如果端口被占用，修改 `application.yml` 中的端口配置。

### 2. 数据库连接失败
- 检查 MySQL 是否启动
- 检查用户名密码是否正确
- 检查数据库 `admission_db` 是否已创建

### 3. 前端无法访问后端
- 检查后端是否启动成功
- 检查 `application.yml` 中的 CORS 配置
- 浏览器按 F12 查看具体错误

## 联系方式

如有问题，请检查：
1. Java 版本是否正确
2. MySQL 和 Redis 是否已启动
3. 配置文件是否正确
