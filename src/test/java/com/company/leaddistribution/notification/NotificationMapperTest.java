package com.company.leaddistribution.notification;

import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.notification.dto.NotificationResponse;
import com.company.leaddistribution.notification.entity.Notification;
import com.company.leaddistribution.notification.entity.NotificationEvent;
import com.company.leaddistribution.notification.entity.NotificationStatus;
import com.company.leaddistribution.notification.entity.NotificationType;
import com.company.leaddistribution.notification.mapper.NotificationMapper;
import com.company.leaddistribution.seller.entity.Seller;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NotificationMapperTest {

    @Test
    void shouldMapNotificationToResponse() {
        Lead lead = new Lead();
        lead.id = 10L;
        Seller seller = new Seller();
        seller.id = 20L;
        LocalDateTime sentAt = LocalDateTime.now();

        Notification notification = new Notification();
        notification.id = 30L;
        notification.lead = lead;
        notification.seller = seller;
        notification.recipient = "seller@email.com";
        notification.type = NotificationType.EMAIL;
        notification.event = NotificationEvent.LEAD_ASSIGNED;
        notification.status = NotificationStatus.SENT;
        notification.sentAt = sentAt;

        NotificationResponse response = NotificationMapper.toResponse(notification);

        assertEquals(30L, response.id());
        assertEquals(10L, response.leadId());
        assertEquals(20L, response.sellerId());
        assertEquals("seller@email.com", response.recipient());
        assertEquals(NotificationType.EMAIL, response.type());
        assertEquals(NotificationEvent.LEAD_ASSIGNED, response.event());
        assertEquals(NotificationStatus.SENT, response.status());
        assertEquals(sentAt, response.sentAt());
    }
}
