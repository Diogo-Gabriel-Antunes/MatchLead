package com.company.leaddistribution.lead.service;

import com.company.leaddistribution.auth.entity.Role;
import com.company.leaddistribution.lead.dto.LeadPageResponse;
import com.company.leaddistribution.lead.dto.LeadRequest;
import com.company.leaddistribution.lead.dto.LeadResponse;
import com.company.leaddistribution.lead.dto.LeadStatusUpdateRequest;
import com.company.leaddistribution.lead.dto.LeadStatusUpdateResponse;
import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.lead.entity.LeadStatus;
import com.company.leaddistribution.lead.mapper.LeadMapper;
import com.company.leaddistribution.lead.repository.LeadRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadHistoryService leadHistoryService;
    private final JsonWebToken jwt;

    public LeadService(LeadRepository leadRepository, LeadHistoryService leadHistoryService, JsonWebToken jwt) {
        this.leadRepository = leadRepository;
        this.leadHistoryService = leadHistoryService;
        this.jwt = jwt;
    }

    public LeadPageResponse list(LeadStatus status, String region, String source, Long sellerId, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), 100);
        QueryParts queryParts = buildListQuery(status, region, source, sellerId);

        PanacheQuery<Lead> query = leadRepository.find(queryParts.query(), queryParts.params());
        List<Lead> leads = query.page(Page.of(normalizedPage, normalizedSize)).list();

        return new LeadPageResponse(
                leads.stream().map(LeadMapper::toListItemResponse).toList(),
                normalizedPage,
                normalizedSize,
                query.count(),
                query.pageCount()
        );
    }

    public LeadResponse findById(Long id) {
        Lead lead = findLead(id);
        validateSellerVisibility(lead);
        return LeadMapper.toResponse(lead);
    }

    @Transactional
    public LeadResponse create(LeadRequest request) {
        validateDuplicates(request, null);
        Lead lead = LeadMapper.toEntity(request);
        leadRepository.persist(lead);
        leadHistoryService.recordCreated(lead);
        return LeadMapper.toResponse(lead);
    }

    @Transactional
    public LeadResponse update(Long id, LeadRequest request) {
        Lead lead = findLead(id);
        validateDuplicates(request, id);
        String previousValue = leadSnapshot(lead);
        LeadMapper.updateEntity(lead, request);
        leadHistoryService.recordUpdated(lead, previousValue, leadSnapshot(lead));
        return LeadMapper.toResponse(lead);
    }

    @Transactional
    public LeadStatusUpdateResponse updateStatus(Long id, LeadStatusUpdateRequest request) {
        Lead lead = findLead(id);
        validateSellerVisibility(lead);
        LeadStatus previousStatus = lead.status;
        lead.status = request.status();
        lead.updatedAt = LocalDateTime.now();
        leadHistoryService.recordStatusChanged(lead, previousStatus.name(), lead.status.name());
        if (previousStatus != LeadStatus.ASSIGNED && lead.status == LeadStatus.ASSIGNED) {
            leadHistoryService.recordAssigned(lead, previousStatus.name(), "Lead atribuído");
        }
        return new LeadStatusUpdateResponse(true, lead.status);
    }

    private QueryParts buildListQuery(LeadStatus status, String region, String source, Long sellerId) {
        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (status != null) {
            query.append(" and status = :status");
            params.put("status", status);
        }
        if (hasText(region)) {
            query.append(" and region = :region");
            params.put("region", region.trim());
        }
        if (hasText(source)) {
            query.append(" and source = :source");
            params.put("source", source.trim());
        }
        if (sellerId != null) {
            query.append(" and seller.id = :sellerId");
            params.put("sellerId", sellerId);
        }
        if (isSeller()) {
            query.append(" and (seller is null or lower(seller.email) = :currentSellerEmail)");
            params.put("currentSellerEmail", jwt.getName().toLowerCase());
        }

        query.append(" order by createdAt desc, id desc");
        return new QueryParts(query.toString(), params);
    }

    private Lead findLead(Long id) {
        return leadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Lead not found"));
    }

    private void validateSellerVisibility(Lead lead) {
        if (!isSeller() || lead.seller == null) {
            return;
        }
        if (!lead.seller.email.equalsIgnoreCase(jwt.getName())) {
            throw new ForbiddenException("Lead belongs to another seller");
        }
    }

    private void validateDuplicates(LeadRequest request, Long currentId) {
        String email = LeadMapper.normalizeEmail(request.email());
        String phone = LeadMapper.normalizePhone(request.phone());

        boolean duplicateEmail = currentId == null
                ? leadRepository.existsByEmail(email)
                : leadRepository.existsByEmailAndDifferentId(email, currentId);
        boolean duplicatePhone = currentId == null
                ? leadRepository.existsByPhone(phone)
                : leadRepository.existsByPhoneAndDifferentId(phone, currentId);

        if (duplicateEmail || duplicatePhone) {
            throw new WebApplicationException("Lead already exists", Response.Status.CONFLICT);
        }
    }

    private boolean isSeller() {
        return jwt.getGroups().contains(Role.SELLER.name());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String leadSnapshot(Lead lead) {
        return "name=%s,email=%s,phone=%s,source=%s,region=%s,status=%s".formatted(
                lead.name,
                lead.email,
                lead.phone,
                lead.source,
                lead.region,
                lead.status
        );
    }

    private record QueryParts(String query, Map<String, Object> params) {
    }
}
