package auth.controller;

import auth.dto.TokenDTO;
import auth.dto.UserDTO;
import auth.repository.UserRepository;
import exceptions.exception.BadRequestException;
import exceptions.exception.UnauthorizedException;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class JwtController {

    @Inject
    JWTParser jwtParser;

    @Inject
    UserRepository userRepository;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public TokenDTO generateToken(UserDTO user) {

        return new TokenDTO(
                generateToken(user, false),
                generateToken(user, true)
        );
    }

    public TokenDTO refreshToken(String refreshToken) {

        try {
            JsonWebToken jwt = jwtParser.parse(refreshToken);

            if (!jwt.getIssuer().equals(issuer) || jwt.getExpirationTime() < Instant.now().getEpochSecond())
                throw new UnauthorizedException();

            final String isRefresh = jwt.getClaim("is_refresh");
            if (isRefresh == null || isRefresh.equals("false"))
                throw new UnauthorizedException();

            final var user = userRepository.find("id", Long.valueOf(jwt.getSubject()))
                    .project(UserDTO.class)
                    .firstResult();

            if (user == null)
                throw new UnauthorizedException();

            return generateToken(user);

        } catch (ParseException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private String generateToken(UserDTO user, boolean isRefreshToken) {
        final Instant issuedAt = Instant.now();
        return Jwt.issuer("Scheduler")
                .issuedAt(issuedAt)
                .subject(user.id().toString())
                .expiresAt(isRefreshToken
                        ? issuedAt.plus(4, ChronoUnit.HOURS)
                        : issuedAt.plus(1, ChronoUnit.HOURS)
                ).claim("is_refresh", Boolean.toString(isRefreshToken))
                .sign();
    }
}
