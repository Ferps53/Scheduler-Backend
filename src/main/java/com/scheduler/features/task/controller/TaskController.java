package com.scheduler.features.task.controller;

import com.scheduler.core.auth.model.User;
import com.scheduler.core.exceptions.exception.NotFoundException;
import com.scheduler.features.task.dto.NewTaskDTO;
import com.scheduler.features.task.dto.TaskDTO;
import com.scheduler.features.task.mapper.TaskMapper;
import com.scheduler.features.task.model.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class TaskController {

    @Inject
    TaskMapper taskMapper;

    @Transactional
    public TaskDTO createTask(NewTaskDTO newTaskDTO, long userId) {

        final Task task = taskMapper.toTask(newTaskDTO);
        task.createdAt = LocalDateTime.now();

        User.findByIdOptional(userId).ifPresentOrElse(
                user -> task.user = (User) user,
                () -> {throw new NotFoundException("user.notFound");}
        );

        task.persist();

        return taskMapper.toTaskDTO(task);
    }

}
