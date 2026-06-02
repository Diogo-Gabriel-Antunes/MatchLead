package com.company.leaddistribution.matchmaking.service;

import com.company.leaddistribution.seller.entity.Seller;

public record SellerCandidate(
        Seller seller,
        int rankingScore,
        long currentLoad
) {
}
