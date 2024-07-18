package auth.repository;

import auth.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.util.List;
import java.util.Optional;

public class UserRepository implements PanacheRepository<User> {

    public boolean doesUserExists(String username, String email) {

        final Optional<User> optionalUser = find("username = 1? OR email 2?", username, email)
                .firstResultOptional();

        return optionalUser.isPresent();
    }
}
