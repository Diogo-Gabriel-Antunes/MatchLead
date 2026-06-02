package com.company.leaddistribution.lead.entity;

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
@Table(name = "lead_history")
public class LeadHistory extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    public Lead lead;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 40)
    public LeadHistoryEventType type;

    @Column(name = "previous_value", length = 1000)
    public String previousValue;

    @Column(name = "new_value", nullable = false, length = 1000)
    public String newValue;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;
}
