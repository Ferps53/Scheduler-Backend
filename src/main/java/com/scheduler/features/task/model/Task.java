package com.scheduler.features.task.model;

import com.scheduler.core.auth.model.User;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Map;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_user", nullable = false)
    public User user;

    public static Task fromJson(Map<String, Object> jsonMap) {

        final Task task = new Task();
        task.id = Long.parseLong(String.valueOf(jsonMap.get("id")));
        task.title = (String) jsonMap.get("title");
        task.description = (String) jsonMap.get("description");
        task.isConcluded = jsonMap.get("isConcluded").equals("true");
        task.isInTrashBin = jsonMap.get("isInTrashBin").equals("true");
        task.createdAt = LocalDateTime.parse((String) jsonMap.get("createdAt"));
        task.expiresIn = LocalDateTime.parse((String) jsonMap.get("expiresIn"));
        task.user = new User();
        task.user.id = Long.parseLong(String.valueOf(jsonMap.get("userId")));

        return task;
    }

    @Override
    public String toString() {
        return "Task{" +
                "user=" + user +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isConcluded=" + isConcluded +
                ", isInTrashBin=" + isInTrashBin +
                ", sentToTrashBinAt=" + sentToTrashBinAt +
                ", concludedAt=" + concludedAt +
                ", createdAt=" + createdAt +
                ", expiresIn=" + expiresIn +
                ", id=" + id +
                '}';
    }
}
