package com.company.leaddistribution.matchmaking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RejectAssignmentRequest(
        @NotNull Long leadId,
        @NotNull Long sellerId,
        @Size(max = 500) String reason
) {
}
