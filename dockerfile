<<<<<<< HEAD
FROM eclipse-temurin:17-jre

RUN apt-get update && \
    apt-get install -y maven
=======
# Etapa de build
FROM eclipse-temurin:17-jdk AS build
>>>>>>> cc62e4677aa6b0c5f5852d88cf2ca86c628d6a57

WORKDIR /app

COPY pom.xml /app
COPY src /app/src
<<<<<<< HEAD
RUN mvn clean package 
=======
RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean package

# Etapa de runtime
FROM eclipse-temurin:17-jre

WORKDIR /app
>>>>>>> cc62e4677aa6b0c5f5852d88cf2ca86c628d6a57

COPY --from=build /app/target/facturas-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
