#!/bin/bash

# 录取数据分析系统 - Docker 部署脚本 (Linux/Mac)

set -e

echo "=========================================="
echo "  录取数据分析系统 - Docker 部署脚本"
echo "=========================================="
echo ""

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "[错误] Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "[错误] Docker Compose 未安装"
    exit 1
fi

echo "[1/5] 正在清理旧容器..."
docker-compose down --volumes --remove-orphans 2>/dev/null || true

echo ""
echo "[2/5] 正在构建镜像..."
docker-compose build --no-cache

echo ""
echo "[3/5] 正在启动服务..."
docker-compose up -d

echo ""
echo "[4/5] 等待服务启动..."
sleep 30

echo ""
echo "[5/5] 检查服务状态..."
docker-compose ps

echo ""
echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo ""
echo "访问地址:"
echo "  - 前端页面: http://localhost"
echo "  - 后端 API: http://localhost:8080"
echo "  - MySQL:    localhost:3306 (root/123456)"
echo "  - Redis:    localhost:6379"
echo ""
echo "常用命令:"
echo "  - 查看日志: docker-compose logs -f"
echo "  - 停止服务: docker-compose down"
echo "  - 重启服务: docker-compose restart"
echo "  - 进入容器: docker exec -it admission-backend bash"
echo ""
