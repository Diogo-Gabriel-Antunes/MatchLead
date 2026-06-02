package com.company.leaddistribution.lead.dto;

import com.company.leaddistribution.lead.entity.LeadStatus;

import java.time.LocalDateTime;

public record LeadListItemResponse(
        Long id,
        String name,
        String source,
        String region,
        LeadStatus status,
        Long sellerId,
        LocalDateTime createdAt
) {
}
