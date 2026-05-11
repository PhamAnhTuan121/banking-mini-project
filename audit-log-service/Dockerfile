# Sử dụng base image Java
FROM eclipse-temurin:17-jre-alpine

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app

# Copy file JAR của ứng dụng vào container
# Giả định project Spring Boot của bạn đã được build ra file target/*.jar
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Lệnh chạy ứng dụng khi container khởi động
ENTRYPOINT ["java", "-jar", "app.jar"]