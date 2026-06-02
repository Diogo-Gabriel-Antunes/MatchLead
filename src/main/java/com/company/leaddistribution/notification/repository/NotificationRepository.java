package com.company.leaddistribution.notification.repository;

import com.company.leaddistribution.notification.entity.Notification;
import com.company.leaddistribution.notification.entity.NotificationEvent;
import com.company.leaddistribution.notification.entity.NotificationStatus;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class NotificationRepository implements PanacheRepositoryBase<Notification, Long> {

    public PanacheQuery<Notification> findFiltered(
            Long leadId,
            Long sellerId,
            NotificationEvent event,
            NotificationStatus status
    ) {
        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (leadId != null) {
            query.append(" and lead.id = :leadId");
            params.put("leadId", leadId);
        }
        if (sellerId != null) {
            query.append(" and seller.id = :sellerId");
            params.put("sellerId", sellerId);
        }
        if (event != null) {
            query.append(" and event = :event");
            params.put("event", event);
        }
        if (status != null) {
            query.append(" and status = :status");
            params.put("status", status);
        }

        query.append(" order by createdAt desc, id desc");
        return find(query.toString(), params);
    }
}
