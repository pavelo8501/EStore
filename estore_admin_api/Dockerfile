FROM openjdk:21-jdk-slim AS build

WORKDIR /app

COPY gradle/ gradle/

COPY gradlew .
COPY build.gradle.kts .
COPY gradle.properties .
COPY settings.gradle.kts .

COPY src/ src/

RUN chmod +x /app/gradlew
RUN sh /app/gradlew installDist

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/build/install/estore_admin_api /app/
COPY conf.json /app/conf.json


EXPOSE 8080

CMD ["./bin/estore_admin_api"]