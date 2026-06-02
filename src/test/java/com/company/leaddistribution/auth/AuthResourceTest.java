package com.company.leaddistribution.auth;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class AuthResourceTest {

    @Test
    void shouldLoginAdminUser() {
        given()
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
                .body("accessToken", notNullValue())
                .body("expiresIn", equalTo(3600))
                .body("role", equalTo("ADMIN"));
    }

    @Test
    void shouldReturnCurrentUser() {
        String token = given()
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

        given()
                .auth().oauth2(token)
                .when().get("/api/v1/auth/me")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("Administrador"))
                .body("email", equalTo("admin@company.com"))
                .body("role", equalTo("ADMIN"));
    }

    @Test
    void shouldRejectInvalidCredentials() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "email": "admin@company.com",
                          "password": "invalid"
                        }
                        """)
                .when().post("/api/v1/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldProtectCurrentUserEndpoint() {
        given()
                .when().get("/api/v1/auth/me")
                .then()
                .statusCode(401);
    }
}
