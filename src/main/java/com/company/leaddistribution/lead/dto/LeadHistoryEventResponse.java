package com.company.leaddistribution.lead.dto;

import com.company.leaddistribution.lead.entity.LeadHistoryEventType;

import java.time.LocalDateTime;

public record LeadHistoryEventResponse(
        LeadHistoryEventType type,
        String previousValue,
        String newValue,
        LocalDateTime date
) {
}
