package com.company.leaddistribution.seller.mapper;

import com.company.leaddistribution.seller.dto.SellerListItemResponse;
import com.company.leaddistribution.seller.dto.SellerRequest;
import com.company.leaddistribution.seller.dto.SellerResponse;
import com.company.leaddistribution.seller.entity.Seller;

import java.time.LocalDateTime;
import java.util.Locale;

public final class SellerMapper {

    private SellerMapper() {
    }

    public static Seller toEntity(SellerRequest request) {
        Seller seller = new Seller();
        seller.name = request.name().trim();
        seller.email = normalizeEmail(request.email());
        seller.region = request.region().trim();
        seller.specialization = request.specialization().trim();
        seller.dailyCapacity = request.dailyCapacity();
        seller.active = request.active();
        LocalDateTime now = LocalDateTime.now();
        seller.createdAt = now;
        seller.updatedAt = now;
        return seller;
    }

    public static void updateEntity(Seller seller, SellerRequest request) {
        seller.name = request.name().trim();
        seller.email = normalizeEmail(request.email());
        seller.region = request.region().trim();
        seller.specialization = request.specialization().trim();
        seller.dailyCapacity = request.dailyCapacity();
        seller.active = request.active();
        seller.updatedAt = LocalDateTime.now();
    }

    public static SellerResponse toResponse(Seller seller) {
        return new SellerResponse(
                seller.id,
                seller.name,
                seller.email,
                seller.region,
                seller.specialization,
                seller.dailyCapacity,
                seller.active
        );
    }

    public static SellerListItemResponse toListItemResponse(Seller seller) {
        return new SellerListItemResponse(
                seller.id,
                seller.name,
                seller.region,
                seller.active
        );
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
