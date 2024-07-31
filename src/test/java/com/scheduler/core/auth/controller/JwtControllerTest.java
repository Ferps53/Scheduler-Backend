package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.dto.TokenDTO;
import com.scheduler.core.auth.dto.UserDTO;
import com.scheduler.core.auth.repository.UserRepository;
import com.scheduler.core.exceptions.exception.BadRequestException;
import com.scheduler.core.exceptions.exception.UnauthorizedException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.smallrye.jwt.auth.principal.DefaultJWTCallerPrincipal;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class JwtControllerTest implements QuarkusTestProfile {

    private static final UserDTO testUser = new UserDTO(1L, "test", "test@gmail.com", "", true);
    private static final UserDTO testUserNotInDb = new UserDTO(3L, "test", "test@gmail.com", "", true);
    private static JsonWebToken jwtRefresh;
    private static JsonWebToken jwtRefreshInvalidIssuer;
    private static JsonWebToken jwtRefreshInvalidExpiration;
    private static JsonWebToken jwtRefreshNullIsRefreshClaim;
    private static JsonWebToken jwtRefreshInvalidIsRefreshClaim;
    private static AutoCloseable mockAutoCloseable;

    @Mock(name = "jwtParser")
    JWTParser mockJwtParser;

    @Mock
    UserRepository userRepository;

    @Spy
    @InjectMocks
    JwtController jwtController;

    @BeforeAll
    static void start() {
        try {
            jwtRefresh = new DefaultJWTCallerPrincipal(JwtClaims.parse("{\"iss\":\"test\",\"iat\":1722444123,\"sub\":\"1\",\"exp\":11722447723,\"is_refresh\":\"true\",\"jti\":\"76c08991-6978-4c89-9530-ec8438bb7628\",\"raw_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0ZXN0IiwiaWF0IjoxNzIyNDQ0MTIzLCJzdWIiOiIxIiwiZXhwIjoxNzIyNDQ3NzIzLCJpc19yZWZyZXNoIjoiZmFsc2UiLCJqdGkiOiI3NmMwODk5MS02OTc4LTRjODktOTUzMC1lYzg0MzhiYjc2MjgifQ.BdxlWks47r95VRdgf0nm_swmNprv03bfpVDG9iwsLlqD4eymvldfg0PoWd_nEf-6ficfxYgzseBEHMy9A2YJWWUYbncX4DpLX5aSkfcVoSko2BuW_v2toAiMdX_FQmwTSW6FyqhGuT83qAJ34Il5eeVdmPUS_Ak5JhA_zcqOWcUh9kLKK0rlCMCyELZFYqCYfbIJABAsnGS4GSke-l6UgXjq4g3UqwtUv7uDWhlRejW3Me3t08vgsKRPUuK8H7xxaQd435dVnwyqrzyWl6U3peJXpfhgwgbi9fWSx-PvwPWVlfV283aW7s6e0meZLnj5M_2pVCM-hLwYL27jcLLmgw\"}"));
            jwtRefreshInvalidIssuer = new DefaultJWTCallerPrincipal(JwtClaims.parse("{\"iss\":\"invalid\",\"iat\":1722444123,\"sub\":\"1\",\"exp\":1722447723,\"is_refresh\":\"true\",\"jti\":\"76c08991-6978-4c89-9530-ec8438bb7628\",\"raw_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0ZXN0IiwiaWF0IjoxNzIyNDQ0MTIzLCJzdWIiOiIxIiwiZXhwIjoxNzIyNDQ3NzIzLCJpc19yZWZyZXNoIjoiZmFsc2UiLCJqdGkiOiI3NmMwODk5MS02OTc4LTRjODktOTUzMC1lYzg0MzhiYjc2MjgifQ.BdxlWks47r95VRdgf0nm_swmNprv03bfpVDG9iwsLlqD4eymvldfg0PoWd_nEf-6ficfxYgzseBEHMy9A2YJWWUYbncX4DpLX5aSkfcVoSko2BuW_v2toAiMdX_FQmwTSW6FyqhGuT83qAJ34Il5eeVdmPUS_Ak5JhA_zcqOWcUh9kLKK0rlCMCyELZFYqCYfbIJABAsnGS4GSke-l6UgXjq4g3UqwtUv7uDWhlRejW3Me3t08vgsKRPUuK8H7xxaQd435dVnwyqrzyWl6U3peJXpfhgwgbi9fWSx-PvwPWVlfV283aW7s6e0meZLnj5M_2pVCM-hLwYL27jcLLmgw\"}"));
            jwtRefreshInvalidExpiration = new DefaultJWTCallerPrincipal(JwtClaims.parse("{\"iss\":\"test\",\"iat\":1722444123,\"sub\":\"1\",\"exp\":1722440023,\"is_refresh\":\"true\",\"jti\":\"76c08991-6978-4c89-9530-ec8438bb7628\",\"raw_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0ZXN0IiwiaWF0IjoxNzIyNDQ0MTIzLCJzdWIiOiIxIiwiZXhwIjoxNzIyNDQ3NzIzLCJpc19yZWZyZXNoIjoiZmFsc2UiLCJqdGkiOiI3NmMwODk5MS02OTc4LTRjODktOTUzMC1lYzg0MzhiYjc2MjgifQ.BdxlWks47r95VRdgf0nm_swmNprv03bfpVDG9iwsLlqD4eymvldfg0PoWd_nEf-6ficfxYgzseBEHMy9A2YJWWUYbncX4DpLX5aSkfcVoSko2BuW_v2toAiMdX_FQmwTSW6FyqhGuT83qAJ34Il5eeVdmPUS_Ak5JhA_zcqOWcUh9kLKK0rlCMCyELZFYqCYfbIJABAsnGS4GSke-l6UgXjq4g3UqwtUv7uDWhlRejW3Me3t08vgsKRPUuK8H7xxaQd435dVnwyqrzyWl6U3peJXpfhgwgbi9fWSx-PvwPWVlfV283aW7s6e0meZLnj5M_2pVCM-hLwYL27jcLLmgw\"}"));
            jwtRefreshNullIsRefreshClaim = new DefaultJWTCallerPrincipal(JwtClaims.parse("{\"iss\":\"test\",\"iat\":1722444123,\"sub\":\"1\",\"exp\":11722447723,\"jti\":\"76c08991-6978-4c89-9530-ec8438bb7628\",\"raw_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0ZXN0IiwiaWF0IjoxNzIyNDQ0MTIzLCJzdWIiOiIxIiwiZXhwIjoxNzIyNDQ3NzIzLCJpc19yZWZyZXNoIjoiZmFsc2UiLCJqdGkiOiI3NmMwODk5MS02OTc4LTRjODktOTUzMC1lYzg0MzhiYjc2MjgifQ.BdxlWks47r95VRdgf0nm_swmNprv03bfpVDG9iwsLlqD4eymvldfg0PoWd_nEf-6ficfxYgzseBEHMy9A2YJWWUYbncX4DpLX5aSkfcVoSko2BuW_v2toAiMdX_FQmwTSW6FyqhGuT83qAJ34Il5eeVdmPUS_Ak5JhA_zcqOWcUh9kLKK0rlCMCyELZFYqCYfbIJABAsnGS4GSke-l6UgXjq4g3UqwtUv7uDWhlRejW3Me3t08vgsKRPUuK8H7xxaQd435dVnwyqrzyWl6U3peJXpfhgwgbi9fWSx-PvwPWVlfV283aW7s6e0meZLnj5M_2pVCM-hLwYL27jcLLmgw\"}"));
            jwtRefreshInvalidIsRefreshClaim = new DefaultJWTCallerPrincipal(JwtClaims.parse("{\"iss\":\"test\",\"iat\":1722444123,\"sub\":\"1\",\"exp\":11722447723,\"is_refresh\":\"false\",\"jti\":\"76c08991-6978-4c89-9530-ec8438bb7628\",\"raw_token\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJ0ZXN0IiwiaWF0IjoxNzIyNDQ0MTIzLCJzdWIiOiIxIiwiZXhwIjoxNzIyNDQ3NzIzLCJpc19yZWZyZXNoIjoiZmFsc2UiLCJqdGkiOiI3NmMwODk5MS02OTc4LTRjODktOTUzMC1lYzg0MzhiYjc2MjgifQ.BdxlWks47r95VRdgf0nm_swmNprv03bfpVDG9iwsLlqD4eymvldfg0PoWd_nEf-6ficfxYgzseBEHMy9A2YJWWUYbncX4DpLX5aSkfcVoSko2BuW_v2toAiMdX_FQmwTSW6FyqhGuT83qAJ34Il5eeVdmPUS_Ak5JhA_zcqOWcUh9kLKK0rlCMCyELZFYqCYfbIJABAsnGS4GSke-l6UgXjq4g3UqwtUv7uDWhlRejW3Me3t08vgsKRPUuK8H7xxaQd435dVnwyqrzyWl6U3peJXpfhgwgbi9fWSx-PvwPWVlfV283aW7s6e0meZLnj5M_2pVCM-hLwYL27jcLLmgw\"}"));
        } catch (InvalidJwtException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void clearMocks() throws Exception {
        mockAutoCloseable.close();
    }

    @BeforeEach
    void init() {
        mockAutoCloseable = MockitoAnnotations.openMocks(this);
        jwtController.issuer = "test";
    }

    @Test
    void generateTokenNotNull() {

        final TokenDTO tokenDTO = jwtController.generateToken(testUser);
        assertNotNull(tokenDTO);
    }

    @Test
    void verifyInvalidIssuerRefreshToken() {

        try {
            when(mockJwtParser.parse(anyString())).thenReturn(jwtRefreshInvalidIssuer);
            assertThrows(UnauthorizedException.class, () -> jwtController.refreshToken(""));
        } catch (ParseException e) {
            fail("Failed to parse accessToken");
        }
    }

    @Test
    void verifyRefreshToken() throws ParseException {

        when(userRepository.findUserDTOById(anyLong())).thenReturn(testUserNotInDb);
        when(mockJwtParser.parse(anyString())).thenReturn(jwtRefresh);

        final var tokenDTO = jwtController.generateToken(testUserNotInDb);
        final var refreshedToken = jwtController.refreshToken(tokenDTO.refreshToken());
        assertNotNull(refreshedToken);
    }

    @Test
    void verifyRefreshTokenInvalidUser() throws ParseException {

        when(userRepository.findUserDTOById(anyLong())).thenReturn(null);
        when(mockJwtParser.parse(anyString())).thenReturn(jwtRefresh);
        assertThrows(UnauthorizedException.class, () -> jwtController.refreshToken(""));
    }

    @Test
    void verifyRefreshTokenInvalidExpiration() {

        try {
            when(mockJwtParser.parse(anyString())).thenReturn(jwtRefreshInvalidExpiration);
            assertThrows(UnauthorizedException.class, () -> jwtController.refreshToken(""));
        } catch (ParseException e) {
            fail("Failed to parse token");
        }
    }

    @Test
    void verifyRefreshTokenInvalidIsRefreshClaim() {

        try {
            when(mockJwtParser.parse(anyString())).thenReturn(jwtRefreshInvalidIsRefreshClaim);
            assertThrows(UnauthorizedException.class, () -> jwtController.refreshToken(""));
        } catch (ParseException e) {
            fail("Failed to parse token");
        }
    }

    @Test
    void verifyRefreshTokenNullIsRefreshClaim() {

        try {
            when(mockJwtParser.parse(anyString())).thenReturn(jwtRefreshNullIsRefreshClaim);
            assertThrows(UnauthorizedException.class, () -> jwtController.refreshToken(""));
        } catch (ParseException e) {
            fail("Failed to parse token");
        }
    }

    @Test
    void verifyInvalidTokenRefreshToken() {

        try {
            when(mockJwtParser.parse(anyString())).thenThrow(ParseException.class);
            assertThrows(BadRequestException.class, () -> jwtController.refreshToken(""));
        } catch (ParseException e) {
            fail("Failed to parse token");
        }
    }
}
