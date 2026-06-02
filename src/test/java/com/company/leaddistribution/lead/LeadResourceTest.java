package com.company.leaddistribution.lead;

import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.repository.LeadRepository;
import com.company.leaddistribution.seller.entity.Seller;
import com.company.leaddistribution.seller.repository.SellerRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class LeadResourceTest {

    @Inject
    SellerRepository sellerRepository;

    @Inject
    LeadRepository leadRepository;

    @Inject
    UserTransaction userTransaction;

    @Test
    void shouldCreateAndFindLeadAsAdmin() {
        String token = adminToken();
        String email = uniqueEmail("create");

        Integer id = given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(leadBody(email, uniquePhone(), "FACEBOOK", "SC"))
                .when().post("/api/v1/leads")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("name", equalTo("Maria Souza"))
                .body("email", equalTo(email))
                .body("source", equalTo("FACEBOOK"))
                .body("region", equalTo("SC"))
                .body("status", equalTo("NEW"))
                .body("createdAt", notNullValue())
                .body("updatedAt", notNullValue())
                .extract()
                .path("id");

        given()
                .auth().oauth2(token)
                .when().get("/api/v1/leads/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("email", equalTo(email))
                .body("status", equalTo("NEW"));
    }

    @Test
    void shouldListLeadsWithFiltersAndPagination() {
        String token = adminToken();
        String phone = uniquePhone();
        createLead(token, uniqueEmail("filter"), phone, "WEBSITE", "PR");

        given()
                .auth().oauth2(token)
                .queryParam("status", "NEW")
                .queryParam("region", "PR")
                .queryParam("source", "WEBSITE")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .when().get("/api/v1/leads")
                .then()
                .statusCode(200)
                .body("content[0].id", notNullValue())
                .body("content[0].source", equalTo("WEBSITE"))
                .body("content[0].region", equalTo("PR"))
                .body("content[0].status", equalTo("NEW"))
                .body("page", equalTo(0))
                .body("size", equalTo(10))
                .body("totalElements", notNullValue())
                .body("totalPages", notNullValue());
    }

    @Test
    void shouldUpdateLeadAsManager() {
        String adminToken = adminToken();
        Integer id = createLead(adminToken, uniqueEmail("update"), uniquePhone(), "FACEBOOK", "SC");
        String managerToken = tokenFor("2", "manager@company.com", "MANAGER");
        String updatedEmail = uniqueEmail("updated");

        given()
                .auth().oauth2(managerToken)
                .contentType("application/json")
                .body(leadBody(updatedEmail, uniquePhone(), "GOOGLE", "RS"))
                .when().put("/api/v1/leads/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("email", equalTo(updatedEmail))
                .body("source", equalTo("GOOGLE"))
                .body("region", equalTo("RS"))
                .body("status", equalTo("NEW"));
    }

    @Test
    void shouldUpdateLeadStatus() {
        String token = adminToken();
        Integer id = createLead(token, uniqueEmail("status"), uniquePhone(), "FACEBOOK", "SC");

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "status": "CONTACTED"
                        }
                        """)
                .when().patch("/api/v1/leads/{id}/status", id)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("status", equalTo("CONTACTED"));
    }

    @Test
    void shouldRejectInvalidStatus() {
        String token = adminToken();
        Integer id = createLead(token, uniqueEmail("invalid-status"), uniquePhone(), "FACEBOOK", "SC");

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "status": "INVALID"
                        }
                        """)
                .when().patch("/api/v1/leads/{id}/status", id)
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectLeadWithoutEmailOrPhone() {
        given()
                .auth().oauth2(adminToken())
                .contentType("application/json")
                .body("""
                        {
                          "name": "Maria Souza",
                          "source": "FACEBOOK",
                          "region": "SC"
                        }
                        """)
                .when().post("/api/v1/leads")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldRejectDuplicateLeadByEmailOrPhone() {
        String token = adminToken();
        String email = uniqueEmail("duplicate");
        String phone = uniquePhone();
        createLead(token, email, phone, "FACEBOOK", "SC");

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(leadBody(email.toUpperCase(), uniquePhone(), "GOOGLE", "PR"))
                .when().post("/api/v1/leads")
                .then()
                .statusCode(409);

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(leadBody(uniqueEmail("duplicate-phone"), phone, "GOOGLE", "PR"))
                .when().post("/api/v1/leads")
                .then()
                .statusCode(409);
    }

    @Test
    void shouldAllowSellerToViewOnlyUnassignedOrOwnAssignedLeads() throws Exception {
        String ownSellerEmail = uniqueEmail("own-seller");
        String otherSellerEmail = uniqueEmail("other-seller");
        AssignedLead ownLead = createAssignedLead(ownSellerEmail);
        AssignedLead otherLead = createAssignedLead(otherSellerEmail);
        String sellerToken = tokenFor("3", ownSellerEmail, "SELLER");

        given()
                .auth().oauth2(sellerToken)
                .when().get("/api/v1/leads/{id}", ownLead.leadId())
                .then()
                .statusCode(200)
                .body("seller.id", notNullValue());

        given()
                .auth().oauth2(sellerToken)
                .when().get("/api/v1/leads/{id}", otherLead.leadId())
                .then()
                .statusCode(403);
    }

    @Test
    void shouldFilterBySellerId() throws Exception {
        String token = adminToken();
        AssignedLead assignedLead = createAssignedLead(uniqueEmail("filter-seller"));

        given()
                .auth().oauth2(token)
                .queryParam("sellerId", assignedLead.sellerId())
                .when().get("/api/v1/leads")
                .then()
                .statusCode(200)
                .body("content[0].id", equalTo(assignedLead.leadId().intValue()))
                .body("content[0].sellerId", equalTo(assignedLead.sellerId().intValue()));
    }

    @Test
    void shouldProtectLeadEndpoints() {
        given()
                .when().get("/api/v1/leads")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldRejectSellerCreatingLead() {
        String sellerToken = tokenFor("3", "seller@company.com", "SELLER");

        given()
                .auth().oauth2(sellerToken)
                .contentType("application/json")
                .body(leadBody(uniqueEmail("forbidden"), uniquePhone(), "FACEBOOK", "SC"))
                .when().post("/api/v1/leads")
                .then()
                .statusCode(403);
    }

    private Integer createLead(String token, String email, String phone, String source, String region) {
        return given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(leadBody(email, phone, source, region))
                .when().post("/api/v1/leads")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private AssignedLead createAssignedLead(String sellerEmail) throws Exception {
        userTransaction.begin();
        try {
            Seller seller = new Seller();
            seller.name = "Assigned Seller";
            seller.email = sellerEmail;
            seller.region = "SC";
            seller.specialization = "AUTOMOTIVO";
            seller.dailyCapacity = 10;
            seller.active = true;
            seller.createdAt = LocalDateTime.now();
            seller.updatedAt = seller.createdAt;
            sellerRepository.persist(seller);

            Lead lead = new Lead();
            lead.name = "Assigned Lead";
            lead.email = uniqueEmail("assigned-lead");
            lead.phone = uniquePhone();
            lead.source = "FACEBOOK";
            lead.region = "SC";
            lead.status = LeadStatus.ASSIGNED;
            lead.seller = seller;
            lead.createdAt = LocalDateTime.now();
            lead.updatedAt = lead.createdAt;
            leadRepository.persist(lead);

            Long sellerId = seller.id;
            Long leadId = lead.id;
            userTransaction.commit();
            return new AssignedLead(leadId, sellerId);
        } catch (Exception exception) {
            userTransaction.rollback();
            throw exception;
        }
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

    private String leadBody(String email, String phone, String source, String region) {
        return """
                {
                  "name": "Maria Souza",
                  "email": "%s",
                  "phone": "%s",
                  "source": "%s",
                  "region": "%s"
                }
                """.formatted(email, phone, source, region);
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + System.nanoTime() + "@email.com";
    }

    private String uniquePhone() {
        return "47" + System.nanoTime();
    }

    private record AssignedLead(Long leadId, Long sellerId) {
    }
}
