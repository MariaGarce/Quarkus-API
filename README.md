# Clients API

A RESTful API for managing customer/client data with automatic demonym enrichment, built with Quarkus and PostgreSQL.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture & Design Decisions](#architecture--design-decisions)
- [API Endpoints](#api-endpoints)
- [Data Model](#data-model)
- [Setup & Configuration](#setup--configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Project Structure](#project-structure)

## Overview

This API provides a complete CRUD (Create, Read, Update, Delete) system for managing client information. It automatically enriches client records with their country's demonym (e.g., "American" for US, "español" for ES) by integrating with the RestCountries API.

**Tech Stack:**
- **Framework:** Quarkus 3.28.5 (Supersonic Subatomic Java)
- **Database:** PostgreSQL with Hibernate ORM Panache
- **External API:** RestCountries API v3.1
- **Testing:** JUnit 5, RestAssured, Testcontainers
- **Validation:** Hibernate Validator (Jakarta Bean Validation)

**Tech Stack:**
- **Framework:** Quarkus 3.28.5 (Supersonic Subatomic Java)
- **Database:** PostgreSQL with Hibernate ORM Panache
- **External API:** RestCountries API v3.1
- **Testing:** JUnit 5, RestAssured, Testcontainers
- **Validation:** Hibernate Validator (Jakarta Bean Validation)

## Features

✅ **Complete CRUD Operations** - Create, Read, Update, and Delete clients  
✅ **UUID-based Identifiers** - Globally unique, non-sequential IDs for security  
✅ **Automatic Demonym Enrichment** - Fetches country demonyms from RestCountries API  
✅ **Intelligent Fallback** - Uses English demonym when Spanish is unavailable  
✅ **Country Filtering** - Query clients by country code  
✅ **Bean Validation** - Automatic request validation with detailed error messages  
✅ **Clean Architecture** - Separation of concerns (Resource → Service → Entity)  
✅ **Comprehensive Testing** - 11 unit tests covering all endpoints and edge cases

## Architecture & Design Decisions

### 1. **Layered Architecture (Clean Code Principle)**

The application follows a **three-layer architecture** to maintain separation of concerns:

```
┌─────────────────────────────────────┐
│   Resource Layer (Controller)       │  ← HTTP handling, routing
│   ClientResource.java                │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Service Layer (Business Logic)    │  ← Business rules, orchestration
│   ClientService.java                 │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│   Entity Layer (Data Model)          │  ← Database mapping
│   Client.java (Panache)              │
└─────────────────────────────────────┘
```

**Design Rationale:**
- **Resource Layer** contains NO business logic - only HTTP concerns (status codes, response building)
- **Service Layer** encapsulates all business rules, including demonym fetching and field update restrictions
- **Entity Layer** uses Panache active record pattern for simplified database operations
- This separation enables easier testing, maintenance, and future scalability


## API Endpoints

### Base URL: `http://localhost:8080`

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/clients` | Create a new client | Client JSON (without ID) | 201 Created + Client |
| `GET` | `/clients` | Get all clients | - | 200 OK + Client[] |
| `GET` | `/clients/country/{code}` | Get clients by country | - | 200 OK + Client[] |
| `GET` | `/clients/{id}` | Get client by UUID | - | 200 OK + Client / 404 |
| `PUT` | `/clients/{id}` | Update client (email/address/phone/country) | Client JSON | 200 OK + Client / 404 |
| `DELETE` | `/clients/{id}` | Delete client | - | 204 No Content / 404 |

### Example Requests

#### Create Client
```bash
curl -X POST http://localhost:8080/clients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "middleName": "Robert",
    "lastName": "Doe",
    "secondLastName": "",
    "email": "john.doe@example.com",
    "address": "123 Main St, New York, NY",
    "phone": "+1234567890",
    "country": "US"
  }'
```

**Response:**
```json
{
  "id": "b47e3e02-43d0-47ab-9a34-5e1b39051846",
  "firstName": "John",
  "middleName": "Robert",
  "lastName": "Doe",
  "secondLastName": "",
  "email": "john.doe@example.com",
  "address": "123 Main St, New York, NY",
  "phone": "+1234567890",
  "country": "US",
  "demonym": "American"
}
```

#### Get All Clients
```bash
curl http://localhost:8080/clients
```

#### Get Clients by Country
```bash
curl http://localhost:8080/clients/country/US
```

#### Get Client by ID
```bash
curl http://localhost:8080/clients/b47e3e02-43d0-47ab-9a34-5e1b39051846
```

#### Update Client
```bash
curl -X PUT http://localhost:8080/clients/b47e3e02-43d0-47ab-9a34-5e1b39051846 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "new.email@example.com",
    "address": "456 Oak Ave, Boston, MA",
    "phone": "+1987654321",
    "country": "US"
  }'
```

#### Delete Client
```bash
curl -X DELETE http://localhost:8080/clients/b47e3e02-43d0-47ab-9a34-5e1b39051846
```

## Data Model

### Client Entity

| Field | Type | Required | Constraints | Description |
|-------|------|----------|-------------|-------------|
| `id` | UUID | Auto-generated | Primary Key | Unique identifier |
| `firstName` | String | ✅ | Not blank | Client's first name |
| `middleName` | String | ❌ | - | Client's middle name (optional) |
| `lastName` | String | ✅ | Not blank | Client's last name |
| `secondLastName` | String | ❌ | - | Client's second last name (optional) |
| `email` | String | ✅ | Valid email, unique | Contact email address |
| `address` | String | ✅ | Not blank | Physical address |
| `phone` | String | ✅ | Not blank | Phone number |
| `country` | String | ✅ | 2-3 chars (ISO code) | Country code (e.g., "US", "ES") |
| `demonym` | String | Auto-populated | - | Country demonym (e.g., "American") |

**Validation Rules:**
- Email must be valid format and unique across all clients
- Country code must be 2-3 characters (ISO 3166-1 alpha-2/alpha-3)
- Demonym is automatically fetched and cannot be manually set

## Setup & Configuration

### Prerequisites
- **Java:** JDK 21 or later
- **Maven:** 3.9+ (or use included `./mvnw`)
- **PostgreSQL:** 13+ (or use Quarkus Dev Services for auto-provisioning)
- **Docker:** Required for Dev Services and Testcontainers

### Database Configuration

**Option 1: Dev Services (Automatic - Recommended for Development)**

No configuration needed! Quarkus automatically starts a PostgreSQL container:

```properties
# application.properties (minimal config)
quarkus.datasource.db-kind=postgresql
```

**Option 2: Manual PostgreSQL Connection**

```properties
# application.properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=your_username
quarkus.datasource.password=your_password
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/your_database

# Disable Dev Services
quarkus.devservices.enabled=false

# Schema management (update schema automatically)
quarkus.hibernate-orm.database.generation=update
```

### Environment Variables (Alternative)

```bash
export QUARKUS_DATASOURCE_USERNAME=postgres
export QUARKUS_DATASOURCE_PASSWORD=postgres
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/clientsdb
```

## Running the Application

### Development Mode (Live Reload)

Run with automatic live coding (code changes reload instantly):

```bash
./mvnw quarkus:dev
```

The application will start at: **http://localhost:8080**

**Dev UI:** Available at http://localhost:8080/q/dev/

## Testing

### Run All Tests

```bash
./mvnw test
```

### Test Coverage

The project includes **11 comprehensive tests** in `ClientResourceTest.java`:

- ✅ `testCreateClient()` - Create new client with demonym enrichment
- ✅ `testListAllClients()` - Retrieve all clients
- ✅ `testGetClientsByCountry()` - Filter clients by country code
- ✅ `testGetClientById()` - Retrieve specific client by UUID
- ✅ `testUpdateClient()` - Update allowed fields (email, address, phone, country)
- ✅ `testDeleteClient()` - Delete client and verify removal
- ✅ `testGetNonExistentClient()` - 404 for invalid UUID
- ✅ `testUpdateNonExistentClient()` - 404 on update attempt
- ✅ `testDeleteNonExistentClient()` - 404 on delete attempt
- ✅ `testCreateClientWithInvalidEmail()` - Validation error handling
- ✅ `testCreateClientWithMissingFields()` - Required field validation

**Integration Tests:** `ClientResourceIT.java` extends unit tests with Testcontainers for full database integration.

### Manual API Testing

**Using HTTPie:**
```bash
# Create client
http POST :8080/clients firstName="Jane" lastName="Smith" email="jane@example.com" address="789 Pine St" phone="+1122334455" country="ES"

# Get all
http :8080/clients

# Get by country
http :8080/clients/country/ES

# Update
http PUT :8080/clients/{uuid} email="updated@example.com"

# Delete
http DELETE :8080/clients/{uuid}
```

## Project Structure

```
src/
├── main/
│   ├── java/Maple/
│   │   ├── Dto/
│   │   │   └── CountryDto.java              # DTO for RestCountries API response
│   │   ├── Entity/
│   │   │   └── Client.java                  # JPA entity with Panache
│   │   ├── Resource/
│   │   │   └── ClientResource.java          # REST endpoints (controller)
│   │   └── Service/
│   │       ├── ClientService.java           # Business logic layer
│   │       └── RestCountriesClient.java     # MicroProfile REST client interface
│   └── resources/
│       ├── application.properties           # Quarkus configuration
│       └── import.sql                       # Initial data (optional)
└── test/
    └── java/Maple/
        ├── ClientResourceTest.java          # Unit tests
        └── ClientResourceIT.java            # Integration tests
```

## RestCountries API Integration

**API Endpoint:** `https://restcountries.com/v3.1/alpha/{code}`

**Example Response:**
```json
[
  {
    "demonyms": {
      "eng": { "f": "American", "m": "American" },
      "fra": { "f": "Américaine", "m": "Américain" }
    }
  }
]
```

**Fallback Strategy:**
1. Try Spanish (`spa`) demonym → Male preferred, Female if male unavailable
2. If no Spanish, try English (`eng`) → Male preferred, Female fallback
3. If neither available, return `null` (graceful degradation)

**Why This Approach?**
- Not all countries have Spanish translations in the API
- English is universally available as fallback
- Application remains functional even if demonym unavailable


## Related Quarkus Guides

- [Hibernate ORM with Panache](https://quarkus.io/guides/hibernate-orm-panache) - Simplified persistence
- [Hibernate Validator](https://quarkus.io/guides/validation) - Bean validation
- [REST Jackson](https://quarkus.io/guides/rest#json-serialisation) - JSON serialization
- [JDBC Driver - PostgreSQL](https://quarkus.io/guides/datasource) - Database connectivity
- [MicroProfile REST Client](https://quarkus.io/guides/rest-client) - External API integration
- [Quarkus Dev Services](https://quarkus.io/guides/dev-services) - Auto-provisioning databases


