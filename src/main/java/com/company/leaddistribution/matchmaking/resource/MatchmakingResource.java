package com.company.leaddistribution.matchmaking.resource;

import com.company.leaddistribution.matchmaking.dto.MatchmakingResponse;
import com.company.leaddistribution.matchmaking.dto.AcceptAssignmentResponse;
import com.company.leaddistribution.matchmaking.dto.AssignmentActionRequest;
import com.company.leaddistribution.matchmaking.dto.RejectAssignmentRequest;
import com.company.leaddistribution.matchmaking.dto.RejectAssignmentResponse;
import com.company.leaddistribution.matchmaking.service.MatchmakingService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/matchmaking")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Matchmaking")
public class MatchmakingResource {

    private final MatchmakingService matchmakingService;

    public MatchmakingResource(MatchmakingService matchmakingService) {
        this.matchmakingService = matchmakingService;
    }

    @POST
    @Path("/execute/{leadId}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Execute matchmaking for a lead")
    public MatchmakingResponse execute(@PathParam("leadId") Long leadId) {
        return matchmakingService.execute(leadId);
    }

    @GET
    @Path("/ranking/{leadId}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Get matchmaking ranking for a lead")
    public MatchmakingResponse ranking(@PathParam("leadId") Long leadId) {
        return matchmakingService.ranking(leadId);
    }

    @POST
    @Path("/accept")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "Accept lead assignment")
    public AcceptAssignmentResponse accept(@Valid AssignmentActionRequest request) {
        return matchmakingService.accept(request.leadId(), request.sellerId());
    }

    @POST
    @Path("/reject")
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "Reject lead assignment")
    public RejectAssignmentResponse reject(@Valid RejectAssignmentRequest request) {
        return matchmakingService.reject(request.leadId(), request.sellerId(), request.reason());
    }
}
