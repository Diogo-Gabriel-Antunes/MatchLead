package com.company.leaddistribution.seller.dto;

import java.util.List;

public record SellerPageResponse(
        List<SellerListItemResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
