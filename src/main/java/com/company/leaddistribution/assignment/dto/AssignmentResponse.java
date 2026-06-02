package com.company.leaddistribution.assignment.dto;

import com.company.leaddistribution.assignment.entity.AssignmentStatus;

import java.time.LocalDateTime;

public record AssignmentResponse(
        Long id,
        Long leadId,
        Long sellerId,
        AssignmentStatus status,
        LocalDateTime assignedAt
) {
}
