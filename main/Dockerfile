FROM amazoncorretto:11
WORKDIR /app
COPY target/*.jar main.jar
ENTRYPOINT ["java","-jar","main.jar"]