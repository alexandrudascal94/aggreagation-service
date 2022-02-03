FROM registry.acc.payconiq.io/base-openjdk11:minideb-nr

COPY /target/aggregation-service-0.0.1-SNAPSHOT.jar aggregation-service.jar

ENTRYPOINT java -jar aggregation-service.jar
