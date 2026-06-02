package com.company.leaddistribution.lead.mapper;

import com.company.leaddistribution.lead.dto.LeadListItemResponse;
import com.company.leaddistribution.lead.dto.LeadRequest;
import com.company.leaddistribution.lead.dto.LeadResponse;
import com.company.leaddistribution.lead.dto.LeadSellerResponse;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.seller.entity.Seller;

import java.time.LocalDateTime;
import java.util.Locale;

public final class LeadMapper {

    private LeadMapper() {
    }

    public static Lead toEntity(LeadRequest request) {
        Lead lead = new Lead();
        lead.name = request.name().trim();
        lead.email = normalizeEmail(request.email());
        lead.phone = normalizePhone(request.phone());
        lead.source = request.source().trim();
        lead.region = request.region().trim();
        lead.status = LeadStatus.NEW;
        LocalDateTime now = LocalDateTime.now();
        lead.createdAt = now;
        lead.updatedAt = now;
        return lead;
    }

    public static void updateEntity(Lead lead, LeadRequest request) {
        lead.name = request.name().trim();
        lead.email = normalizeEmail(request.email());
        lead.phone = normalizePhone(request.phone());
        lead.source = request.source().trim();
        lead.region = request.region().trim();
        lead.updatedAt = LocalDateTime.now();
    }

    public static LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.id,
                lead.name,
                lead.email,
                lead.phone,
                lead.source,
                lead.region,
                lead.status,
                toSellerResponse(lead.seller),
                lead.createdAt,
                lead.updatedAt
        );
    }

    public static LeadListItemResponse toListItemResponse(Lead lead) {
        Long sellerId = lead.seller == null ? null : lead.seller.id;
        return new LeadListItemResponse(
                lead.id,
                lead.name,
                lead.source,
                lead.region,
                lead.status,
                sellerId,
                lead.createdAt
        );
    }

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        return phone.trim();
    }

    private static LeadSellerResponse toSellerResponse(Seller seller) {
        if (seller == null) {
            return null;
        }
        return new LeadSellerResponse(seller.id, seller.name);
    }
}
