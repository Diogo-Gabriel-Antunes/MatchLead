package com.company.leaddistribution.assignment.entity;

import com.company.leaddistribution.lead.entity.Lead;
import com.company.leaddistribution.seller.entity.Seller;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    public Lead lead;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    public Seller seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public AssignmentStatus status;

    @Column(name = "assigned_at", nullable = false)
    public LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    public LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    public LocalDateTime rejectedAt;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;
}
