package com.company.leaddistribution.auth.dto;

import com.company.leaddistribution.auth.entity.Role;

public record CurrentUserResponse(
        Long id,
        String name,
        String email,
        Role role
) {
}
