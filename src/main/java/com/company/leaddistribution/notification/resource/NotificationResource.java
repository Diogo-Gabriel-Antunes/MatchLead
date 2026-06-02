package com.company.leaddistribution.notification.resource;

import com.company.leaddistribution.notification.dto.NotificationPageResponse;
import com.company.leaddistribution.notification.entity.NotificationEvent;
import com.company.leaddistribution.notification.entity.NotificationStatus;
import com.company.leaddistribution.notification.service.NotificationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Notifications")
public class NotificationResource {

    private final NotificationService notificationService;

    public NotificationResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GET
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "List notifications")
    public NotificationPageResponse list(
            @QueryParam("leadId") Long leadId,
            @QueryParam("sellerId") Long sellerId,
            @QueryParam("event") NotificationEvent event,
            @QueryParam("status") NotificationStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        return notificationService.list(leadId, sellerId, event, status, page, size);
    }
}
