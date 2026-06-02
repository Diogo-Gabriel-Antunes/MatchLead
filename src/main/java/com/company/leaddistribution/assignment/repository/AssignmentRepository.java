package com.company.leaddistribution.assignment.repository;

import com.company.leaddistribution.assignment.entity.Assignment;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class AssignmentRepository implements PanacheRepositoryBase<Assignment, Long> {

    public Optional<Assignment> findActiveByLeadId(Long leadId) {
        return find("lead.id = ?1 and status = 'PENDING'", leadId).firstResultOptional();
    }

    public boolean hasActiveAssignment(Long leadId) {
        return findActiveByLeadId(leadId).isPresent();
    }
}
