ARG MODULE_NAME=api

FROM eclipse-temurin:24-jdk-alpine AS builder

WORKDIR /opt/build
COPY gradle/ ./gradle/
COPY gradlew gradlew.bat settings.gradle ./

COPY build.gradle ./
COPY api/ ./api/
COPY bot/ ./bot/
COPY common-jpa/ ./common-jpa/
COPY common-rabbitmq/ ./common-rabbitmq/
COPY http/ ./http/
COPY node/ ./node/

RUN ./gradlew --no-daemon dependencies
RUN ./gradlew --no-daemon clean build -x test

FROM eclipse-temurin:24-jre-alpine

ARG MODULE_NAME=api

WORKDIR /opt/app

# Копируем нужный модуль
COPY --from=builder /opt/build/${MODULE_NAME}/build/libs/*.jar app.jar

COPY images/ /opt/app/images/

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher -version || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]