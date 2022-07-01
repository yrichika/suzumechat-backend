FROM openjdk:11

RUN adduser --system --group spring
USER spring:spring

WORKDIR /home/spring

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /home/spring/app.jar
ENTRYPOINT ["java","-jar","/home/spring/app.jar"]