package com.company.leaddistribution.matchmaking.dto;

import com.company.leaddistribution.assignment.entity.AssignmentStatus;

public record AcceptAssignmentResponse(
        boolean success,
        AssignmentStatus status
) {
}
