package com.company.leaddistribution.notification.entity;

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
@Table(name = "notifications")
public class Notification extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lead_id", nullable = false)
    public Lead lead;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    public Seller seller;

    @Column(nullable = false, length = 180)
    public String recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    public NotificationEvent event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public NotificationStatus status;

    @Column(nullable = false, length = 1000)
    public String payload;

    @Column(name = "error_message", length = 500)
    public String errorMessage;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "sent_at")
    public LocalDateTime sentAt;
}
