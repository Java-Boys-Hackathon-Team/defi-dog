FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY build/libs/defi-dog-0.0.1-SNAPSHOT.jar defi-dog.jar

ENTRYPOINT ["java","-jar","/defi-dog.jar"]