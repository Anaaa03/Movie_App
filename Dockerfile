FROM eclipse-temurin:24
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]