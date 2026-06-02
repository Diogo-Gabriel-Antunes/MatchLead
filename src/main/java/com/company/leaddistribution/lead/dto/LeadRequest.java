package com.company.leaddistribution.lead.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeadRequest(
        @NotBlank @Size(max = 120) String name,
        @Email @Size(max = 180) String email,
        @Size(max = 30) String phone,
        @NotBlank @Size(max = 80) String source,
        @NotBlank @Size(max = 80) String region
) {

    @AssertTrue(message = "Lead must have email or phone")
    public boolean hasEmailOrPhone() {
        return hasText(email) || hasText(phone);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
