package com.company.leaddistribution.assignment.service;

import com.company.leaddistribution.assignment.entity.Assignment;
import com.company.leaddistribution.assignment.entity.AssignmentStatus;
import com.company.leaddistribution.assignment.repository.AssignmentRepository;
import com.company.leaddistribution.auth.entity.Role;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.service.LeadHistoryService;
import com.company.leaddistribution.seller.entity.Seller;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final LeadHistoryService leadHistoryService;
    private final JsonWebToken jwt;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            LeadHistoryService leadHistoryService,
            JsonWebToken jwt
    ) {
        this.assignmentRepository = assignmentRepository;
        this.leadHistoryService = leadHistoryService;
        this.jwt = jwt;
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

    public Assignment accept(Long leadId, Long sellerId) {
        Assignment assignment = findPendingAssignment(leadId, sellerId);
        validateSellerCanOperate(assignment);

        LocalDateTime now = LocalDateTime.now();
        assignment.status = AssignmentStatus.ACCEPTED;
        assignment.acceptedAt = now;
        assignment.updatedAt = now;
        assignment.lead.status = LeadStatus.ASSIGNED;
        assignment.lead.seller = assignment.seller;
        assignment.lead.updatedAt = now;

        leadHistoryService.recordAccepted(assignment.lead, null, "Lead aceito pelo vendedor " + assignment.seller.name);
        return assignment;
    }

    public Assignment reject(Long leadId, Long sellerId, String reason) {
        Assignment assignment = findPendingAssignment(leadId, sellerId);
        validateSellerCanOperate(assignment);

        LocalDateTime now = LocalDateTime.now();
        assignment.status = AssignmentStatus.REJECTED;
        assignment.rejectedAt = now;
        assignment.updatedAt = now;

        String message = "Lead recusado pelo vendedor " + assignment.seller.name;
        if (reason != null && !reason.isBlank()) {
            message = message + ". Motivo: " + reason.trim();
        }
        leadHistoryService.recordRejected(assignment.lead, null, message);
        return assignment;
    }

    public List<Long> findRejectedSellerIds(Long leadId) {
        return assignmentRepository.findSellerIdsByLeadIdAndStatus(leadId, AssignmentStatus.REJECTED);
    }

    private Assignment findPendingAssignment(Long leadId, Long sellerId) {
        return assignmentRepository.findPendingByLeadIdAndSellerId(leadId, sellerId)
                .orElseThrow(() -> new NotFoundException("Pending assignment not found"));
    }

    private void validateSellerCanOperate(Assignment assignment) {
        if (!jwt.getGroups().contains(Role.SELLER.name())) {
            return;
        }
        if (!assignment.seller.email.equalsIgnoreCase(jwt.getName())) {
            throw new ForbiddenException("Seller cannot operate another seller assignment");
        }
    }
}
