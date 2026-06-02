package com.company.leaddistribution.seller.resource;

import com.company.leaddistribution.seller.dto.SellerPageResponse;
import com.company.leaddistribution.seller.dto.SellerRequest;
import com.company.leaddistribution.seller.dto.SellerResponse;
import com.company.leaddistribution.seller.service.SellerService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
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

@Path("/api/v1/sellers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Sellers")
public class SellerResource {

    private final SellerService sellerService;

    public SellerResource(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @GET
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "List sellers")
    public SellerPageResponse list(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        return sellerService.list(page, size);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "Get seller details")
    public SellerResponse findById(@PathParam("id") Long id) {
        return sellerService.findById(id);
    }

    @POST
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Create seller")
    public Response create(@Valid SellerRequest request) {
        SellerResponse response = sellerService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Update seller")
    public SellerResponse update(@PathParam("id") Long id, @Valid SellerRequest request) {
        return sellerService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "MANAGER"})
    @Operation(summary = "Deactivate seller")
    public Response deactivate(@PathParam("id") Long id) {
        sellerService.deactivate(id);
        return Response.noContent().build();
    }
}
