# 后端 Dockerfile
# 使用 Maven 构建阶段
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制 pom.xml 先下载依赖（利用 Docker 缓存层）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre

WORKDIR /app

# 安装必要的工具
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 从构建阶段复制 jar 包
COPY --from=builder /app/target/admission-backend-1.0.0.jar app.jar

# 创建上传目录
RUN mkdir -p /app/upload

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
