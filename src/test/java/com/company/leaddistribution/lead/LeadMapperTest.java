package com.company.leaddistribution.lead;

import com.company.leaddistribution.lead.dto.LeadRequest;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.mapper.LeadMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LeadMapperTest {

    @Test
    void shouldMapRequestToNewLead() {
        LeadRequest request = new LeadRequest(
                " Maria Souza ",
                " MARIA@EMAIL.COM ",
                " 47999999999 ",
                " FACEBOOK ",
                " SC "
        );

        Lead lead = LeadMapper.toEntity(request);

        assertEquals("Maria Souza", lead.name);
        assertEquals("maria@email.com", lead.email);
        assertEquals("47999999999", lead.phone);
        assertEquals("FACEBOOK", lead.source);
        assertEquals("SC", lead.region);
        assertEquals(LeadStatus.NEW, lead.status);
        assertNotNull(lead.createdAt);
        assertNotNull(lead.updatedAt);
    }

    @Test
    void shouldNormalizeBlankContactFieldsToNull() {
        assertNull(LeadMapper.normalizeEmail(" "));
        assertNull(LeadMapper.normalizePhone(""));
    }
}
