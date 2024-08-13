package com.scheduler.core.auth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record NewUserCreatedDTO(Long id, String username, String email) {
}