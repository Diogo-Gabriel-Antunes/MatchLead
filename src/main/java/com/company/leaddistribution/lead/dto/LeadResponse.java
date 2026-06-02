package com.company.leaddistribution.lead.dto;

import com.company.leaddistribution.lead.entity.LeadStatus;

import java.time.LocalDateTime;

public record LeadResponse(
        Long id,
        String name,
        String email,
        String phone,
        String source,
        String region,
        LeadStatus status,
        LeadSellerResponse seller,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
