package com.company.leaddistribution.seller.service;

import com.company.leaddistribution.seller.dto.SellerPageResponse;
import com.company.leaddistribution.seller.dto.SellerRequest;
import com.company.leaddistribution.seller.dto.SellerResponse;
import com.company.leaddistribution.seller.entity.Seller;
import com.company.leaddistribution.seller.mapper.SellerMapper;
import com.company.leaddistribution.seller.repository.SellerRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public SellerPageResponse list(int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        PanacheQuery<Seller> query = sellerRepository.find("order by id asc");
        List<Seller> sellers = query.page(Page.of(normalizedPage, normalizedSize)).list();

        return new SellerPageResponse(
                sellers.stream().map(SellerMapper::toListItemResponse).toList(),
                normalizedPage,
                normalizedSize,
                query.count(),
                query.pageCount()
        );
    }

    public SellerResponse findById(Long id) {
        return SellerMapper.toResponse(findSeller(id));
    }

    @Transactional
    public SellerResponse create(SellerRequest request) {
        String email = SellerMapper.normalizeEmail(request.email());
        if (sellerRepository.existsByEmail(email)) {
            throw duplicateEmailException();
        }

        Seller seller = SellerMapper.toEntity(request);
        sellerRepository.persist(seller);
        return SellerMapper.toResponse(seller);
    }

    @Transactional
    public SellerResponse update(Long id, SellerRequest request) {
        Seller seller = findSeller(id);
        String email = SellerMapper.normalizeEmail(request.email());
        if (sellerRepository.existsByEmailAndDifferentId(email, id)) {
            throw duplicateEmailException();
        }

        SellerMapper.updateEntity(seller, request);
        return SellerMapper.toResponse(seller);
    }

    @Transactional
    public void deactivate(Long id) {
        Seller seller = findSeller(id);
        seller.active = false;
        seller.updatedAt = LocalDateTime.now();
    }

    private Seller findSeller(Long id) {
        return sellerRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Seller not found"));
    }

    private WebApplicationException duplicateEmailException() {
        return new WebApplicationException("Seller email already exists", Response.Status.CONFLICT);
    }
}
