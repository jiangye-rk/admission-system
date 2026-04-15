@echo off
chcp 65001 >nul
echo ==========================================
echo  录取数据分析系统 - Docker 部署脚本
echo ==========================================
echo.

REM 检查 Docker 是否安装
docker --version >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker 未安装，请先安装 Docker Desktop
    pause
    exit /b 1
)

REM 检查 Docker Compose 是否安装
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker Compose 未安装
    pause
    exit /b 1
)

echo [1/5] 正在清理旧容器...
docker-compose down --volumes --remove-orphans 2>nul

echo.
echo [2/5] 正在构建镜像...
docker-compose build --no-cache

echo.
echo [3/5] 正在启动服务...
docker-compose up -d

echo.
echo [4/5] 等待服务启动...
timeout /t 30 /nobreak >nul

echo.
echo [5/5] 检查服务状态...
docker-compose ps

echo.
echo ==========================================
echo  部署完成！
echo ==========================================
echo.
echo 访问地址:
echo   - 前端页面: http://localhost
echo   - 后端 API: http://localhost:8080
echo   - MySQL:    localhost:3306 (root/123456)
echo   - Redis:    localhost:6379
echo.
echo 常用命令:
echo   - 查看日志: docker-compose logs -f
echo   - 停止服务: docker-compose down
echo   - 重启服务: docker-compose restart
echo   - 进入容器: docker exec -it admission-backend bash
echo.
pause
