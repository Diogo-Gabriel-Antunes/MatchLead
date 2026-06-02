package com.company.leaddistribution.lead.dto;

import com.company.leaddistribution.lead.entity.LeadStatus;
import jakarta.validation.constraints.NotNull;

public record LeadStatusUpdateRequest(
        @NotNull LeadStatus status
) {
}
