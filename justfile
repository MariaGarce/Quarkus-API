# Quarkus Clients API - Justfile
# Run commands with: just <command>

# Default recipe (runs when you type 'just')
default:
    @just --list

# Start application in development mode (live reload)
dev:
    ./mvnw quarkus:dev

# Run all tests
test:
    ./mvnw test

# Run tests with coverage
test-coverage:
    ./mvnw verify

# Clean build artifacts
clean:
    ./mvnw clean

# Package application for production
package:
    ./mvnw package

# Build native executable (requires GraalVM)
build-native:
    ./mvnw package -Dnative

# Build native executable in Docker container
build-native-docker:
    ./mvnw package -Dnative -Dquarkus.native.container-build=true

# Run packaged application
run:
    java -jar target/quarkus-app/quarkus-run.jar

# Build Docker image (JVM mode)
docker-build:
    docker build -f src/main/docker/Dockerfile.jvm -t quarkus/clientsapi:jvm .

# Build Docker image (native mode)
docker-build-native:
    docker build -f src/main/docker/Dockerfile.native -t quarkus/clientsapi:native .

# Run Docker container (JVM mode)
docker-run:
    docker run -i --rm -p 8080:8080 quarkus/clientsapi:jvm

# Run Docker container (native mode)
docker-run-native:
    docker run -i --rm -p 8080:8080 quarkus/clientsapi:native

# Open Swagger UI in browser
swagger:
    open http://localhost:8080/q/swagger-ui

# Open Dev UI in browser
dev-ui:
    open http://localhost:8080/q/dev


# Kill process running on port 8080
kill-port:
    lsof -ti:8080 | xargs kill -9 2>/dev/null || echo "No process on port 8080"


# Install project dependencies
install:
    ./mvnw install -DskipTests

# Update dependencies
update-deps:
    ./mvnw versions:display-dependency-updates


# Show project info
info:
    @echo "Project: Quarkus Clients API"
    @echo "Java Version: $(java -version 2>&1 | head -1)"
    @echo "Maven Version: $(./mvnw --version | head -1)"
    @echo "Port: 8080"
