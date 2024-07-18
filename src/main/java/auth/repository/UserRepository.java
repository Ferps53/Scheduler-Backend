package auth.repository;

import auth.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public boolean doesUserExists(String username, String email) {

        final Optional<User> optionalUser = find(
                "name = :username OR email = :email",
                Parameters.with("username", username)
                        .and("email", email)
        ).firstResultOptional();

        return optionalUser.isPresent();
    }
}
