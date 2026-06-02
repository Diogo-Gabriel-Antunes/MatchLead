package com.company.leaddistribution.lead.repository;

import com.company.leaddistribution.lead.entity.Lead;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LeadRepository implements PanacheRepositoryBase<Lead, Long> {

    public boolean existsByEmail(String email) {
        return email != null && count("email", email) > 0;
    }

    public boolean existsByPhone(String phone) {
        return phone != null && count("phone", phone) > 0;
    }

    public boolean existsByEmailAndDifferentId(String email, Long id) {
        return email != null && count("email = ?1 and id <> ?2", email, id) > 0;
    }

    public boolean existsByPhoneAndDifferentId(String phone, Long id) {
        return phone != null && count("phone = ?1 and id <> ?2", phone, id) > 0;
    }
}
