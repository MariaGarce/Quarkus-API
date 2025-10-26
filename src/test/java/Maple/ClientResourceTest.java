package Maple;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClientResourceTest {

    private static UUID createdClientId;

    @Test
    @Order(1)
    void testCreateClient() {
        String clientJson = """
                {
                    "firstName": "John",
                    "middleName": "Michael",
                    "lastName": "Doe",
                    "secondLastName": "Smith",
                    "email": "john.doe@example.com",
                    "address": "123 Main St, New York, NY",
                    "phone": "+1234567890",
                    "country": "US"
                }
                """;

        String idString = given()
                .contentType(ContentType.JSON)
                .body(clientJson)
                .when().post("/clients")
                .then()
                .statusCode(201)
                .body("firstName", is("John"))
                .body("lastName", is("Doe"))
                .body("email", is("john.doe@example.com"))
                .body("country", is("US"))
                .body("id", notNullValue())
                .extract().path("id");
        
        createdClientId = UUID.fromString(idString);

        System.out.println("Created client with ID: " + createdClientId);
    }

    @Test
    @Order(2)
    void testCreateClientWithValidationError() {
        String invalidClientJson = """
                {
                    "firstName": "",
                    "email": "invalid-email",
                    "address": "123 Main St"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(invalidClientJson)
                .when().post("/clients")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(3)
    void testGetAllClients() {
        given()
                .when().get("/clients")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    @Order(4)
    void testGetClientById() {
        given()
                .when().get("/clients/" + createdClientId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", is(createdClientId.toString()))
                .body("firstName", is("John"))
                .body("lastName", is("Doe"))
                .body("email", is("john.doe@example.com"))
                .body("country", is("US"));
    }

    @Test
    @Order(5)
    void testGetClientByIdNotFound() {
        given()
                .when().get("/clients/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    @Order(6)
    void testGetClientsByCountry() {
        given()
                .when().get("/clients/country/US")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].country", is("US"));
    }

    @Test
    @Order(7)
    void testGetClientsByCountryEmpty() {
        given()
                .when().get("/clients/country/ZZ")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", is(0));
    }

    @Test
    @Order(8)
    void testUpdateClient() {
        String updateJson = """
                {
                    "firstName": "Jane",
                    "middleName": "Marie",
                    "lastName": "Smith",
                    "secondLastName": "Johnson",
                    "email": "john.updated@example.com",
                    "address": "456 Updated Ave, Los Angeles, CA",
                    "phone": "+0987654321",
                    "country": "CA"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when().put("/clients/" + createdClientId)
                .then()
                .statusCode(200)
                .body("email", is("john.updated@example.com"))
                .body("address", is("456 Updated Ave, Los Angeles, CA"))
                .body("phone", is("+0987654321"))
                .body("country", is("CA"))
                .body("firstName", is("John"))
                .body("lastName", is("Doe"));
    }

    @Test
    @Order(9)
    void testUpdateClientNotFound() {
        String updateJson = """
                {
                    "firstName": "Test",
                    "lastName": "Test",
                    "email": "test@example.com",
                    "address": "Test Address",
                    "phone": "+1111111111",
                    "country": "US"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(updateJson)
                .when().put("/clients/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }

    @Test
    @Order(10)
    void testDeleteClient() {
        given()
                .when().delete("/clients/" + createdClientId)
                .then()
                .statusCode(204);

        given()
                .when().get("/clients/" + createdClientId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(11)
    void testDeleteClientNotFound() {
        given()
                .when().delete("/clients/" + UUID.randomUUID())
                .then()
                .statusCode(404);
    }
}
