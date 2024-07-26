package com.scheduler.core.auth.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "confirmation_code")
public class ConfirmationCode extends PanacheEntity {

    @Column(unique = true)
    public String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Column(nullable = false, name = "expiry_date")
    public LocalDateTime expiryDate;

    public ConfirmationCode(String code, User user) {
        this.code = code;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusMinutes(15);
    }

    public ConfirmationCode() {
    }
}
