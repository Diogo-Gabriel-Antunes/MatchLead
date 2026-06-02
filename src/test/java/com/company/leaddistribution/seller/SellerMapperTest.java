package com.company.leaddistribution.seller;

import com.company.leaddistribution.seller.dto.SellerRequest;
import com.company.leaddistribution.seller.entity.Seller;
import com.company.leaddistribution.seller.mapper.SellerMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SellerMapperTest {

    @Test
    void shouldMapRequestToEntity() {
        SellerRequest request = new SellerRequest(
                " Joao Silva ",
                " JOAO@EMAIL.COM ",
                " SC ",
                " AUTOMOTIVO ",
                50,
                true
        );

        Seller seller = SellerMapper.toEntity(request);

        assertEquals("Joao Silva", seller.name);
        assertEquals("joao@email.com", seller.email);
        assertEquals("SC", seller.region);
        assertEquals("AUTOMOTIVO", seller.specialization);
        assertEquals(50, seller.dailyCapacity);
        assertTrue(seller.active);
        assertNotNull(seller.createdAt);
        assertNotNull(seller.updatedAt);
    }
}
