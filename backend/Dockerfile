FROM maven:3.9.6-sapmachine-21 AS build
COPY src /app/src/
COPY pom.xml /app/
WORKDIR /app
RUN mvn -X clean package -DskipTests

FROM openjdk:21
COPY --from=build /app/target/*.jar /usr/app/app.jar
CMD ["java","-jar","/usr/app/app.jar"]