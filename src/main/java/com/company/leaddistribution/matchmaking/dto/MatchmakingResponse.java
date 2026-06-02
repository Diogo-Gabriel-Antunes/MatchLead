package com.company.leaddistribution.matchmaking.dto;

import java.util.List;

public record MatchmakingResponse(
        Long leadId,
        int score,
        Long selectedSellerId,
        String selectedSellerName,
        List<SellerRankingResponse> ranking,
        String message
) {
}
