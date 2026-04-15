# Docker 部署说明

## 项目简介

录取数据分析系统的 Docker 一键部署方案，包含：
- **前端**: Vue 3 + Element Plus + ECharts
- **后端**: Spring Boot + MyBatis-Plus + JWT
- **数据库**: MySQL 8.0
- **缓存**: Redis 7

## 快速开始

### 前置要求

1. 安装 Docker Desktop
   - Windows/Mac: [下载 Docker Desktop](https://www.docker.com/products/docker-desktop)
   - Linux: 安装 Docker 和 Docker Compose

2. 确保 Docker 服务正在运行

### 一键部署

#### Windows

双击运行 `docker-deploy.bat`

#### Linux/Mac

```bash
chmod +x docker-deploy.sh
./docker-deploy.sh
```

#### 手动部署

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端页面 | http://localhost | 默认账号: admin/admin |
| 后端 API | http://localhost:8080 | RESTful API |
| MySQL | localhost:3306 | root/123456 |
| Redis | localhost:6379 | 无密码 |

## 项目结构

```
admission-system/
├── admission-backend/          # 后端项目
│   ├── Dockerfile              # 后端镜像构建文件
│   └── src/                    # 源代码
├── admission-frontend/         # 前端项目
│   ├── Dockerfile              # 前端镜像构建文件
│   ├── nginx.conf              # Nginx 配置
│   └── src/                    # 源代码
├── sql/
│   └── admission_db_complete.sql  # 数据库初始化脚本
├── docker-compose.yml          # Docker 编排配置
├── docker-deploy.bat           # Windows 部署脚本
├── docker-deploy.sh            # Linux/Mac 部署脚本
└── DOCKER_README.md           # 本说明文件
```

## 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 停止并删除数据卷（清空数据库）
docker-compose down -v

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f

# 查看指定服务日志
docker-compose logs -f backend

# 进入容器
docker exec -it admission-backend bash
docker exec -it admission-mysql mysql -uroot -p123456

# 重新构建并启动
docker-compose up -d --build
```

## 数据持久化

- **MySQL 数据**: 存储在 Docker 卷 `admission-system_mysql_data` 中
- **Redis 数据**: 存储在 Docker 卷 `admission-system_redis_data` 中

数据不会因为容器重启而丢失，除非执行 `docker-compose down -v`。

## 环境变量配置

如需修改配置，编辑 `docker-compose.yml` 中的环境变量：

### 数据库配置
```yaml
mysql:
  environment:
    MYSQL_ROOT_PASSWORD: your_password  # 修改 root 密码
    MYSQL_DATABASE: admission_db
```

### 后端配置
```yaml
backend:
  environment:
    SPRING_DATASOURCE_PASSWORD: your_password  # 与上面一致
```

## 故障排查

### 1. 端口冲突

如果 3306、6379、8080 或 80 端口被占用，修改 `docker-compose.yml`：

```yaml
ports:
  - "3307:3306"  # 将主机端口改为 3307
```

### 2. 内存不足

Docker 默认内存可能不足，建议在 Docker Desktop 设置中分配至少 4GB 内存。

### 3. 数据库初始化失败

如果数据库初始化失败，可以手动执行 SQL：

```bash
# 进入 MySQL 容器
docker exec -it admission-mysql mysql -uroot -p123456

# 在 MySQL 中执行
source /docker-entrypoint-initdb.d/01-init.sql
```

### 4. 查看详细日志

```bash
# 后端日志
docker-compose logs backend

# 前端日志
docker-compose logs frontend

# MySQL 日志
docker-compose logs mysql
```

## 生产环境部署建议

1. **修改默认密码**: 务必修改 MySQL 和系统默认账号密码
2. **使用 HTTPS**: 配置 SSL 证书
3. **限制端口暴露**: 只暴露必要的端口
4. **定期备份**: 备份数据库数据卷
5. **资源限制**: 为容器设置 CPU 和内存限制

## 更新部署

```bash
# 拉取最新代码后重新构建
docker-compose down
docker-compose up -d --build
```

## 完全清理

```bash
# 停止服务并删除所有数据
docker-compose down -v

# 删除镜像
docker rmi admission-system_backend admission-system_frontend
```

## 技术支持

如有问题，请检查：
1. Docker 和 Docker Compose 版本是否最新
2. 端口是否被占用
3. 系统内存是否充足
4. 查看容器日志定位问题
