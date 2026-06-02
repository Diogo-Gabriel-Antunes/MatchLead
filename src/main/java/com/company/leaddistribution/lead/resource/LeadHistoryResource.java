package com.company.leaddistribution.lead.resource;

import com.company.leaddistribution.lead.dto.LeadHistoryResponse;
import com.company.leaddistribution.lead.service.LeadHistoryService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/leads/{leadId}/history")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Lead History")
public class LeadHistoryResource {

    private final LeadHistoryService leadHistoryService;

    public LeadHistoryResource(LeadHistoryService leadHistoryService) {
        this.leadHistoryService = leadHistoryService;
    }

    @GET
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "Get lead history")
    public LeadHistoryResponse findByLeadId(@PathParam("leadId") Long leadId) {
        return leadHistoryService.findByLeadId(leadId);
    }
}
