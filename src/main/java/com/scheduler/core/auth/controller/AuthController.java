package com.scheduler.core.auth.controller;

import com.scheduler.core.auth.dto.NewUserCreatedDTO;
import com.scheduler.core.auth.dto.TokenDTO;
import com.scheduler.core.auth.dto.UserDTO;
import com.scheduler.core.auth.mapper.UserMapper;
import com.scheduler.core.auth.model.User;
import com.scheduler.core.auth.repository.UserRepository;
import com.scheduler.core.exceptions.exception.BadRequestException;
import com.scheduler.core.exceptions.exception.UnauthorizedException;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    @Transactional
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

        return userMapper.toUserCreatedDTO(user);
    }

    @Transactional
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
}
