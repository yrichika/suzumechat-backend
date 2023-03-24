# TODO: should include `gradle build` process in this?
FROM openjdk:17-slim

ARG keyset_file=plain_keyset.json

RUN adduser --system --group spring
USER spring:spring

WORKDIR /home/spring

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /home/spring/app.jar
COPY ${keyset_file} /home/spring/${keyset_file}

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java","-jar","/home/spring/app.jar"]