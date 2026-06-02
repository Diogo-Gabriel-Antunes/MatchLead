package com.company.leaddistribution.assignment.mapper;

import com.company.leaddistribution.assignment.dto.AssignmentResponse;
import com.company.leaddistribution.assignment.entity.Assignment;

public final class AssignmentMapper {

    private AssignmentMapper() {
    }

    public static AssignmentResponse toResponse(Assignment assignment) {
        return new AssignmentResponse(
                assignment.id,
                assignment.lead.id,
                assignment.seller.id,
                assignment.status,
                assignment.assignedAt
        );
    }
}
