package com.company.leaddistribution.seller.dto;

public record SellerResponse(
        Long id,
        String name,
        String email,
        String region,
        String specialization,
        Integer dailyCapacity,
        Boolean active
) {
}
