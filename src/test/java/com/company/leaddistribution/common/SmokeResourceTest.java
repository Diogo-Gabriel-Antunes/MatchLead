package com.company.leaddistribution.common;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class SmokeResourceTest {

    @Test
    void healthEndpointShouldBeAvailable() {
        given()
                .when().get("/q/health")
                .then()
                .statusCode(200);
    }

    @Test
    void openApiEndpointShouldBeAvailable() {
        given()
                .when().get("/q/openapi")
                .then()
                .statusCode(200);
    }
}
