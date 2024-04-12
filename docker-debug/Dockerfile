FROM openjdk:8-jdk-alpine
WORKDIR /data
COPY ./target/docker-debug-1.0-SNAPSHOT.jar  /data/docker-debug-1.0-SNAPSHOT.jar
ENTRYPOINT java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar docker-debug-1.0-SNAPSHOT.jar