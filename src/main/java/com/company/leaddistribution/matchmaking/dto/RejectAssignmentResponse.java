package com.company.leaddistribution.matchmaking.dto;

import com.company.leaddistribution.assignment.entity.AssignmentStatus;

public record RejectAssignmentResponse(
        boolean success,
        AssignmentStatus status,
        Long nextAssignmentId,
        Long nextSellerId,
        String message
) {
}
