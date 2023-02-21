# TODO: should include `gradle build` process in this?
FROM openjdk:17-slim

RUN adduser --system --group spring
USER spring:spring

WORKDIR /home/spring

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /home/spring/app.jar
# this is only for development TODO: change this for production
COPY plain_keyset.json /home/spring/plain_keyset.json

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java","-jar","/home/spring/app.jar"]