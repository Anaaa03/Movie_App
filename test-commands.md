# Uruchamianie testów w Docker

## Opcja 1: Uruchomienie testów jednorazowo

```bash
docker-compose run --rm test-runner

docker-compose run --rm test-runner ./gradlew test --no-daemon
```

## Opcja 2: Uruchomienie serwisu testowego

```bash
docker-compose --profile test up test-runner

docker-compose exec test-runner bash

./gradlew test
./gradlew test --tests "com.example.movie.user.domain.RegisterUserUseCaseTest"
./gradlew clean test
```

## Opcja 3: Build i uruchomienie testów w jednym kroku

```bash
docker-compose run --rm --build test-runner
```

## Struktura

- `Dockerfile` - produkcyjny obraz z JAR
- `Dockerfile.test` - obraz testowy z kodem źródłowym i Gradle
- `docker-compose.yml` - zawiera serwis `test-runner` z profilem `test`
