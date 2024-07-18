package auth.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @NotNull(message = "user.username.notNull")
    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @NotNull(message = "user.email.notNull")
    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @NotNull(message = "user.password.notNull")
    @Column(name = "password", nullable = false)
    public String password;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
