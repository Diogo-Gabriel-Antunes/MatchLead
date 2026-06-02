package com.company.leaddistribution.seller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SellerRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Size(max = 80) String region,
        @NotBlank @Size(max = 120) String specialization,
        @NotNull @Positive Integer dailyCapacity,
        @NotNull Boolean active
) {
}
