package com.scheduler.features.task.model;

import com.scheduler.core.auth.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
public class Task extends PanacheEntity {

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "description")
    public String description;

    @Column(name = "is_concluded")
    public boolean isConcluded;

    @Column(name = "is_in_trash_bin")
    public boolean isInTrashBin;

    @Column(name = "sent_to_trash_bin_at")
    public LocalDateTime sentToTrashBinAt;

    @Column(name = "concluded_at")
    public LocalDateTime concludedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    public LocalDateTime createdAt;

    @Column(name = "expires_in")
    public LocalDateTime expiresIn;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    public User user;

}
