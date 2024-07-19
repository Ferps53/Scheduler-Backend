package com.scheduler.features.task.repository;

import com.scheduler.features.task.dto.TaskDTO;
import com.scheduler.features.task.model.Task;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {

    public List<TaskDTO> listTasksNotInTrashBin(long userId) {
        return find("""
                FROM Task t
                LEFT JOIN t.user u
                WHERE isInTrashBin = false AND
                u.id = :userId
                """,
                Parameters.with("userId", userId))
                .project(TaskDTO.class).list();
    }
}
