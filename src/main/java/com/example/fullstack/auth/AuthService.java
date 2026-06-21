package com.example.fullstack.auth;

import com.example.fullstack.user.User;
import com.example.fullstack.user.UserService;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.security.AuthenticationFailedException;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.naming.AuthenticationException;
import java.time.Duration;
import java.util.HashSet;

@ApplicationScoped
public class AuthService {

    private final String issuer;
    private final UserService userService;

    @Inject
    public AuthService(
        @ConfigProperty(name = "mp.jwt.verify.issuer") String issuer,
        UserService userService
    ) {
        this.userService = userService;
        this.issuer = issuer;
    }

    @WithSession
    public Uni<String> authenticate(AuthRequest request) {
        return userService.findByName(request.name())
            .onItem()
            .transform(user -> {
                if (user == null || !UserService.matches(user, request.password())) {
                    throw new AuthenticationFailedException("Invalid credentials");
                }

                return Jwt.issuer(issuer)
                    .upn(user.name)
                    .groups(new HashSet<>(user.roles))
                    .expiresIn(Duration.ofHours(1L))
                    .sign();
            });
    }
}
