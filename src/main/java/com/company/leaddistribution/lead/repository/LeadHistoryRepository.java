package com.company.leaddistribution.lead.repository;

import com.company.leaddistribution.lead.entity.LeadHistory;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class LeadHistoryRepository implements PanacheRepositoryBase<LeadHistory, Long> {

    public List<LeadHistory> findByLeadId(Long leadId) {
        return find("lead.id = ?1 order by createdAt desc, id desc", leadId).list();
    }
}
