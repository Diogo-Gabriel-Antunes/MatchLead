package com.company.leaddistribution.lead;

import com.company.leaddistribution.lead.dto.LeadHistoryResponse;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadHistory;
import com.company.leaddistribution.lead.entity.LeadHistoryEventType;
import com.company.leaddistribution.lead.mapper.LeadHistoryMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LeadHistoryMapperTest {

    @Test
    void shouldMapHistoryToResponse() {
        Lead lead = new Lead();
        lead.id = 10L;

        LeadHistory history = new LeadHistory();
        history.lead = lead;
        history.type = LeadHistoryEventType.LEAD_CREATED;
        history.previousValue = null;
        history.newValue = "Lead criado";
        history.createdAt = LocalDateTime.now();

        LeadHistoryResponse response = LeadHistoryMapper.toResponse(lead.id, List.of(history));

        assertEquals(10L, response.leadId());
        assertEquals(1, response.events().size());
        assertEquals(LeadHistoryEventType.LEAD_CREATED, response.events().getFirst().type());
        assertNull(response.events().getFirst().previousValue());
        assertEquals("Lead criado", response.events().getFirst().newValue());
        assertEquals(history.createdAt, response.events().getFirst().date());
    }
}
