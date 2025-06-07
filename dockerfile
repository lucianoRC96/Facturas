FROM eclipse-temurin:17-jre

RUN apt-get update && \
    apt-get install -y maven

WORKDIR /app

COPY pom.xml /app
COPY src /app/src
RUN mvn clean package 

COPY --from=build /app/target/facturas-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
