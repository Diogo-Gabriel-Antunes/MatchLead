package com.company.leaddistribution.lead.dto;

import com.company.leaddistribution.lead.entity.LeadStatus;

public record LeadStatusUpdateResponse(
        boolean success,
        LeadStatus status
) {
}
