package com.company.leaddistribution.matchmaking.service;

import com.company.leaddistribution.assignment.entity.Assignment;
import com.company.leaddistribution.assignment.service.AssignmentService;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.repository.LeadRepository;
import com.company.leaddistribution.matchmaking.dto.AcceptAssignmentResponse;
import com.company.leaddistribution.matchmaking.dto.RejectAssignmentResponse;
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
    private static final String NO_ELIGIBLE_SELLER_ASSIGNMENT_MESSAGE = "Nenhum vendedor elegível encontrado";
    private static final String ELIGIBLE_SELLER_FOUND_MESSAGE = "Vendedor elegível encontrado";
    private static final String LEAD_ALREADY_ASSIGNED_MESSAGE = "Lead já atribuído";
    private static final String REJECTED_AND_REPROCESSED_MESSAGE = "Lead recusado e reprocessado para o próximo vendedor";
    private static final String REJECTED_WITHOUT_NEXT_SELLER_MESSAGE = "Lead recusado, mas nenhum próximo vendedor elegível foi encontrado";

    private final LeadRepository leadRepository;
    private final SellerRepository sellerRepository;
    private final AssignmentService assignmentService;

    public MatchmakingService(
            LeadRepository leadRepository,
            SellerRepository sellerRepository,
            AssignmentService assignmentService
    ) {
        this.leadRepository = leadRepository;
        this.sellerRepository = sellerRepository;
        this.assignmentService = assignmentService;
    }

    @Transactional
    public MatchmakingResponse execute(Long leadId) {
        Lead lead = leadRepository.findByIdOptional(leadId)
                .orElseThrow(() -> new NotFoundException("Lead not found"));

        if (lead.status == LeadStatus.ASSIGNED) {
            return alreadyAssignedResponse(lead);
        }

        List<SellerCandidate> candidates = rankEligibleSellers(lead);
        List<SellerRankingResponse> ranking = MatchmakingMapper.toRankingResponse(candidates);

        if (ranking.isEmpty()) {
            return noEligibleSellerResponse(lead, NO_ELIGIBLE_SELLER_ASSIGNMENT_MESSAGE);
        }

        Seller selectedSeller = candidates.getFirst().seller();
        Assignment assignment = assignmentService.assignLead(lead, selectedSeller);
        return assignedResponse(lead, assignment, ranking);
    }

    public MatchmakingResponse ranking(Long leadId) {
        Lead lead = leadRepository.findByIdOptional(leadId)
                .orElseThrow(() -> new NotFoundException("Lead not found"));
        return buildResponse(lead);
    }

    @Transactional
    public AcceptAssignmentResponse accept(Long leadId, Long sellerId) {
        Assignment assignment = assignmentService.accept(leadId, sellerId);
        return new AcceptAssignmentResponse(true, assignment.status);
    }

    @Transactional
    public RejectAssignmentResponse reject(Long leadId, Long sellerId, String reason) {
        Assignment rejectedAssignment = assignmentService.reject(leadId, sellerId, reason);
        Lead lead = rejectedAssignment.lead;
        List<Long> rejectedSellerIds = assignmentService.findRejectedSellerIds(lead.id);
        List<SellerCandidate> nextCandidates = rankEligibleSellers(lead, rejectedSellerIds);

        if (nextCandidates.isEmpty()) {
            lead.seller = null;
            lead.status = LeadStatus.NEW;
            lead.updatedAt = rejectedAssignment.updatedAt;
            return new RejectAssignmentResponse(
                    true,
                    rejectedAssignment.status,
                    null,
                    null,
                    REJECTED_WITHOUT_NEXT_SELLER_MESSAGE
            );
        }

        Assignment nextAssignment = assignmentService.assignLead(lead, nextCandidates.getFirst().seller());
        return new RejectAssignmentResponse(
                true,
                rejectedAssignment.status,
                nextAssignment.id,
                nextAssignment.seller.id,
                REJECTED_AND_REPROCESSED_MESSAGE
        );
    }

    private MatchmakingResponse buildResponse(Lead lead) {
        List<SellerCandidate> candidates = rankEligibleSellers(lead);
        List<SellerRankingResponse> ranking = MatchmakingMapper.toRankingResponse(candidates);

        if (ranking.isEmpty()) {
            return noEligibleSellerResponse(lead, NO_ELIGIBLE_SELLER_MESSAGE);
        }

        SellerRankingResponse selected = ranking.getFirst();
        return new MatchmakingResponse(
                lead.id,
                0,
                selected.sellerId(),
                selected.sellerName(),
                null,
                null,
                lead.status,
                ranking,
                ELIGIBLE_SELLER_FOUND_MESSAGE
        );
    }

    private MatchmakingResponse assignedResponse(Lead lead, Assignment assignment, List<SellerRankingResponse> ranking) {
        SellerRankingResponse selected = ranking.getFirst();
        return new MatchmakingResponse(
                lead.id,
                0,
                selected.sellerId(),
                selected.sellerName(),
                assignment.id,
                assignment.status,
                lead.status,
                ranking,
                ELIGIBLE_SELLER_FOUND_MESSAGE
        );
    }

    private MatchmakingResponse alreadyAssignedResponse(Lead lead) {
        Assignment assignment = assignmentService.findActiveByLeadId(lead.id).orElse(null);
        return new MatchmakingResponse(
                lead.id,
                0,
                lead.seller == null ? null : lead.seller.id,
                lead.seller == null ? null : lead.seller.name,
                assignment == null ? null : assignment.id,
                assignment == null ? null : assignment.status,
                lead.status,
                List.of(),
                LEAD_ALREADY_ASSIGNED_MESSAGE
        );
    }

    private MatchmakingResponse noEligibleSellerResponse(Lead lead, String message) {
        return new MatchmakingResponse(
                lead.id,
                0,
                null,
                null,
                null,
                null,
                lead.status,
                List.of(),
                message
        );
    }

    List<SellerCandidate> rankEligibleSellers(Lead lead) {
        return rankEligibleSellers(lead, List.of());
    }

    List<SellerCandidate> rankEligibleSellers(Lead lead, List<Long> excludedSellerIds) {
        LocalDate today = LocalDate.now();
        return sellerRepository.findActiveSellers().stream()
                .filter(seller -> !excludedSellerIds.contains(seller.id))
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
