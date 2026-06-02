package com.company.leaddistribution.lead.dto;

import java.util.List;

public record LeadPageResponse(
        List<LeadListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
