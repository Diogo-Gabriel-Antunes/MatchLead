package com.company.leaddistribution.lead.resource;

import com.company.leaddistribution.lead.dto.LeadPageResponse;
import com.company.leaddistribution.lead.dto.LeadRequest;
import com.company.leaddistribution.lead.dto.LeadResponse;
import com.company.leaddistribution.lead.dto.LeadStatusUpdateRequest;
import com.company.leaddistribution.lead.dto.LeadStatusUpdateResponse;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.service.LeadService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/leads")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Leads")
public class LeadResource {

    private final LeadService leadService;

    public LeadResource(LeadService leadService) {
        this.leadService = leadService;
    }

    @GET
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "List leads")
    public LeadPageResponse list(
            @QueryParam("status") LeadStatus status,
            @QueryParam("region") String region,
            @QueryParam("source") String source,
            @QueryParam("sellerId") Long sellerId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        return leadService.list(status, region, source, sellerId, page, size);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "Get lead details")
    public LeadResponse findById(@PathParam("id") Long id) {
        return leadService.findById(id);
    }

    @POST
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Create lead")
    public Response create(@Valid LeadRequest request) {
        LeadResponse response = leadService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Update lead")
    public LeadResponse update(@PathParam("id") Long id, @Valid LeadRequest request) {
        return leadService.update(id, request);
    }

    @PATCH
    @Path("/{id}/status")
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "Update lead status")
    public LeadStatusUpdateResponse updateStatus(
            @PathParam("id") Long id,
            @Valid LeadStatusUpdateRequest request
    ) {
        return leadService.updateStatus(id, request);
    }
}
