package com.company.leaddistribution.notification.service;

import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.notification.dto.NotificationPageResponse;
import com.company.leaddistribution.notification.entity.Notification;
import com.company.leaddistribution.notification.entity.NotificationEvent;
import com.company.leaddistribution.notification.entity.NotificationStatus;
import com.company.leaddistribution.notification.entity.NotificationType;
import com.company.leaddistribution.notification.mapper.NotificationMapper;
import com.company.leaddistribution.notification.repository.NotificationRepository;
import com.company.leaddistribution.seller.entity.Seller;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailNotificationSender emailNotificationSender;
    private final WebhookNotificationSender webhookNotificationSender;

    public NotificationService(
            NotificationRepository notificationRepository,
            EmailNotificationSender emailNotificationSender,
            WebhookNotificationSender webhookNotificationSender
    ) {
        this.notificationRepository = notificationRepository;
        this.emailNotificationSender = emailNotificationSender;
        this.webhookNotificationSender = webhookNotificationSender;
    }

    public NotificationPageResponse list(
            Long leadId,
            Long sellerId,
            NotificationEvent event,
            NotificationStatus status,
            int page,
            int size
    ) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        PanacheQuery<Notification> query = notificationRepository.findFiltered(leadId, sellerId, event, status);
        List<Notification> notifications = query.page(Page.of(normalizedPage, normalizedSize)).list();

        return new NotificationPageResponse(
                notifications.stream().map(NotificationMapper::toResponse).toList(),
                normalizedPage,
                normalizedSize,
                query.count(),
                query.pageCount()
        );
    }

    public void notifyLeadAssigned(Lead lead, Seller seller) {
        notify(lead, seller, NotificationEvent.LEAD_ASSIGNED);
    }

    public void notifyLeadAccepted(Lead lead, Seller seller) {
        notify(lead, seller, NotificationEvent.LEAD_ACCEPTED);
    }

    public void notifyLeadRejected(Lead lead, Seller seller) {
        notify(lead, seller, NotificationEvent.LEAD_REJECTED);
    }

    public void notifyLeadReassigned(Lead lead, Seller seller) {
        notify(lead, seller, NotificationEvent.LEAD_REASSIGNED);
    }

    private void notify(Lead lead, Seller seller, NotificationEvent event) {
        createAndSend(lead, seller, NotificationType.EMAIL, seller.email, event);
        createAndSend(lead, seller, NotificationType.WEBHOOK, "webhook://seller/" + seller.id, event);
    }

    private void createAndSend(
            Lead lead,
            Seller seller,
            NotificationType type,
            String recipient,
            NotificationEvent event
    ) {
        Notification notification = new Notification();
        notification.lead = lead;
        notification.seller = seller;
        notification.recipient = recipient;
        notification.type = type;
        notification.event = event;
        notification.status = NotificationStatus.PENDING;
        notification.payload = buildPayload(lead, seller, event);
        notification.createdAt = LocalDateTime.now();
        notificationRepository.persist(notification);

        try {
            send(notification);
            notification.status = NotificationStatus.SENT;
            notification.sentAt = LocalDateTime.now();
        } catch (RuntimeException exception) {
            notification.status = NotificationStatus.FAILED;
            notification.errorMessage = exception.getMessage();
        }
    }

    private void send(Notification notification) {
        if (notification.type == NotificationType.EMAIL) {
            emailNotificationSender.send(notification);
            return;
        }
        webhookNotificationSender.send(notification);
    }

    private String buildPayload(Lead lead, Seller seller, NotificationEvent event) {
        return "event=%s,leadId=%d,sellerId=%d,sellerName=%s".formatted(
                event,
                lead.id,
                seller.id,
                seller.name
        );
    }
}
