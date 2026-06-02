package com.company.leaddistribution.assignment;

import com.company.leaddistribution.assignment.dto.AssignmentResponse;
import com.company.leaddistribution.assignment.entity.Assignment;
import com.company.leaddistribution.assignment.entity.AssignmentStatus;
import com.company.leaddistribution.assignment.mapper.AssignmentMapper;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.seller.entity.Seller;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssignmentMapperTest {

    @Test
    void shouldMapAssignmentToResponse() {
        Lead lead = new Lead();
        lead.id = 10L;
        Seller seller = new Seller();
        seller.id = 20L;
        LocalDateTime assignedAt = LocalDateTime.now();

        Assignment assignment = new Assignment();
        assignment.id = 30L;
        assignment.lead = lead;
        assignment.seller = seller;
        assignment.status = AssignmentStatus.PENDING;
        assignment.assignedAt = assignedAt;

        AssignmentResponse response = AssignmentMapper.toResponse(assignment);

        assertEquals(30L, response.id());
        assertEquals(10L, response.leadId());
        assertEquals(20L, response.sellerId());
        assertEquals(AssignmentStatus.PENDING, response.status());
        assertEquals(assignedAt, response.assignedAt());
    }
}
