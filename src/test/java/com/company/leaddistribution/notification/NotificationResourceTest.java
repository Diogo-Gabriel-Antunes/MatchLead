package com.company.leaddistribution.notification;

import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.repository.LeadRepository;
import com.company.leaddistribution.seller.entity.Seller;
import com.company.leaddistribution.seller.repository.SellerRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
class NotificationResourceTest {

    @Inject
    SellerRepository sellerRepository;

    @Inject
    LeadRepository leadRepository;

    @Inject
    UserTransaction userTransaction;

    @Test
    void shouldCreateAssignedNotificationsWhenMatchmakingExecutes() throws Exception {
        String region = uniqueRegion("NOTIFYASSIGNED");
        Long leadId = createLead(region);
        Long sellerId = createSeller("Notification Seller", region);

        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(adminToken())
                .queryParam("leadId", leadId)
                .queryParam("sellerId", sellerId)
                .queryParam("event", "LEAD_ASSIGNED")
                .queryParam("status", "SENT")
                .when().get("/api/v1/notifications")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(2))
                .body("content[0].leadId", equalTo(leadId.intValue()))
                .body("content[0].sellerId", equalTo(sellerId.intValue()))
                .body("content[0].event", equalTo("LEAD_ASSIGNED"))
                .body("content[0].status", equalTo("SENT"))
                .body("content[0].sentAt", notNullValue());
    }

    @Test
    void shouldCreateAcceptedNotifications() throws Exception {
        String region = uniqueRegion("NOTIFYACCEPTED");
        Long leadId = createLead(region);
        Long sellerId = createSeller("Accepted Notification Seller", region);
        executeMatchmaking(leadId);

        given()
                .auth().oauth2(adminToken())
                .contentType("application/json")
                .body("""
                        {
                          "leadId": %d,
                          "sellerId": %d
                        }
                        """.formatted(leadId, sellerId))
                .when().post("/api/v1/matchmaking/accept")
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(adminToken())
                .queryParam("leadId", leadId)
                .queryParam("event", "LEAD_ACCEPTED")
                .when().get("/api/v1/notifications")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(2))
                .body("content[0].event", equalTo("LEAD_ACCEPTED"))
                .body("content[0].status", equalTo("SENT"));
    }

    @Test
    void shouldCreateRejectedAndReassignedNotifications() throws Exception {
        String region = uniqueRegion("NOTIFYREJECTED");
        Long leadId = createLead(region);
        Long firstSellerId = createSeller("Rejected Notification Seller", region);
        Long nextSellerId = createSeller("Reassigned Notification Seller", region);
        executeMatchmaking(leadId);

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
                .body("nextSellerId", equalTo(nextSellerId.intValue()));

        given()
                .auth().oauth2(adminToken())
                .queryParam("leadId", leadId)
                .queryParam("event", "LEAD_REJECTED")
                .when().get("/api/v1/notifications")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(2))
                .body("content[0].sellerId", equalTo(firstSellerId.intValue()))
                .body("content[0].status", equalTo("SENT"));

        given()
                .auth().oauth2(adminToken())
                .queryParam("leadId", leadId)
                .queryParam("sellerId", nextSellerId)
                .queryParam("event", "LEAD_REASSIGNED")
                .when().get("/api/v1/notifications")
                .then()
                .statusCode(200)
                .body("content.size()", equalTo(2))
                .body("content[0].sellerId", equalTo(nextSellerId.intValue()))
                .body("content[0].status", equalTo("SENT"));
    }

    @Test
    void shouldProtectNotificationsEndpoint() {
        given()
                .when().get("/api/v1/notifications")
                .then()
                .statusCode(401);
    }

    private void executeMatchmaking(Long leadId) {
        given()
                .auth().oauth2(adminToken())
                .when().post("/api/v1/matchmaking/execute/{leadId}", leadId)
                .then()
                .statusCode(200);
    }

    private Long createSeller(String name, String region) throws Exception {
        userTransaction.begin();
        try {
            Seller seller = new Seller();
            seller.name = name;
            seller.email = uniqueEmail("notification-seller");
            seller.region = region;
            seller.specialization = "AUTOMOTIVO";
            seller.dailyCapacity = 10;
            seller.active = true;
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
            lead.name = "Notification Lead";
            lead.email = uniqueEmail("notification-lead");
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
