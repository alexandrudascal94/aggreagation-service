FROM openjdk:11

COPY /target/aggregation-service-0.0.1-SNAPSHOT.jar aggregation-service.jar

ENTRYPOINT java -jar aggregation-service.jar
