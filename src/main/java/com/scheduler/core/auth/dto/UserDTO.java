package com.scheduler.core.auth.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserDTO(Long id, String name, String email, String password, boolean emailConfirmed) {
}
