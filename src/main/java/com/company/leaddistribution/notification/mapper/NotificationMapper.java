package com.company.leaddistribution.notification.mapper;

import com.company.leaddistribution.notification.dto.NotificationResponse;
import com.company.leaddistribution.notification.entity.Notification;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    public static NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.id,
                notification.lead.id,
                notification.seller.id,
                notification.recipient,
                notification.type,
                notification.event,
                notification.status,
                notification.sentAt
        );
    }
}
