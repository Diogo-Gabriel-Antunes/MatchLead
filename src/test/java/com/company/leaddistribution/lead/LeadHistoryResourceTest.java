package com.company.leaddistribution.lead;

import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadHistory;
import com.company.leaddistribution.lead.entity.LeadHistoryEventType;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.repository.LeadHistoryRepository;
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
class LeadHistoryResourceTest {

    @Inject
    SellerRepository sellerRepository;

    @Inject
    LeadRepository leadRepository;

    @Inject
    LeadHistoryRepository leadHistoryRepository;

    @Inject
    UserTransaction userTransaction;

    @Test
    void shouldCreateHistoryWhenLeadIsCreated() {
        String token = adminToken();
        Integer leadId = createLead(token, uniqueEmail("history-create"));

        given()
                .auth().oauth2(token)
                .when().get("/api/v1/leads/{id}/history", leadId)
                .then()
                .statusCode(200)
                .body("leadId", equalTo(leadId))
                .body("events[0].type", equalTo("LEAD_CREATED"))
                .body("events[0].previousValue", equalTo(null))
                .body("events[0].newValue", equalTo("Lead criado"))
                .body("events[0].date", notNullValue());
    }

    @Test
    void shouldCreateHistoryWhenLeadIsUpdatedAndStatusChanges() {
        String token = adminToken();
        Integer leadId = createLead(token, uniqueEmail("history-flow"));

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(leadBody(uniqueEmail("history-flow-updated"), uniquePhone(), "GOOGLE", "PR"))
                .when().put("/api/v1/leads/{id}", leadId)
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "status": "CONTACTED"
                        }
                        """)
                .when().patch("/api/v1/leads/{id}/status", leadId)
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(token)
                .when().get("/api/v1/leads/{id}/history", leadId)
                .then()
                .statusCode(200)
                .body("events[0].type", equalTo("LEAD_STATUS_CHANGED"))
                .body("events[0].previousValue", equalTo("NEW"))
                .body("events[0].newValue", equalTo("CONTACTED"))
                .body("events[1].type", equalTo("LEAD_UPDATED"))
                .body("events[2].type", equalTo("LEAD_CREATED"));
    }

    @Test
    void shouldCreateAssignedHistoryWhenStatusChangesToAssigned() {
        String token = adminToken();
        Integer leadId = createLead(token, uniqueEmail("history-assigned"));

        given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body("""
                        {
                          "status": "ASSIGNED"
                        }
                        """)
                .when().patch("/api/v1/leads/{id}/status", leadId)
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(token)
                .when().get("/api/v1/leads/{id}/history", leadId)
                .then()
                .statusCode(200)
                .body("events[0].type", equalTo("LEAD_ASSIGNED"))
                .body("events[0].previousValue", equalTo("NEW"))
                .body("events[0].newValue", equalTo("Lead atribuído"))
                .body("events[1].type", equalTo("LEAD_STATUS_CHANGED"));
    }

    @Test
    void shouldAllowManagerToViewHistory() {
        String adminToken = adminToken();
        Integer leadId = createLead(adminToken, uniqueEmail("history-manager"));
        String managerToken = tokenFor("2", "manager@company.com", "MANAGER");

        given()
                .auth().oauth2(managerToken)
                .when().get("/api/v1/leads/{id}/history", leadId)
                .then()
                .statusCode(200)
                .body("leadId", equalTo(leadId));
    }

    @Test
    void shouldAllowSellerToViewOnlyOwnLinkedLeadHistory() throws Exception {
        String ownSellerEmail = uniqueEmail("history-own-seller");
        String otherSellerEmail = uniqueEmail("history-other-seller");
        Long ownLeadId = createAssignedLeadWithHistory(ownSellerEmail);
        Long otherLeadId = createAssignedLeadWithHistory(otherSellerEmail);
        String sellerToken = tokenFor("3", ownSellerEmail, "SELLER");

        given()
                .auth().oauth2(sellerToken)
                .when().get("/api/v1/leads/{id}/history", ownLeadId)
                .then()
                .statusCode(200)
                .body("events[0].type", equalTo("LEAD_CREATED"));

        given()
                .auth().oauth2(sellerToken)
                .when().get("/api/v1/leads/{id}/history", otherLeadId)
                .then()
                .statusCode(403);
    }

    @Test
    void shouldProtectHistoryEndpoint() {
        given()
                .when().get("/api/v1/leads/{id}/history", 1)
                .then()
                .statusCode(401);
    }

    private Integer createLead(String token, String email) {
        return given()
                .auth().oauth2(token)
                .contentType("application/json")
                .body(leadBody(email, uniquePhone(), "FACEBOOK", "SC"))
                .when().post("/api/v1/leads")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private Long createAssignedLeadWithHistory(String sellerEmail) throws Exception {
        userTransaction.begin();
        try {
            Seller seller = new Seller();
            seller.name = "History Seller";
            seller.email = sellerEmail;
            seller.region = "SC";
            seller.specialization = "AUTOMOTIVO";
            seller.dailyCapacity = 10;
            seller.active = true;
            seller.createdAt = LocalDateTime.now();
            seller.updatedAt = seller.createdAt;
            sellerRepository.persist(seller);

            Lead lead = new Lead();
            lead.name = "History Lead";
            lead.email = uniqueEmail("history-assigned-lead");
            lead.phone = uniquePhone();
            lead.source = "FACEBOOK";
            lead.region = "SC";
            lead.status = LeadStatus.ASSIGNED;
            lead.seller = seller;
            lead.createdAt = LocalDateTime.now();
            lead.updatedAt = lead.createdAt;
            leadRepository.persist(lead);

            LeadHistory history = new LeadHistory();
            history.lead = lead;
            history.type = LeadHistoryEventType.LEAD_CREATED;
            history.previousValue = null;
            history.newValue = "Lead criado";
            history.createdAt = LocalDateTime.now();
            leadHistoryRepository.persist(history);

            Long leadId = lead.id;
            userTransaction.commit();
            return leadId;
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
}
