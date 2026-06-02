package com.company.leaddistribution.auth.mapper;

import com.company.leaddistribution.auth.dto.CurrentUserResponse;
import com.company.leaddistribution.auth.entity.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static CurrentUserResponse toCurrentUserResponse(User user) {
        return new CurrentUserResponse(user.id, user.name, user.email, user.role);
    }
}
