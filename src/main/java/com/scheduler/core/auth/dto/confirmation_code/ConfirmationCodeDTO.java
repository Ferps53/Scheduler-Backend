package com.scheduler.core.auth.dto.confirmation_code;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record ConfirmationCodeDTO(String confirmationCode) {
}