package com.scheduler.features.task.controller;

import com.scheduler.core.auth.model.User;
import com.scheduler.core.exceptions.exception.NotFoundException;
import com.scheduler.features.task.dto.NewTaskDTO;
import com.scheduler.features.task.dto.TaskDTO;
import com.scheduler.features.task.mapper.TaskMapper;
import com.scheduler.features.task.model.Task;
import com.scheduler.features.task.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TaskController {

    @Inject
    TaskMapper taskMapper;

    @Inject
    TaskRepository taskRepository;

    @Transactional
    public TaskDTO createTask(NewTaskDTO newTaskDTO, long userId) {

        final Task task = taskMapper.toTask(newTaskDTO);
        task.createdAt = LocalDateTime.now();

        User.findByIdOptional(userId).ifPresentOrElse(
                user -> task.user = (User) user,
                () -> {
                    throw new NotFoundException("user.notFound");
                }
        );

        task.persist();

        return taskMapper.toTaskDTO(task);
    }

    public TaskDTO getTaskById(long taskId, long userId) {

        return taskRepository.getTaskDTOById(taskId, userId);
    }

    @Transactional
    public TaskDTO patchTask(long taskId, long userId, NewTaskDTO newTaskDTO) {

        final Task task = taskRepository.getTaskById(taskId, userId);

        task.title = newTaskDTO.title();
        task.description = newTaskDTO.description();
        task.expiresIn = newTaskDTO.expiresIn();

        task.persist();

        return taskMapper.toTaskDTO(task);
    }

    @Transactional
    public TaskDTO markTaskAsCompleted(long taskId, long userId) {

        final Task task = taskRepository.getTaskById(taskId, userId);

        task.isConcluded = !task.isConcluded;
        task.concludedAt = task.isConcluded ? LocalDateTime.now() : null;

        task.persist();

        return taskMapper.toTaskDTO(task);
    }

    @Transactional
    public TaskDTO sendTaskToTrashBin(long taskId, long userId) {

        final Task task = taskRepository.getTaskById(taskId, userId);

        task.isInTrashBin = !task.isInTrashBin;
        task.sentToTrashBinAt = task.isInTrashBin ? LocalDateTime.now() : null;

        task.persist();

        return taskMapper.toTaskDTO(task);
    }

    public List<TaskDTO> getTasksNotInTrashBin(long userId) {

        final Optional<User> optionalUser = User.findByIdOptional(userId);
        if (optionalUser.isEmpty())
            throw new NotFoundException("user.notFound");

        return taskRepository.listTasksNotInTrashBin(userId);
    }
}
