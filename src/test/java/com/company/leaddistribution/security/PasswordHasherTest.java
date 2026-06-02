package com.company.leaddistribution.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    private final PasswordHasher passwordHasher = new PasswordHasher();

    @Test
    void shouldMatchGeneratedHash() {
        String hash = passwordHasher.hash("123456");

        assertTrue(passwordHasher.matches("123456", hash));
    }

    @Test
    void shouldRejectInvalidPassword() {
        String hash = passwordHasher.hash("123456");

        assertFalse(passwordHasher.matches("invalid", hash));
    }
}
