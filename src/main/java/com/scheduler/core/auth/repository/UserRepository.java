package com.scheduler.core.auth.repository;

import com.scheduler.core.auth.dto.UserDTO;
import com.scheduler.core.auth.model.User;
import com.scheduler.core.exceptions.exception.BadRequestException;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public boolean doesUserExists(String username, String email) {

        return findUserByEmailOrUsername(username, email)
                .isPresent();
    }

    public UserDTO findUserLogin(String usernameOrEmail) {
        final var optionalUser = findUserByEmailOrUsername(usernameOrEmail, usernameOrEmail);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        }
        // Hides the emails signed in the app
        throw new BadRequestException("user.login.password.incorrect");
    }

    public void confirmEmail(String email) {

        update("set emailConfirmed = true where email = ?1", email);
    }

    public List<User> getInactiveUsersWithoutEmailConfirmation() {

        return find("emailConfirmed = false").list();
    }

    public UserDTO findUserDTOById(Long id) {
        return find("id", id)
                .project(UserDTO.class)
                .firstResult();
    }

    private Optional<UserDTO> findUserByEmailOrUsername(String username, String email) {
        return find(
                "name = :username OR email = :email",
                Parameters.with("username", username)
                        .and("email", email)
        ).project(UserDTO.class).firstResultOptional();
    }
}
