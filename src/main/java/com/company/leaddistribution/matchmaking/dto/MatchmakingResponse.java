package com.company.leaddistribution.matchmaking.dto;

import com.company.leaddistribution.assignment.entity.AssignmentStatus;
import com.company.leaddistribution.lead.entity.LeadStatus;

import java.util.List;

public record MatchmakingResponse(
        Long leadId,
        int score,
        Long selectedSellerId,
        String selectedSellerName,
        Long assignmentId,
        AssignmentStatus assignmentStatus,
        LeadStatus leadStatus,
        List<SellerRankingResponse> ranking,
        String message
) {
}
