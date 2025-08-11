FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY src/main/resources/static/JetBrainsMono-Bold.ttf /app/resources/static/
COPY src/main/resources/static/JetBrainsMono-Regular.ttf /app/resources/static/
COPY src/main/resources/static/JetBrainsMono-ExtraLightItalic.ttf /app/resources/static/
COPY target/ImageTextAdder.jar ImageTextAdder.jar

EXPOSE 8082

CMD ["java", "-jar", "ImageTextAdder.jar"]