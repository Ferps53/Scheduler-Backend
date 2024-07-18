package com.scheduler.features.task.dto;

public record TaskDTO(
        long id,
        String title,
        String description,
        boolean isConcluded,
        boolean isInTrashBin,
        String createdAt,
        String expiresIn,
        long userId
) {
}
