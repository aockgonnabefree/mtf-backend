# ใช้ base image ที่มี Java 21
FROM openjdk:21-jdk-slim

# ตั้งค่า working directory ใน container
WORKDIR /app

COPY build/libs/*.jar app.jar

# บอก Docker ว่า container จะ expose port 8080
EXPOSE 8080

# คำสั่งสำหรับรันแอปพลิเคชัน
ENTRYPOINT ["java", "-jar", "app.jar"]
