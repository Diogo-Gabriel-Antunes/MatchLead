package com.company.leaddistribution.auth.service;

import com.company.leaddistribution.auth.dto.CurrentUserResponse;
import com.company.leaddistribution.auth.dto.LoginRequest;
import com.company.leaddistribution.auth.dto.LoginResponse;
import com.company.leaddistribution.auth.entity.User;
import com.company.leaddistribution.auth.mapper.UserMapper;
import com.company.leaddistribution.auth.repository.UserRepository;
import com.company.leaddistribution.security.PasswordHasher;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JsonWebToken jwt;
    private final String issuer;
    private final long expiresIn;

    public AuthService(
            UserRepository userRepository,
            PasswordHasher passwordHasher,
            JsonWebToken jwt,
            @ConfigProperty(name = "mp.jwt.verify.issuer") String issuer,
            @ConfigProperty(name = "matchlead.jwt.expires-in") long expiresIn
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwt = jwt;
        this.issuer = issuer;
        this.expiresIn = expiresIn;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(found -> found.active)
                .filter(found -> passwordHasher.matches(request.password(), found.passwordHash))
                .orElseThrow(() -> new NotAuthorizedException("Invalid credentials"));

        String accessToken = Jwt.issuer(issuer)
                .subject(user.id.toString())
                .upn(user.email)
                .groups(Set.of(user.role.name()))
                .claim("name", user.name)
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .sign();

        return new LoginResponse(accessToken, expiresIn, user.role);
    }

    public CurrentUserResponse currentUser() {
        Long userId = Long.valueOf(jwt.getSubject());
        User user = userRepository.findByIdOptional(userId)
                .filter(found -> found.active)
                .orElseThrow(() -> new NotAuthorizedException("Invalid token subject"));

        return UserMapper.toCurrentUserResponse(user);
    }
}
