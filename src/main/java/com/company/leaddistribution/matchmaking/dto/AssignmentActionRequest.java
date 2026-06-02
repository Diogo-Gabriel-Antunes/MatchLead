package com.company.leaddistribution.matchmaking.dto;

import jakarta.validation.constraints.NotNull;

public record AssignmentActionRequest(
        @NotNull Long leadId,
        @NotNull Long sellerId
) {
}
