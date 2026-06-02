package com.company.leaddistribution.seller.repository;

import com.company.leaddistribution.seller.entity.Seller;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SellerRepository implements PanacheRepositoryBase<Seller, Long> {

    public Optional<Seller> findByEmail(String email) {
        return find("email", email.toLowerCase()).firstResultOptional();
    }

    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    public boolean existsByEmailAndDifferentId(String email, Long id) {
        return count("email = ?1 and id <> ?2", email.toLowerCase(), id) > 0;
    }

    public List<Seller> findActiveSellers() {
        return find("active = true order by id asc").list();
    }
}
