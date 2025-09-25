# Movie App - Movie Review Application

Movie App is a web application for managing movies and reviews, built with Spring Boot technology. The application
allows users to browse movies, add reviews, and manage accounts with different permission levels.
The system also uses session-based authentication.

## Installation

### Option 1: With Docker

**Prerequisites:**

- Docker Desktop or Docker Engine installed
- Docker Compose installed

**Quick Start:**

```bash
git clone https://github.com/Anaaa03/movie-app.git
cd movie-app

./gradlew build

docker-compose up
```

**What happens:**

- PostgreSQL database starts on port 5432
- Spring Boot application starts on port 8088
- Database migrations run automatically
- Application is ready at: http://localhost:8088

### Option 2: Local Development (requires local PostgreSQL)

**Prerequisites:**

- Java 24 or newer
- PostgreSQL 12 or newer (installed locally)
- Gradle 8.0+ (optional - project contains wrapper)

**Setup:**

```bash
CREATE DATABASE db_MovieApp;

./gradlew bootRun
```

**Application:** http://localhost:8088

## Tests

```bash
./gradlew test
```

## Features

### Movies

- Adding movies
- Uploading posters and trailers
- Browsing movie details

### Users

- Registration and login
- Roles: USER, SUPER_REVIEWER, ADMIN
- Role management by administrator

### Reviews

- **Regular reviews** (everyone): rating 1-10 + comment
- **Super reviews** (SUPER_REVIEWER/ADMIN): detailed rating in 5 categories + title, pros/cons, recommendation

## Technologies

- Java 24 + Spring Boot 3.5.3
- Spring Security + PostgreSQL
- Flyway + Lombok + Gradle

## Requirements

### With Docker:

- Docker Desktop or Docker Engine
- Docker Compose

### Without Docker:

- Java 24 or newer
- PostgreSQL 12 or newer
- Gradle 8.0+ (optional - project contains wrapper)

## API Endpoints

### Authentication

```
POST /api/auth/login     - Login
POST /api/auth/logout    - Logout
```

### Users

```
POST   /api/users                    - Registration
GET    /api/users/{id}               - User data
POST   /api/users/admin/change-role  - Role change (ADMIN)
```

### Movies

```
POST   /api/movies                    - Add movie
GET    /api/movies/{movieId}          - Movie details
POST   /api/movies/{movieId}/poster   - Upload poster
```

### Reviews

```
POST   /api/reviews                    - Add review
GET    /api/reviews/{reviewId}         - Get review
GET    /api/reviews/movie/{movieId}    - Movie reviews
PUT    /api/reviews/{reviewId}         - Edit review
DELETE /api/reviews/{reviewId}         - Delete review
```

### Super Reviews

```
POST   /api/super-reviews                    - Add super review
GET    /api/super-reviews/{superReviewId}    - Get super review
PUT    /api/super-reviews/{superReviewId}    - Edit super review
DELETE /api/super-reviews/{superReviewId}    - Delete super review
```

## Database

**Tables:**

- users - users
- movies - movies
- reviews - regular reviews
- super_reviews - super reviews

**Relations:**

- User can have many reviews
- Movie can have many reviews
- One user = one review per movie

## Architecture

**Clean Architecture:**

- api/ - REST controllers
- domain/ - business logic
- persistence/ - database access

**Modules:**

- movie/ - movies
- user/ - users
- review/ - regular reviews
- superreview/ - super reviews

---

**Author:** Anna Zakrzewska
