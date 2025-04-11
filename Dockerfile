FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY target/user-organization-management-0.0.1-SNAPSHOT.jar /app/user-organization-management.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "user-organization-management.jar"]
