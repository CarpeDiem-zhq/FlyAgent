# 使用一个合适的 JDK 镜像作为基础镜像
FROM eclipse-temurin:21-jdk-jammy

# 设定容器内的工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到容器中的工作目录
COPY target/your-springboot-app.jar /app/app.jar

# 设置容器启动时的命令
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# 暴露 Spring Boot 默认的端口，确保外部能访问应用
EXPOSE 8080
