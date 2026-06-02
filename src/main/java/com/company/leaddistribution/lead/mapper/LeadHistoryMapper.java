package com.company.leaddistribution.lead.mapper;

import com.company.leaddistribution.lead.dto.LeadHistoryEventResponse;
import com.company.leaddistribution.lead.dto.LeadHistoryResponse;
import com.company.leaddistribution.lead.entity.LeadHistory;

import java.util.List;

public final class LeadHistoryMapper {

    private LeadHistoryMapper() {
    }

    public static LeadHistoryResponse toResponse(Long leadId, List<LeadHistory> events) {
        return new LeadHistoryResponse(
                leadId,
                events.stream().map(LeadHistoryMapper::toEventResponse).toList()
        );
    }

    public static LeadHistoryEventResponse toEventResponse(LeadHistory history) {
        return new LeadHistoryEventResponse(
                history.type,
                history.previousValue,
                history.newValue,
                history.createdAt
        );
    }
}
