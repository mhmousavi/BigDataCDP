FROM maven:3.8.1-openjdk-16 as build

WORKDIR /app

COPY ./.mvn ./.mvn
COPY ./pom.xml ./mvnw ./

RUN ./mvnw dependency:resolve dependency:resolve-plugins
COPY . .
RUN ./mvnw compile

ENTRYPOINT ["./mvnw"]

FROM build as package
RUN ./mvnw package

FROM openjdk:16-jdk-alpine
VOLUME /tmp
WORKDIR /app
COPY --from=package /app/target/dcp*.jar ./dcp.jar
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "dcp.jar"]
