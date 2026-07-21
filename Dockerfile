# syntax=docker/dockerfile:1

FROM docker.m.daocloud.io/library/maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY docker/maven/settings.xml /root/.m2/settings.xml
COPY server/pom.xml ./server/pom.xml
WORKDIR /app/server
RUN mvn -B -s /root/.m2/settings.xml -DskipTests dependency:go-offline

WORKDIR /app
COPY server ./server
WORKDIR /app/server
RUN mvn -B -s /root/.m2/settings.xml -DskipTests package

FROM docker.m.daocloud.io/library/eclipse-temurin:17-jre
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod
COPY --from=build /app/server/target/xiyuguopu-server-1.0.0.jar /app/xiyuguopu-server.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/xiyuguopu-server.jar"]
