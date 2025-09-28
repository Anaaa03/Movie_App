# Running Tests in Docker

## Option 1: Run tests once

```bash
docker-compose run --rm test-runner

docker-compose run --rm test-runner ./gradlew test --no-daemon
```

## Option 2: Start test service

```bash
docker-compose --profile test up test-runner

docker-compose exec test-runner bash

./gradlew test
./gradlew test --tests "com.example.movie.user.domain.RegisterUserUseCaseTest"
./gradlew clean test
```

## Option 3: Build and run tests in one step

```bash
docker-compose run --rm --build test-runner
```

## Structure

- `Dockerfile` - production image with JAR
- `Dockerfile.test` - test image with source code and Gradle
- `docker-compose.yml` - contains `test-runner` service with `test` profile
