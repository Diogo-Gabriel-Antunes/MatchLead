package com.company.leaddistribution.auth.dto;

import com.company.leaddistribution.auth.entity.Role;

public record LoginResponse(
        String accessToken,
        long expiresIn,
        Role role
) {
}
