package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.dto.NewUserCreatedDTO;
import com.scheduler.core.auth.model.User;
import com.scheduler.core.auth.repository.UserRepository;
import com.scheduler.core.mailer.controller.EmailController;
import com.scheduler.core.mailer.dto.EmailDTO;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@QuarkusTest
class AuthControllerTest {

    private static final User TEST_USER = new User("test", "test@gmail.com", BcryptUtil.bcryptHash("12345678"));
    private static final User TEST_USER_INCORRECT_PASSWORD = new User("test", "test@gmail.com", BcryptUtil.bcryptHash("wrongPassword"));
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

        Mockito.when(userRepository.doesUserExists(anyString(), anyString())).thenReturn(false);
        Mockito.doNothing().when(userRepository).persist(any(User.class));
        Mockito.doNothing().when(emailController).sendEmail(any(EmailDTO.class));
        final NewUserCreatedDTO newUser = authController.createNewUser(generateBasic(), "test", "test@gmail.com", "12345678");

        assertEquals(expectedUser, newUser);
    }

    String generateBasic() {
        return "BASIC " + Base64.getEncoder().encodeToString((basicUsername + ":" + basicPassword).getBytes());
    }
}
