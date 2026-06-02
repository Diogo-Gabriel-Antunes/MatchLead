package com.company.leaddistribution.matchmaking;

import com.company.leaddistribution.matchmaking.dto.SellerRankingResponse;
import com.company.leaddistribution.matchmaking.mapper.MatchmakingMapper;
import com.company.leaddistribution.matchmaking.service.SellerCandidate;
import com.company.leaddistribution.seller.entity.Seller;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchmakingMapperTest {

    @Test
    void shouldMapCandidatesToRankingWithPositions() {
        Seller firstSeller = seller(10L, "Joao Silva");
        Seller secondSeller = seller(20L, "Maria Oliveira");

        List<SellerRankingResponse> ranking = MatchmakingMapper.toRankingResponse(List.of(
                new SellerCandidate(firstSeller, 100, 0),
                new SellerCandidate(secondSeller, 95, 1)
        ));

        assertEquals(1, ranking.getFirst().position());
        assertEquals(10L, ranking.getFirst().sellerId());
        assertEquals("Joao Silva", ranking.getFirst().sellerName());
        assertEquals(100, ranking.getFirst().rankingScore());
        assertEquals(2, ranking.get(1).position());
    }

    private Seller seller(Long id, String name) {
        Seller seller = new Seller();
        seller.id = id;
        seller.name = name;
        return seller;
    }
}
