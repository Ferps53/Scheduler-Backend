package com.scheduler.features.task.dto;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;

@RegisterForReflection
public record TaskDTO(
        long id,
        String title,
        String description,
        boolean isConcluded,
        boolean isInTrashBin,
        @ProjectedFieldName("createdAt") LocalDateTime createdAt,
        LocalDateTime expiresIn,
        @ProjectedFieldName("user.id") long userId
) {
}
