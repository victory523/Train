FROM openjdk:17-alpine as build-deps
WORKDIR /usr/src/app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY . ./
RUN ./mvnw package -DskipTests

FROM openjdk:17-alpine
COPY --from=build-deps /usr/src/app/target/*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","/app.jar"]