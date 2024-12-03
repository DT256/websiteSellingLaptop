# Sử dụng hình ảnh Java 21
FROM eclipse-temurin:21-jdk

# Tạo thư mục làm việc
WORKDIR /app

# Copy file .jar vào container
COPY target/websiteSellingLaptop-0.0.1-SNAPSHOT.jar app.jar

# Expose cổng 8080
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
