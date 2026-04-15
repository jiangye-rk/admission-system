@echo off
chcp 65001 >nul
echo ==========================================
echo  录取数据分析系统 - 本地一键启动脚本
echo  （无需 Docker）
echo ==========================================
echo.

REM 检查 Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] Java 未安装，请先安装 JDK 8 或更高版本
    echo 下载地址: https://adoptium.net/
    pause
    exit /b 1
)

echo [1/3] Java 环境正常

REM 检查 MySQL
echo.
echo [2/3] 请确保 MySQL 和 Redis 已安装并运行
echo.
echo 如果未安装，请先安装：
echo   - MySQL 8.0: https://dev.mysql.com/downloads/installer/
echo   - Redis: https://github.com/tporadowski/redis/releases
echo.
echo MySQL 需要创建数据库：
echo   数据库名: admission_db
echo   用户名: root
echo   密码: 123456
echo.
pause

echo.
echo [3/3] 正在启动后端服务...
echo.

REM 启动后端
cd admission-backend\target
start "后端服务" cmd /c "java -jar admission-backend-1.0.0.jar"

echo 后端服务启动中，请等待 10 秒...
timeout /t 10 /nobreak >nul

echo.
echo 正在启动前端...
cd ..\..\admission-frontend\dist
start "" "index.html"

echo.
echo ==========================================
echo  启动完成！
echo ==========================================
echo.
echo 访问地址:
echo   - 前端页面: 已自动打开浏览器
echo   - 后端 API: http://localhost:8080
echo.
echo 注意事项:
echo   1. 确保 MySQL 和 Redis 已启动
echo   2. 首次运行需要导入数据库：sql/admission_db_complete.sql
echo   3. 后端服务窗口请勿关闭
echo.
pause
