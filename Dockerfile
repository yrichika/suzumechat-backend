# REFERENCE: https://spring.pleiades.io/spring-boot/docs/current/reference/html/container-images.html#container-images.dockerfiles
# builder
FROM openjdk:17-slim as builder
ARG JAR_FILE=build/libs/*.jar

WORKDIR /extracted
COPY ${JAR_FILE} /extracted/app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# runner
FROM openjdk:17-slim as runner
ARG KEYSET_FILE=plain_keyset.json

RUN adduser --system --group spring
USER spring:spring
WORKDIR /home/spring

COPY --from=builder /extracted/dependencies/ ./
COPY --from=builder /extracted/spring-boot-loader/ ./
COPY --from=builder /extracted/snapshot-dependencies/ ./
COPY --from=builder /extracted/application/ ./

COPY ${KEYSET_FILE} /home/spring/${KEYSET_FILE}

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]