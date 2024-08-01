package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.dto.NewUserCreatedDTO;
import com.scheduler.core.auth.dto.TokenDTO;
import com.scheduler.core.auth.dto.UserDTO;
import com.scheduler.core.auth.model.User;
import com.scheduler.core.auth.repository.UserRepository;
import com.scheduler.core.exceptions.exception.BadRequestException;
import com.scheduler.core.exceptions.exception.UnauthorizedException;
import com.scheduler.core.mailer.controller.EmailController;
import com.scheduler.core.mailer.dto.EmailDTO;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@QuarkusTest
class AuthControllerTest {

    private static final UserDTO TEST_USER = new UserDTO(null, "test", "test@gmail.com", BcryptUtil.bcryptHash("12345678"), false);
    private static final UserDTO TEST_USER_INCORRECT_PASSWORD = new UserDTO(null, "test", "test@gmail.com", BcryptUtil.bcryptHash("wrongPassword"), false);
    private static final NewUserCreatedDTO expectedUser = new NewUserCreatedDTO(null, "test", "test@gmail.com");
    @Inject
    AuthController authController;

    @InjectMock
    UserRepository userRepository;

    @InjectMock
    JwtController jwtController;

    @InjectMock
    EmailController emailController;

    @InjectMock
    ConfirmationCodeController confirmationCodeController;

    @ConfigProperty(name = "basic.username")
    String basicUsername;

    @ConfigProperty(name = "basic.password")
    String basicPassword;

    @Test
    void createUserOk() {

        when(userRepository.doesUserExists(anyString(), anyString())).thenReturn(false);
        doNothing().when(userRepository).persist(any(User.class));
        doNothing().when(emailController).sendEmail(any(EmailDTO.class));
        final NewUserCreatedDTO newUser = authController.createNewUser(generateBasic(), TEST_USER.name(), TEST_USER.email(), "12345678");

        assertEquals(expectedUser, newUser);
    }

    @Test
    void createUserFailBasic() {

        when(userRepository.doesUserExists(anyString(), anyString())).thenReturn(false);
        doNothing().when(userRepository).persist(any(User.class));
        doNothing().when(emailController).sendEmail(any(EmailDTO.class));

        assertThrows(UnauthorizedException.class, () -> authController.createNewUser("wrongBasic", TEST_USER.name(), TEST_USER.email(), "12345678"));
    }
    @Test
    void createUserFailNullParameters() {

        assertThrows(BadRequestException.class, () -> authController.createNewUser(generateBasic(), null, null, null));
    }
    @Test
    void createUserFailEmptyParameters() {

        assertThrows(BadRequestException.class, () -> authController.createNewUser(generateBasic(), "", "", ""));
    }

    @Test
    void createUserAlreadyExists() {

        when(userRepository.doesUserExists(anyString(), anyString())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> authController.createNewUser(generateBasic(), TEST_USER.name(), TEST_USER.email(), "12345678"));
    }

    @Test
    void loginOk() {

        when(userRepository.findUserLogin(anyString())).thenReturn(TEST_USER);
        when(jwtController.generateToken(any())).thenReturn(new TokenDTO("", ""));
        assertNotNull(authController.login(generateBasic(), TEST_USER.email(), "12345678"));
    }

    @Test
    void loginInvalidPassword() {
        when(userRepository.findUserLogin(anyString())).thenReturn(TEST_USER_INCORRECT_PASSWORD);
        assertThrows(UnauthorizedException.class, () -> authController.login(generateBasic(), TEST_USER.email(), "12345678"));
    }

    String generateBasic() {
        return "BASIC " + Base64.getEncoder().encodeToString((basicUsername + ":" + basicPassword).getBytes());
    }
}
