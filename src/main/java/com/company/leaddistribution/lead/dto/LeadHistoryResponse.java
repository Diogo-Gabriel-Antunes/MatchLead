package com.company.leaddistribution.lead.dto;

import java.util.List;

public record LeadHistoryResponse(
        Long leadId,
        List<LeadHistoryEventResponse> events
) {
}
