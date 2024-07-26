package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.dto.NewUserCreatedDTO;
import com.scheduler.core.auth.dto.TokenDTO;
import com.scheduler.core.auth.dto.UserDTO;
import com.scheduler.core.auth.mapper.UserMapper;
import com.scheduler.core.auth.model.ConfirmationCode;
import com.scheduler.core.auth.model.User;
import com.scheduler.core.auth.repository.UserRepository;
import com.scheduler.core.exceptions.exception.BadRequestException;
import com.scheduler.core.exceptions.exception.UnauthorizedException;
import com.scheduler.core.mailer.controller.EmailController;
import com.scheduler.core.mailer.dto.EmailContentsDTO;
import com.scheduler.core.mailer.dto.EmailDTO;
import com.scheduler.core.mailer.enums.EmailImages;
import com.scheduler.core.mailer.enums.EmailModels;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Transactional(dontRollbackOn = BadRequestException.class)
@ApplicationScoped
public class AuthController {

    @ConfigProperty(name = "basic.username")
    String basicUsername;

    @ConfigProperty(name = "basic.password")
    String basicPassword;

    @Inject
    UserRepository userRepository;

    @Inject
    UserMapper userMapper;

    @Inject
    JwtController jwtController;

    @Inject
    EmailController emailController;

    @Inject
    ConfirmationCodeController confirmationCodeController;

    public NewUserCreatedDTO createNewUser(
            String basic,
            String username,
            String email,
            String password
    ) {

        validateBasic(basic);

        if (username == null || username.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new BadRequestException("user.sign.in.notNull");
        }

        username = decode(username);
        password = BcryptUtil.bcryptHash(decode(password));
        email = decode(email);

        if (userRepository.doesUserExists(username, email)) {
            throw new BadRequestException("user.exists");
        }

        final User user = new User(username, email, password);
        user.persist();

        sendConfirmationEmail(user);
        return userMapper.toUserCreatedDTO(user);
    }

    public TokenDTO login(String basic, String usernameOrEmail, String password) {

        validateBasic(basic);

        if (usernameOrEmail == null || usernameOrEmail.isEmpty() ||
                password == null || password.isEmpty()) {
            throw new BadRequestException("user.login.notNull");
        }

        usernameOrEmail = decode(usernameOrEmail);
        password = decode(password);

        final UserDTO user = userRepository.findUserLogin(usernameOrEmail);

        if (!BcryptUtil.matches(password, user.password()))
            throw new UnauthorizedException("user.login.password.incorrect");

        return jwtController.generateToken(user);
    }

    public TokenDTO refreshToken(String basic, String refreshToken) {

        validateBasic(basic);

        return jwtController.refreshToken(refreshToken);
    }

    public void confirmEmail(String basic, String confirmationCode, String email) {

        validateBasic(basic);

        try {

            confirmationCodeController.validateCode(confirmationCode, email);
            userRepository.confirmEmail(email);

        } catch (BadRequestException e) {

            final ConfirmationCode oldCode = ConfirmationCode.find("user.email = ?1", email)
                    .firstResult();
            sendConfirmationEmail(oldCode.user);
            oldCode.delete();

            throw new BadRequestException("user.new.code.sent");
        }
    }

    public void resendConfirmationEmail (String basic, String email) {

        validateBasic(basic);
        User user = userMapper.toUser(userRepository.findUserLogin(email));
        sendConfirmationEmail(user);

    }

    private void validateBasic(String basic) {

        if (basic == null || basic.isBlank() || !basic.toUpperCase().startsWith("BASIC "))
            throw new UnauthorizedException();

        final String expectedBase64 = Base64.getEncoder().encodeToString((basicUsername + ":" + basicPassword).getBytes());

        if (!basic.substring(6).equals(expectedBase64))
            throw new UnauthorizedException();
    }

    private String decode(String param) {
        return URLDecoder.decode(param, StandardCharsets.UTF_8);
    }

    private void sendConfirmationEmail(User user) {

        final String code = confirmationCodeController.createCode(6, user);

        final var contents = List.of(
                new EmailContentsDTO("-username-", user.name, false),
                new EmailContentsDTO("-code-", code, false)
        );

        final var images = List.of(EmailImages.LOGO);
        emailController.sendEmail(new EmailDTO(user.email, EmailModels.EMAIL_CONFIRMATION, contents, images));
    }
}
