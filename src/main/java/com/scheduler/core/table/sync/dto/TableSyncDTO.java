package com.scheduler.core.table.sync.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

@RegisterForReflection
public record TableSyncDTO(String name, long userId, List<?> rows) {
}
