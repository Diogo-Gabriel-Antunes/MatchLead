package com.company.leaddistribution.seller.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
public class Seller extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 120)
    public String name;

    @Column(nullable = false, unique = true, length = 180)
    public String email;

    @Column(nullable = false, length = 80)
    public String region;

    @Column(nullable = false, length = 120)
    public String specialization;

    @Column(name = "daily_capacity", nullable = false)
    public Integer dailyCapacity;

    @Column(nullable = false)
    public boolean active;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    public LocalDateTime updatedAt;
}
