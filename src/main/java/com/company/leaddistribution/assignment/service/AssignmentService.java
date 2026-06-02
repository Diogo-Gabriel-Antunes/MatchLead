package com.company.leaddistribution.assignment.service;

import com.company.leaddistribution.assignment.entity.Assignment;
import com.company.leaddistribution.assignment.entity.AssignmentStatus;
import com.company.leaddistribution.assignment.repository.AssignmentRepository;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.service.LeadHistoryService;
import com.company.leaddistribution.seller.entity.Seller;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LeadHistoryService leadHistoryService;

    public AssignmentService(AssignmentRepository assignmentRepository, LeadHistoryService leadHistoryService) {
        this.assignmentRepository = assignmentRepository;
        this.leadHistoryService = leadHistoryService;
    }

    public Assignment assignLead(Lead lead, Seller seller) {
        if (assignmentRepository.hasActiveAssignment(lead.id)) {
            throw new WebApplicationException("Lead already has an active assignment", Response.Status.CONFLICT);
        }

        LocalDateTime now = LocalDateTime.now();
        Assignment assignment = new Assignment();
        assignment.lead = lead;
        assignment.seller = seller;
        assignment.status = AssignmentStatus.PENDING;
        assignment.assignedAt = now;
        assignment.createdAt = now;
        assignment.updatedAt = now;

        lead.seller = seller;
        lead.status = LeadStatus.ASSIGNED;
        lead.updatedAt = now;

        assignmentRepository.persist(assignment);
        leadHistoryService.recordAssigned(lead, null, "Lead atribuído ao vendedor " + seller.name);
        return assignment;
    }

    public Optional<Assignment> findActiveByLeadId(Long leadId) {
        return assignmentRepository.findActiveByLeadId(leadId);
    }
}
