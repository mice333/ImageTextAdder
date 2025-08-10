FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/ImageTextAdder.jar ImageTextAdder.jar

EXPOSE 8082

CMD ["java", "-jar", "ImageTextAdder.jar"]