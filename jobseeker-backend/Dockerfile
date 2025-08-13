# base image
FROM openjdk:21-jdk-slim

# WORKING DIRECTORY: where commands will run inside container, will create if doesn't exist
WORKDIR /app

# Docker cache layers - files don't change --> no re-downloading dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# grant gradlew executable permission
RUN chmod +x gradlew
COPY src src

# -x test: skip tests (run them separately in CI/CD)
# --no-daemon: don't start Gradle daemon (saves memory in container)
RUN ./gradlew build -x test --no-daemon

EXPOSE 8080

# command for when container starts
# runs JAR file that Gradle built
CMD ["java", "-jar", "build/libs/jobseeker-backend-0.0.1-SNAPSHOT.jar"]

# build logs alternative: 
# CMD ["./gradlew", "bootRun", "--no-daemon"]