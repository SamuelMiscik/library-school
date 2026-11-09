# Library

School project for **B4B36SIN** (FEL CTU) – a library system built step by step during the exercises.
Libraries own books, books are published by publishers and written by authors, authors have contracts with publishers.

## Tech stack

- Java 25, Spring Boot 4, Maven (wrapper included: `mvnw`)
- Spring Data JPA + Hibernate, Bean Validation, Lombok
- PostgreSQL 17 in Docker (application), H2 in memory (tests)

## Data model

| Entity | Description |
|---|---|
| `Library` | a library with name and address |
| `Publisher` | publishing house, publishes books, has contracts with authors |
| `Author` | writes books, has contracts with publishers |
| `Book` | a book title (ISBN = primary key), with genre (`Genre` enum) |
| `Ownership` | "library owns this book in N copies" (1–5) |
| `Address` | embedded value (street, city, zip, country) |

Relationships:
- Publisher **1 : N** Book
- Book **N : M** Author (join table `writes`)
- Author **N : M** Publisher (join table `contract`)
- Library **N : M** Book through `Ownership` (has its own attribute `copies`)

Diagrams are in [`docs/`](docs): conceptual model, ER diagram (PlantUML) and relational model.

## Project structure

```
src/main/java/cz/cvut/fel/sin/library/
├── entities/     BO layer – JPA entities
└── repository/   DAO layer – Spring Data repositories (CRUD)
```

## How to run

Requirements: JDK 25, Docker Desktop.

1. Start the database (PostgreSQL on port **5433**):
   ```
   docker compose up -d
   ```
2. Run the application (`LibraryApplication` in the IDE, or):
   ```
   ./mvnw spring-boot:run
   ```
   Tables are created automatically from the entities on the first start.
3. Stop the database:
   ```
   docker compose down        # keeps the data
   docker compose down -v     # deletes the data
   ```

### Profiles

| Profile | File | Usage |
|---|---|---|
| `dev` (default) | `application-dev.properties` | local development, Docker DB, SQL logging |
| `prod` | `application-prod.properties` | DB from env variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`), schema only validated |

Run with another profile: `./mvnw spring-boot:run -Dspring-boot.run.profiles=prod`

## Tests

```
./mvnw test
```
Tests use an in-memory H2 database, Docker is not needed.

## Versions

| Version | Exercise |
|---|---|
| `0.0.1` | cv1 – project setup, conceptual model |
| `0.0.2` | cv2 – JPA entities, repositories, PostgreSQL |
| `0.0.3-SNAPSHOT` | cv3 – services (in progress) |
