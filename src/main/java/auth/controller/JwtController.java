package auth.controller;

import auth.dto.TokenDTO;
import auth.dto.UserDTO;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class JwtController {

    public TokenDTO generateToken(UserDTO user) {

        return new TokenDTO(
                generateToken(user, false),
                generateToken(user, true)
        );
    }

    private String generateToken(UserDTO user, boolean isRefreshToken) {
        final Instant issuedAt = Instant.now();
        return Jwt.issuer("Scheduler")
                .issuedAt(issuedAt)
                .subject(user.id().toString())
                .expiresAt(isRefreshToken
                        ? issuedAt.plus(4, ChronoUnit.HOURS)
                        : issuedAt.plus(1, ChronoUnit.HOURS)
                ).sign();
    }
}
