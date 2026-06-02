package com.company.leaddistribution.lead.service;

import com.company.leaddistribution.auth.entity.Role;
import com.company.leaddistribution.lead.dto.LeadHistoryResponse;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadHistory;
import com.company.leaddistribution.lead.entity.LeadHistoryEventType;
import com.company.leaddistribution.lead.mapper.LeadHistoryMapper;
import com.company.leaddistribution.lead.repository.LeadHistoryRepository;
import com.company.leaddistribution.lead.repository.LeadRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;

@ApplicationScoped
public class LeadHistoryService {

    private final LeadHistoryRepository leadHistoryRepository;
    private final LeadRepository leadRepository;
    private final JsonWebToken jwt;

    public LeadHistoryService(
            LeadHistoryRepository leadHistoryRepository,
            LeadRepository leadRepository,
            JsonWebToken jwt
    ) {
        this.leadHistoryRepository = leadHistoryRepository;
        this.leadRepository = leadRepository;
        this.jwt = jwt;
    }

    public LeadHistoryResponse findByLeadId(Long leadId) {
        Lead lead = leadRepository.findByIdOptional(leadId)
                .orElseThrow(() -> new NotFoundException("Lead not found"));
        validateHistoryVisibility(lead);
        return LeadHistoryMapper.toResponse(leadId, leadHistoryRepository.findByLeadId(leadId));
    }

    public void recordCreated(Lead lead) {
        record(lead, LeadHistoryEventType.LEAD_CREATED, null, "Lead criado");
    }

    public void recordUpdated(Lead lead, String previousValue, String newValue) {
        record(lead, LeadHistoryEventType.LEAD_UPDATED, previousValue, newValue);
    }

    public void recordStatusChanged(Lead lead, String previousValue, String newValue) {
        record(lead, LeadHistoryEventType.LEAD_STATUS_CHANGED, previousValue, newValue);
    }

    public void recordAssigned(Lead lead, String previousValue, String newValue) {
        record(lead, LeadHistoryEventType.LEAD_ASSIGNED, previousValue, newValue);
    }

    public void recordAccepted(Lead lead, String previousValue, String newValue) {
        record(lead, LeadHistoryEventType.LEAD_ACCEPTED, previousValue, newValue);
    }

    public void recordRejected(Lead lead, String previousValue, String newValue) {
        record(lead, LeadHistoryEventType.LEAD_REJECTED, previousValue, newValue);
    }

    private void record(Lead lead, LeadHistoryEventType type, String previousValue, String newValue) {
        LeadHistory history = new LeadHistory();
        history.lead = lead;
        history.type = type;
        history.previousValue = previousValue;
        history.newValue = newValue;
        history.createdAt = LocalDateTime.now();
        leadHistoryRepository.persist(history);
    }

    private void validateHistoryVisibility(Lead lead) {
        if (!jwt.getGroups().contains(Role.SELLER.name())) {
            return;
        }
        if (lead.seller == null || !lead.seller.email.equalsIgnoreCase(jwt.getName())) {
            throw new ForbiddenException("Lead history is not available for this seller");
        }
    }
}
