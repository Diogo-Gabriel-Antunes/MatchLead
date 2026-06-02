package com.company.leaddistribution.matchmaking.service;

import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.repository.LeadRepository;
import com.company.leaddistribution.lead.service.LeadHistoryService;
import com.company.leaddistribution.matchmaking.dto.MatchmakingResponse;
import com.company.leaddistribution.matchmaking.dto.SellerRankingResponse;
import com.company.leaddistribution.matchmaking.mapper.MatchmakingMapper;
import com.company.leaddistribution.seller.entity.Seller;
import com.company.leaddistribution.seller.repository.SellerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class MatchmakingService {

    private static final String NO_ELIGIBLE_SELLER_MESSAGE = "Nenhum vendedor elegível foi encontrado";
    private static final String ELIGIBLE_SELLER_FOUND_MESSAGE = "Vendedor elegível encontrado";

    private final LeadRepository leadRepository;
    private final SellerRepository sellerRepository;
    private final LeadHistoryService leadHistoryService;

    public MatchmakingService(
            LeadRepository leadRepository,
            SellerRepository sellerRepository,
            LeadHistoryService leadHistoryService
    ) {
        this.leadRepository = leadRepository;
        this.sellerRepository = sellerRepository;
        this.leadHistoryService = leadHistoryService;
    }

    @Transactional
    public MatchmakingResponse execute(Long leadId) {
        Lead lead = leadRepository.findByIdOptional(leadId)
                .orElseThrow(() -> new NotFoundException("Lead not found"));

        MatchmakingResponse response = buildResponse(lead);
        leadHistoryService.recordUpdated(
                lead,
                null,
                "Matchmaking executado: " + response.ranking().size() + " vendedor(es) elegível(eis)"
        );
        return response;
    }

    public MatchmakingResponse ranking(Long leadId) {
        Lead lead = leadRepository.findByIdOptional(leadId)
                .orElseThrow(() -> new NotFoundException("Lead not found"));
        return buildResponse(lead);
    }

    private MatchmakingResponse buildResponse(Lead lead) {
        List<SellerCandidate> candidates = rankEligibleSellers(lead);
        List<SellerRankingResponse> ranking = MatchmakingMapper.toRankingResponse(candidates);

        if (ranking.isEmpty()) {
            return new MatchmakingResponse(
                    lead.id,
                    0,
                    null,
                    null,
                    List.of(),
                    NO_ELIGIBLE_SELLER_MESSAGE
            );
        }

        SellerRankingResponse selected = ranking.getFirst();
        return new MatchmakingResponse(
                lead.id,
                selected.rankingScore(),
                selected.sellerId(),
                selected.sellerName(),
                ranking,
                ELIGIBLE_SELLER_FOUND_MESSAGE
        );
    }

    List<SellerCandidate> rankEligibleSellers(Lead lead) {
        LocalDate today = LocalDate.now();
        return sellerRepository.findActiveSellers().stream()
                .filter(seller -> isRegionCompatible(lead, seller))
                .map(seller -> toCandidate(seller, currentLoad(seller, today)))
                .filter(candidate -> candidate.currentLoad() < candidate.seller().dailyCapacity)
                .sorted(candidateComparator())
                .toList();
    }

    private SellerCandidate toCandidate(Seller seller, long currentLoad) {
        int capacityScore = capacityScore(seller.dailyCapacity, currentLoad);
        int score = 40 + 30 + capacityScore + 10;
        return new SellerCandidate(seller, score, currentLoad);
    }

    private long currentLoad(Seller seller, LocalDate today) {
        return leadRepository.countAssignedTodayBySeller(
                seller.id,
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        );
    }

    private Comparator<SellerCandidate> candidateComparator() {
        return Comparator
                .comparingInt(SellerCandidate::rankingScore).reversed()
                .thenComparingLong(SellerCandidate::currentLoad)
                .thenComparing(candidate -> candidate.seller().id);
    }

    private boolean isRegionCompatible(Lead lead, Seller seller) {
        return seller.region.equalsIgnoreCase(lead.region);
    }

    private int capacityScore(Integer dailyCapacity, long currentLoad) {
        if (dailyCapacity == null || dailyCapacity <= 0) {
            return 0;
        }
        long availableCapacity = Math.max(dailyCapacity - currentLoad, 0);
        return (int) Math.round((availableCapacity * 20.0) / dailyCapacity);
    }
}
