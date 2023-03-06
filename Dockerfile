FROM openjdk:17

WORKDIR /app

COPY ./target/testingMicroservices-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "testingMicroservices-0.0.1-SNAPSHOT.jar"]