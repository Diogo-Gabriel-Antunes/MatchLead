package com.company.leaddistribution.assignment.repository;

import com.company.leaddistribution.assignment.entity.Assignment;
import com.company.leaddistribution.assignment.entity.AssignmentStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AssignmentRepository implements PanacheRepositoryBase<Assignment, Long> {

    public Optional<Assignment> findActiveByLeadId(Long leadId) {
        return find("lead.id = ?1 and status = 'PENDING'", leadId).firstResultOptional();
    }

    public Optional<Assignment> findPendingByLeadIdAndSellerId(Long leadId, Long sellerId) {
        return find("lead.id = ?1 and seller.id = ?2 and status = 'PENDING'", leadId, sellerId)
                .firstResultOptional();
    }

    public boolean hasActiveAssignment(Long leadId) {
        return findActiveByLeadId(leadId).isPresent();
    }

    public List<Long> findSellerIdsByLeadIdAndStatus(Long leadId, AssignmentStatus status) {
        return find("lead.id = ?1 and status = ?2", leadId, status).list()
                .stream()
                .map(assignment -> assignment.seller.id)
                .toList();
    }
}
