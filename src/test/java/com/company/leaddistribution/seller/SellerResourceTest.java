package com.company.leaddistribution.seller;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class SellerResourceTest {

    @Test
    void shouldCreateAndFindSellerAsAdmin() {
        String token = adminToken();
        String email = uniqueEmail("create");

        Integer id = given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(sellerBody(email, true, 50))
                .when().post("/api/v1/sellers")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Joao Silva"))
                .body("email", equalTo(email))
                .body("region", equalTo("SC"))
                .body("specialization", equalTo("AUTOMOTIVO"))
                .body("dailyCapacity", equalTo(50))
                .body("active", equalTo(true))
                .extract()
                .path("id");

        given()
                .auth().oauth2(token)
                .when().get("/api/v1/sellers/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("email", equalTo(email));
    }

    @Test
    void shouldListSellersWithPagination() {
        String token = adminToken();
        String email = uniqueEmail("list");
        createSeller(token, email);

        given()
                .auth().oauth2(token)
                .queryParam("page", 0)
                .queryParam("size", 20)
                .when().get("/api/v1/sellers")
                .then()
                .statusCode(200)
                .body("content[0].id", notNullValue())
                .body("page", equalTo(0))
                .body("size", equalTo(20))
                .body("totalElements", notNullValue())
                .body("totalPages", notNullValue());
    }

    @Test
    void shouldUpdateSellerAsManager() {
        String adminToken = adminToken();
        Integer id = createSeller(adminToken, uniqueEmail("update"));
        String managerToken = tokenFor("2", "manager@company.com", "MANAGER");
        String updatedEmail = uniqueEmail("updated");

        given()
                .auth().oauth2(managerToken)
                .contentType("application/json")
                .body(sellerBody(updatedEmail, false, 25))
                .when().put("/api/v1/sellers/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("email", equalTo(updatedEmail))
                .body("dailyCapacity", equalTo(25))
                .body("active", equalTo(false));
    }

    @Test
    void shouldDeactivateSellerAsAdmin() {
        String token = adminToken();
        Integer id = createSeller(token, uniqueEmail("delete"));

        given()
                .auth().oauth2(token)
                .when().delete("/api/v1/sellers/{id}", id)
                .then()
                .statusCode(204);

        given()
                .auth().oauth2(token)
                .when().get("/api/v1/sellers/{id}", id)
                .then()
                .statusCode(200)
                .body("active", equalTo(false));
    }

    @Test
    void shouldRejectDuplicateSellerEmail() {
        String token = adminToken();
        String email = uniqueEmail("duplicate");
        createSeller(token, email);

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(sellerBody(email.toUpperCase(), true, 50))
                .when().post("/api/v1/sellers")
                .then()
                .statusCode(409);
    }

    @Test
    void shouldRejectInvalidDailyCapacity() {
        given()
                .auth().oauth2(adminToken())
                .contentType("application/json")
                .body(sellerBody(uniqueEmail("invalid"), true, 0))
                .when().post("/api/v1/sellers")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldAllowSellerOnlyToRead() {
        String sellerToken = tokenFor("3", "seller@company.com", "SELLER");

        given()
                .auth().oauth2(sellerToken)
                .when().get("/api/v1/sellers")
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(sellerToken)
                .contentType("application/json")
                .body(sellerBody(uniqueEmail("forbidden"), true, 50))
                .when().post("/api/v1/sellers")
                .then()
                .statusCode(403);
    }

    @Test
    void shouldProtectSellerEndpoints() {
        given()
                .when().get("/api/v1/sellers")
                .then()
                .statusCode(401);
    }

    private Integer createSeller(String token, String email) {
        return given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(sellerBody(email, true, 50))
                .when().post("/api/v1/sellers")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String adminToken() {
        return given()
                .contentType("application/json")
                .body("""
                        {
                          "email": "admin@company.com",
                          "password": "123456"
                        }
                        """)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    private String tokenFor(String subject, String email, String role) {
        return Jwt.issuer("matchlead")
                .subject(subject)
                .upn(email)
                .groups(Set.of(role))
                .expiresAt(Instant.now().plusSeconds(3600))
                .sign();
    }

    private String sellerBody(String email, boolean active, int dailyCapacity) {
        return """
                {
                  "name": "Joao Silva",
                  "email": "%s",
                  "region": "SC",
                  "specialization": "AUTOMOTIVO",
                  "dailyCapacity": %d,
                  "active": %s
                }
                """.formatted(email, dailyCapacity, active);
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + System.nanoTime() + "@email.com";
    }
}
