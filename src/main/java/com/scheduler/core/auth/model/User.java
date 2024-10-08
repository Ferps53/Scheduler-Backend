package com.scheduler.core.auth.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.type.TrueFalseConverter;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {

    @Column(name = "created_at", nullable = false)
    public final Instant createdAt = Instant.now();

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(name = "email", nullable = false, unique = true)
    public String email;

    @Column(name = "password", nullable = false)
    public String password;

    @Convert(converter = TrueFalseConverter.class)
    @Column(name = "email_confirmed", length = 1, nullable = false)
    public boolean emailConfirmed;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.emailConfirmed = false;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", emailConfirmed=" + emailConfirmed +
                ", createdAt=" + createdAt +
                '}';
    }
}
