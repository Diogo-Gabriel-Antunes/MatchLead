package com.company.leaddistribution.auth.resource;

import com.company.leaddistribution.auth.dto.CurrentUserResponse;
import com.company.leaddistribution.auth.dto.LoginRequest;
import com.company.leaddistribution.auth.dto.LoginResponse;
import com.company.leaddistribution.auth.service.AuthService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/v1/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Auth")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Authenticate user")
    public LoginResponse login(@Valid LoginRequest request) {
        return authService.login(request);
    }

    @GET
    @Path("/me")
    @RolesAllowed({"ADMIN", "MANAGER", "SELLER"})
    @Operation(summary = "Get current authenticated user")
    public CurrentUserResponse me() {
        return authService.currentUser();
    }
}
