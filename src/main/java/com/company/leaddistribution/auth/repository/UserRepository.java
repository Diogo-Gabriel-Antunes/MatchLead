package com.company.leaddistribution.auth.repository;

import com.company.leaddistribution.auth.entity.User;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, Long> {

    public Optional<User> findByEmail(String email) {
        return find("lower(email)", email.toLowerCase()).firstResultOptional();
    }
}
