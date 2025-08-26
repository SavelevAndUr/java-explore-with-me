FROM amazoncorretto:21-alpine

# Установка curl для healthcheck
RUN apk add --no-cache curl

COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]