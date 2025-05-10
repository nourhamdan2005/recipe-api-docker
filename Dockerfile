FROM openjdk:21-jdk-slim

WORKDIR /app
COPY target/recipeApi-0.0.1-SNAPSHOT.jar app.jar

# Unpack the JAR so we can access files like application.properties
RUN mkdir unpacked && cd unpacked && jar -xf ../app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
