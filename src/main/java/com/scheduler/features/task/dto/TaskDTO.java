package com.scheduler.features.task.dto;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;

@RegisterForReflection
public record TaskDTO(
        @ProjectedFieldName("t.id") long id,
        String title,
        String description,
        boolean isConcluded,
        boolean isInTrashBin,
        @ProjectedFieldName("t.createdAt") LocalDateTime createdAt,
        LocalDateTime expiresIn,
        @ProjectedFieldName("t.user.id") long userId
) {
}
