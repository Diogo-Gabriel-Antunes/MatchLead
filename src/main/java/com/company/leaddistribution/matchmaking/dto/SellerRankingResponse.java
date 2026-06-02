package com.company.leaddistribution.matchmaking.dto;

public record SellerRankingResponse(
        int position,
        Long sellerId,
        String sellerName,
        int rankingScore
) {
}
