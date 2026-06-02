package com.company.leaddistribution.matchmaking;

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
import static org.hamcrest.CoreMatchers.nullValue;

@QuarkusTest
class MatchmakingResourceTest {

    @Inject
    SellerRepository sellerRepository;

    @Inject
    LeadRepository leadRepository;

    @Inject
    UserTransaction userTransaction;

    @Test
    void shouldExecuteMatchmakingWithEligibleSeller() throws Exception {
        String region = uniqueRegion("ELIGIBLE");
        Long leadId = createLead(region);
        Long expectedSellerId = createSeller("Joao Silva", region, true, 10);
        createSeller("Maria Oliveira", region, true, 10);
        createSeller("Inactive Seller", region, false, 10);

        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200)
                .body("leadId", equalTo(leadId.intValue()))
                .body("score", equalTo(0))
                .body("selectedSellerId", equalTo(expectedSellerId.intValue()))
                .body("selectedSellerName", equalTo("Joao Silva"))
                .body("assignmentId", notNullValue())
                .body("assignmentStatus", equalTo("PENDING"))
                .body("leadStatus", equalTo("ASSIGNED"))
                .body("ranking[0].position", equalTo(1))
                .body("ranking[0].sellerId", equalTo(expectedSellerId.intValue()))
                .body("ranking[0].rankingScore", equalTo(100))
                .body("message", equalTo("Vendedor elegível encontrado"));

        given()
                .auth().oauth2(adminToken())
                .when().get("/api/v1/leads/{id}", leadId)
                .then()
                .statusCode(200)
                .body("status", equalTo("ASSIGNED"))
                .body("seller.id", equalTo(expectedSellerId.intValue()));

