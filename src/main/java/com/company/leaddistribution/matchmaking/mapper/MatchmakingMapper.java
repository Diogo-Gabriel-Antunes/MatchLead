package com.company.leaddistribution.matchmaking.mapper;

import com.company.leaddistribution.matchmaking.dto.SellerRankingResponse;
import com.company.leaddistribution.matchmaking.service.SellerCandidate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class MatchmakingMapper {

    private MatchmakingMapper() {
    }

    public static List<SellerRankingResponse> toRankingResponse(List<SellerCandidate> candidates) {
        AtomicInteger position = new AtomicInteger(1);
        return candidates.stream()
                .map(candidate -> new SellerRankingResponse(
                        position.getAndIncrement(),
                        candidate.seller().id,
                        candidate.seller().name,
                        candidate.rankingScore()
                ))
                .toList();
    }
}
