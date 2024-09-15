FROM openjdk:21-jdk-slim

WORKDIR /app

#COPY build.gradle.kts settings.gradle.kts ./
#COPY gradlew gradlew.bat ./
#COPY gradle ./gradle
#COPY src ./src


COPY . .

RUN chmod +x ./gradlew

RUN ls -l


EXPOSE 8080
CMD ["./gradlew", "build"]
CMD ["java", "-jar", "build/libs/.jar"]