        given()
                .auth().oauth2(adminToken())
                .when().get("/api/v1/leads/{id}/history", leadId)
                .then()
                .statusCode(200)
                .body("events[0].type", equalTo("LEAD_ASSIGNED"))
                .body("events[0].newValue", equalTo("Lead atribuído ao vendedor Joao Silva"));
    }

    @Test
    void shouldReturnEmptyRankingWhenNoSellerIsEligible() throws Exception {
        String region = uniqueRegion("NOSELLER");
        Long leadId = createLead(region);
        createSeller("Other Region Seller", uniqueRegion("OTHER"), true, 10);

        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200)
                .body("leadId", equalTo(leadId.intValue()))
                .body("score", equalTo(0))
                .body("selectedSellerId", nullValue())
                .body("selectedSellerName", nullValue())
                .body("assignmentId", nullValue())
                .body("assignmentStatus", nullValue())
                .body("leadStatus", equalTo("NEW"))
                .body("ranking.size()", equalTo(0))
                .body("message", equalTo("Nenhum vendedor elegível encontrado"));
    }

    @Test
    void shouldPreferSellerWithLowerCurrentLoad() throws Exception {
        String region = uniqueRegion("LOAD");
        Long busySellerId = createSeller("Busy Seller", region, true, 10);
        Long availableSellerId = createSeller("Available Seller", region, true, 10);
        createAssignedLead(region, busySellerId);
        Long leadId = createLead(region);

        given()
                .auth().oauth2(adminToken())
                .when().get("/api/v1/matchmaking/ranking/{leadId}", leadId)
                .then()
                .statusCode(200)
                .body("selectedSellerId", equalTo(availableSellerId.intValue()))
                .body("assignmentId", nullValue())
                .body("assignmentStatus", nullValue())
                .body("leadStatus", equalTo("NEW"))
                .body("ranking[0].sellerId", equalTo(availableSellerId.intValue()))
                .body("ranking[1].sellerId", equalTo(busySellerId.intValue()));
    }

    @Test
    void shouldExcludeSellerAtDailyCapacity() throws Exception {
        String region = uniqueRegion("CAPACITY");
        Long fullSellerId = createSeller("Full Seller", region, true, 1);
        Long availableSellerId = createSeller("Capacity Seller", region, true, 10);
        createAssignedLead(region, fullSellerId);
        Long leadId = createLead(region);

        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200)
                .body("selectedSellerId", equalTo(availableSellerId.intValue()))
                .body("assignmentId", notNullValue())
                .body("assignmentStatus", equalTo("PENDING"))
                .body("leadStatus", equalTo("ASSIGNED"))
                .body("ranking.size()", equalTo(1));
    }

    @Test
    void shouldNotRedistributeAlreadyAssignedLead() throws Exception {
        String region = uniqueRegion("ALREADY");
        Long leadId = createLead(region);
        Long firstSellerId = createSeller("First Seller", region, true, 10);
        createSeller("Second Seller", region, true, 10);

        Integer assignmentId = given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200)
                .body("selectedSellerId", equalTo(firstSellerId.intValue()))
                .body("leadStatus", equalTo("ASSIGNED"))
                .extract()
                .path("assignmentId");

        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200)
                .body("selectedSellerId", equalTo(firstSellerId.intValue()))
                .body("assignmentId", equalTo(assignmentId))
                .body("assignmentStatus", equalTo("PENDING"))
                .body("leadStatus", equalTo("ASSIGNED"))
                .body("ranking.size()", equalTo(0))
                .body("message", equalTo("Lead já atribuído"));
    }

    @Test
    void shouldAcceptPendingAssignment() throws Exception {
        String region = uniqueRegion("ACCEPT");
        Long leadId = createLead(region);
        String sellerEmail = uniqueEmail("accept-seller");
        Long sellerId = createSeller("Accept Seller", sellerEmail, region, true, 10);
        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(tokenFor("10", sellerEmail, "SELLER"))
                .contentType("application/json")
                .body("""
                        {
                          "leadId": %d,
                          "sellerId": %d
                        }
                        """.formatted(leadId, sellerId))
                .when().post("/api/v1/matchmaking/accept")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("status", equalTo("ACCEPTED"));

        given()
                .auth().oauth2(adminToken())
                .when().get("/api/v1/leads/{id}/history", leadId)
                .then()
                .statusCode(200)
                .body("events[0].type", equalTo("LEAD_ACCEPTED"))
                .body("events[0].newValue", equalTo("Lead aceito pelo vendedor Accept Seller"));
    }

    @Test
    void shouldRejectAndReprocessToNextSeller() throws Exception {
        String region = uniqueRegion("REJECTNEXT");
        Long leadId = createLead(region);
        Long firstSellerId = createSeller("Reject First Seller", region, true, 10);
        Long nextSellerId = createSeller("Reject Next Seller", region, true, 10);
        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200)
                .body("selectedSellerId", equalTo(firstSellerId.intValue()));

        given()
                .auth().oauth2(adminToken())
                .contentType("application/json")
                .body("""
                        {
                          "leadId": %d,
                          "sellerId": %d,
                          "reason": "Indisponivel"
                        }
                        """.formatted(leadId, firstSellerId))
                .when().post("/api/v1/matchmaking/reject")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("status", equalTo("REJECTED"))
                .body("nextAssignmentId", notNullValue())
                .body("nextSellerId", equalTo(nextSellerId.intValue()))
                .body("message", equalTo("Lead recusado e reprocessado para o próximo vendedor"));

        given()
                .auth().oauth2(adminToken())
                .when().get("/api/v1/leads/{id}", leadId)
                .then()
                .statusCode(200)
                .body("status", equalTo("ASSIGNED"))
                .body("seller.id", equalTo(nextSellerId.intValue()));

        given()
                .auth().oauth2(adminToken())
                .when().get("/api/v1/leads/{id}/history", leadId)
                .then()
                .statusCode(200)
                .body("events[0].type", equalTo("LEAD_ASSIGNED"))
                .body("events[1].type", equalTo("LEAD_REJECTED"))
                .body("events[1].newValue", equalTo("Lead recusado pelo vendedor Reject First Seller. Motivo: Indisponivel"));
    }

    @Test
    void shouldRejectWithoutNextSeller() throws Exception {
        String region = uniqueRegion("REJECTNONE");
        Long leadId = createLead(region);
        Long sellerId = createSeller("Only Seller", region, true, 10);
        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(adminToken())
                .contentType("application/json")
                .body("""
                        {
                          "leadId": %d,
                          "sellerId": %d,
                          "reason": "Sem agenda"
                        }
                        """.formatted(leadId, sellerId))
                .when().post("/api/v1/matchmaking/reject")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("status", equalTo("REJECTED"))
                .body("nextAssignmentId", nullValue())
                .body("nextSellerId", nullValue())
                .body("message", equalTo("Lead recusado, mas nenhum próximo vendedor elegível foi encontrado"));

        given()
                .auth().oauth2(adminToken())
                .when().get("/api/v1/leads/{id}", leadId)
                .then()
                .statusCode(200)
                .body("status", equalTo("NEW"))
                .body("seller", nullValue());
    }

    @Test
    void shouldRejectSellerOperatingAnotherSellerAssignment() throws Exception {
        String region = uniqueRegion("FORBIDDEN");
        Long leadId = createLead(region);
        Long sellerId = createSeller("Protected Seller", uniqueEmail("protected-seller"), region, true, 10);
        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(tokenFor("11", "another-seller@email.com", "SELLER"))
                .contentType("application/json")
                .body("""
                        {
                          "leadId": %d,
                          "sellerId": %d
                        }
                        """.formatted(leadId, sellerId))
                .when().post("/api/v1/matchmaking/accept")
                .then()
                .statusCode(403);
    }

    @Test
    void shouldReturnNotFoundForMissingLead() {
        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", 999999)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldProtectMatchmakingEndpoint() {
        given()
                .when().post("/api/v1/matchmaking/execute/{leadId}", 1)
                .then()
                .statusCode(401);
    }

    @Test
    void shouldRejectSellerRoleExecutingMatchmaking() {
        given()
                .auth().oauth2(tokenFor("3", "seller@company.com", "SELLER"))
                .when().post("/api/v1/matchmaking/execute/{leadId}", 1)
                .then()
                .statusCode(403);
    }

    private Long createSeller(String name, String region, boolean active, int dailyCapacity) throws Exception {
        return createSeller(name, uniqueEmail("seller"), region, active, dailyCapacity);
    }

    private Long createSeller(String name, String email, String region, boolean active, int dailyCapacity) throws Exception {
        userTransaction.begin();
        try {
            Seller seller = new Seller();
            seller.name = name;
            seller.email = email;
            seller.region = region;
            seller.specialization = "AUTOMOTIVO";
            seller.dailyCapacity = dailyCapacity;
            seller.active = active;
            seller.createdAt = LocalDateTime.now();
            seller.updatedAt = seller.createdAt;
            sellerRepository.persist(seller);
            Long sellerId = seller.id;
            userTransaction.commit();
            return sellerId;
        } catch (Exception exception) {
            userTransaction.rollback();
            throw exception;
        }
    }

    private Long createLead(String region) throws Exception {
        userTransaction.begin();
        try {
            Lead lead = new Lead();
            lead.name = "Match Lead";
            lead.email = uniqueEmail("lead");
            lead.phone = uniquePhone();
            lead.source = "FACEBOOK";
            lead.region = region;
            lead.status = LeadStatus.NEW;
            lead.createdAt = LocalDateTime.now();
            lead.updatedAt = lead.createdAt;
            leadRepository.persist(lead);
            Long leadId = lead.id;
            userTransaction.commit();
            return leadId;
        } catch (Exception exception) {
            userTransaction.rollback();
            throw exception;
        }
    }

    private void createAssignedLead(String region, Long sellerId) throws Exception {
        userTransaction.begin();
        try {
            Lead lead = new Lead();
            lead.name = "Assigned Load Lead";
            lead.email = uniqueEmail("assigned-lead");
            lead.phone = uniquePhone();
            lead.source = "FACEBOOK";
            lead.region = region;
            lead.status = LeadStatus.ASSIGNED;
            lead.seller = sellerRepository.findById(sellerId);
            lead.createdAt = LocalDateTime.now();
            lead.updatedAt = lead.createdAt;
            leadRepository.persist(lead);
            userTransaction.commit();
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

    private String uniqueRegion(String prefix) {
        return prefix + "-" + System.nanoTime();
    }

    private String uniqueEmail(String prefix) {
        return prefix + "-" + System.nanoTime() + "@email.com";
    }

    private String uniquePhone() {
        return "47" + System.nanoTime();
    }
}
