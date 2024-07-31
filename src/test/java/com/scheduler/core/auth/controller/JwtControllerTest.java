package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.dto.TokenDTO;
import com.scheduler.core.auth.dto.UserDTO;
import com.scheduler.core.exceptions.exception.BadRequestException;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class JwtControllerTest {

    private static final UserDTO testUser = new UserDTO(1L, "test", "test@gmail.com", "", true);

    @Inject
    JwtController jwtController;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @Inject
    JWTParser jwtParser;

    @Test
    void generateTokenNotNull() {
        final TokenDTO tokenDTO = jwtController.generateToken(testUser);

        assertNotNull(tokenDTO);
    }

    @Test
    void verifyIssuer() {
        final TokenDTO tokenDTO = jwtController.generateToken(testUser);

        try {
            JsonWebToken jwt = jwtParser.parse(tokenDTO.accessToken());

            assertEquals(issuer, jwt.getIssuer());
        } catch (ParseException e) {
            fail("Failed to parse accessToken");
        }
    }

    @Test
    void verifyRefreshToken() {
        final TokenDTO tokenDTO = jwtController.generateToken(testUser);
        final var refreshedToken = jwtController.refreshToken(tokenDTO.refreshToken());

        assertNotNull(refreshedToken);
    }

    @Test
    void verifyInvalidTokenRefreshToken() {
        assertThrows(BadRequestException.class, () -> jwtController.refreshToken("asdf"));
    }
}
