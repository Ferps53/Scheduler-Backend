package com.scheduler.features.task.mapper;

import com.scheduler.features.task.dto.NewTaskDTO;
import com.scheduler.features.task.dto.TaskDTO;
import com.scheduler.features.task.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "jakarta-cdi")
public interface TaskMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "sentToTrashBinAt", ignore = true)
    @Mapping(target = "isInTrashBin", constant = "false")
    @Mapping(target = "isConcluded", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "concludedAt", ignore = true)
    Task toTask(NewTaskDTO newTaskDTO);

    @Mapping(target = "userId", source = "task.user.id")
    TaskDTO toTaskDTO(Task task);

}
