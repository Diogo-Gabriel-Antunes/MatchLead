package com.company.leaddistribution.seller.dto;

public record SellerListItemResponse(
        Long id,
        String name,
        String region,
        Boolean active
) {
}
