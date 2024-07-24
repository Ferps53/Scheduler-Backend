package com.scheduler.core.auth.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class ConfirmationCode extends PanacheEntity {

    @Column(unique = true)
    public String code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user.id", nullable = false)
    public User user;

    @Column(unique = true, nullable = false, name = "expiry_date")
    public LocalDateTime expiryDate;

    public ConfirmationCode(String code, User user) {
        this.code = code;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusSeconds(1);
    }

    public ConfirmationCode() {
    }
}
