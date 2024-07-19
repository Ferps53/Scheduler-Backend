package com.scheduler.features.task.repository;

import com.scheduler.core.exceptions.exception.NotFoundException;
import com.scheduler.features.task.dto.TaskDTO;
import com.scheduler.features.task.model.Task;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped

public class TaskRepository implements PanacheRepository<Task> {

    private static final String BASE_QUERY_FIND_TASK = """
            FROM Task t
            LEFT JOIN t.user u
            WHERE t.id = ?1 and
            u.id = ?2
            """;

    public List<TaskDTO> listTasksNotInTrashBin(long userId) {
        return find("""
                        FROM Task t
                        LEFT JOIN t.user u
                        WHERE isInTrashBin = false AND
                        u.id = ?1
                        """,
                userId
        )
                .project(TaskDTO.class)
                .list();
    }

    public TaskDTO getTaskDTOById(long taskId, long userId) {
        final var optionalTask = find(
                BASE_QUERY_FIND_TASK,
                taskId,
                userId
        )
                .project(TaskDTO.class)
                .firstResultOptional();

        if (optionalTask.isEmpty())
            throw new NotFoundException("task.notFound");

        return optionalTask.get();
    }

    public Task getTaskById(long taskId, long userId) {
        final var optionalTask = find(
                BASE_QUERY_FIND_TASK,
                taskId,
                userId
        ).firstResultOptional();

        if (optionalTask.isEmpty())
            throw new NotFoundException("task.notFound");

        return optionalTask.get();
    }
}
