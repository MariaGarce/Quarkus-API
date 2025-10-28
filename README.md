# Clients API

RESTful API for managing client data with automatic demonym enrichment from country codes.

**Tech Stack:** Quarkus 3.28.5 | PostgreSQL | Panache ORM | RestCountries API | Swagger UI

## Table of Contents
- [Quick Start](#quick-start)
- [Features](#features)
- [Architecture](#architecture)
- [How to Run](#how-to-run)
- [Using Just (Task Runner)](#using-just-task-runner)
- [API Endpoints](#api-endpoints)
- [Data Model](#data-model)
- [Configuration](#configuration)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Key Design Decisions](#key-design-decisions)
- [Dependencies](#dependencies)

## Quick Start

```bash
# Run in development mode (auto-starts PostgreSQL via Dev Services)
./mvnw quarkus:dev

# Access Swagger UI
open http://localhost:8080/q/swagger-ui

# Run tests
./mvnw test
```

## Features

- ✅ Full CRUD operations (Create, Read, Update, Delete)
- ✅ UUID-based identifiers for security
- ✅ Auto-enrichment of country demonyms (Spanish → English fallback)
- ✅ Country-based filtering
- ✅ Case-insensitive email uniqueness
- ✅ Comprehensive validation & error handling
- ✅ Clean layered architecture with DTO pattern
- ✅ 11 unit + integration tests

## Architecture

**Layered Architecture:**
- **Resource Layer** → HTTP/REST endpoints, validation (`ClientResource.java`)
- **Service Layer** → Business logic, email uniqueness, demonym fetching (`ClientService.java`)
- **Entity Layer** → Database mapping with Panache Active Record (`Client.java`)
- **DTO Layer** → API contract, separates input/output from persistence (`ClientDto.java`)

**Request Flow:**
```
HTTP Request → ClientResource → ClientDto validation → ClientService
    → Check duplicate email (Database)
    → Fetch demonym (RestCountries API)
    → Client.persist() (Database)
    → Return ClientDto
```

## How to Run

### Prerequisites
- **Java:** JDK 21 or later
- **Maven:** 3.9+ (or use included `./mvnw`)
- **Docker:** Required for Dev Services (auto-starts PostgreSQL)

### Development Mode

```bash
# Clone the repository
git clone https://github.com/MariaGarce/Quarkus-API.git
cd clientsapi

# Run in development mode (live reload enabled)
./mvnw quarkus:dev
```

Quarkus will:
- Automatically start a PostgreSQL container via Dev Services
- Apply database schema migrations
- Start the application on `http://localhost:8080`
- Enable live coding (code changes reload instantly)

**Access Points:**
- **API Base:** http://localhost:8080/clients
- **Swagger UI:** http://localhost:8080/q/swagger-ui
- **Dev UI:** http://localhost:8080/q/dev


### Stop the Application

- **Dev Mode:** Press `Ctrl+C` in terminal or type `q` and press Enter
- **Production:** Press `Ctrl+C`

## Using Just (Task Runner)

This project includes a `justfile` for common tasks. [Install Just](https://github.com/casey/just#installation) to use these commands:

```bash
# Install just (macOS)
brew install just

# Show all available commands
just

# Common commands
just dev              # Start development mode
just test             # Run all tests
just swagger          # Open Swagger UI in browser
just create-client John john@example.com US  # Create a test client
just get-clients      # List all clients (pretty printed)
just kill-port        # Kill process on port 8080
just clean            # Clean build artifacts
just package          # Build production package
```

**Available Commands:**
- **Development:** `dev`, `dev-ui`, `swagger`, `kill-port`
- **Building:** `package`, `build-native`, `docker-build`
- **Testing:** `test`, `test-coverage`
- **API Testing:** `create-client`, `get-clients`, `get-clients-by-country`, `delete-client`
- **Maintenance:** `clean`, `format`, `update-deps`, `logs`
- **Git:** `commit "message"`

See the full list with `just --list`

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/clients` | Create client (demonym auto-populated) |
| `GET` | `/clients` | List all clients |
| `GET` | `/clients/country/{code}` | Filter by country (e.g., "US", "ES") |
| `GET` | `/clients/{id}` | Get client by UUID |
| `PUT` | `/clients/{id}` | Update email/address/phone/country only |
| `DELETE` | `/clients/{id}` | Delete client |

**Example:**
```bash
# Create client
curl -X POST http://localhost:8080/clients -H "Content-Type: application/json" -d '{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "address": "123 Main St",
  "phone": "+1234567890",
  "country": "US"
}'

# Response
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "address": "123 Main St",
  "phone": "+1234567890",
  "country": "US",
  "demonym": "American"  # Auto-populated from RestCountries API
}
```

## Data Model

| Field | Type | Required | Notes |
|-------|------|----------|-------|
| `id` | UUID | Auto | Primary key |
| `firstName` | String | ✅ | - |
| `middleName` | String | ❌ | Optional |
| `lastName` | String | ✅ | - |
| `secondLastName` | String | ❌ | Optional |
| `email` | String | ✅ | Unique, case-insensitive |
| `address` | String | ✅ | - |
| `phone` | String | ✅ | - |
| `country` | String | ✅ | ISO 3166-1 (2-3 chars) |
| `demonym` | String | Auto | Fetched from RestCountries API |

## Configuration

**Minimal setup (Dev Services auto-starts PostgreSQL):**
```properties
# application.properties
quarkus.datasource.db-kind=postgresql
```

**Manual PostgreSQL:**
```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/clientsdb
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.devservices.enabled=false
```

## Testing

```bash
./mvnw test  # Runs 11 tests with Testcontainers
```

**Coverage:**
- CRUD operations (create, read, update, delete)
- Validation (invalid email, missing fields)
- Error handling (404 Not Found, 409 Conflict)
- Demonym enrichment verification

## Project Structure

```
src/main/java/Maple/
├── Resource/
│   ├── ClientResource.java      # REST endpoints
│   └── RootResource.java         # Redirects / → /q/swagger-ui
├── Service/
│   ├── ClientService.java        # Business logic
│   └── RestCountriesClient.java  # External API client
├── Entity/
│   └── Client.java               # JPA entity (Panache)
└── Dto/
    ├── ClientDto.java            # Request/response DTO
    └── CountryDto.java           # External API response
```

## Key Design Decisions

1. **Manual Mapping** → `Client.toEntity(dto)` instead of MapStruct (simpler, no dependencies)
2. **DTO Pattern** → Input DTOs exclude `id`/`demonym` (read-only fields in responses)
3. **Panache Active Record** → `Client.listAll()`, `client.persist()` (less boilerplate)
4. **Case-Insensitive Email** → Stored as lowercase for uniqueness checks
5. **Update Restrictions** → Only email/address/phone/country can be modified
6. **Error Handling** → Try-catch in all endpoints (409 Conflict, 404 Not Found, 500 Server Error)

## Dependencies

- `quarkus-rest` - JAX-RS REST endpoints
- `quarkus-hibernate-orm-panache` - Simplified ORM
- `quarkus-jdbc-postgresql` - PostgreSQL driver
- `quarkus-rest-client-jackson` - MicroProfile REST Client
- `quarkus-smallrye-openapi` - Swagger/OpenAPI docs
- `quarkus-hibernate-validator` - Bean validation
- `rest-assured` + `testcontainers` - Testing

---

**Swagger UI:** http://localhost:8080/q/swagger-ui  
**Dev UI:** http://localhost:8080/q/dev



