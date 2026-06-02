package com.company.leaddistribution.notification.dto;

import com.company.leaddistribution.notification.entity.NotificationEvent;
import com.company.leaddistribution.notification.entity.NotificationStatus;
import com.company.leaddistribution.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long leadId,
        Long sellerId,
        String recipient,
        NotificationType type,
        NotificationEvent event,
        NotificationStatus status,
        LocalDateTime sentAt
) {
}
