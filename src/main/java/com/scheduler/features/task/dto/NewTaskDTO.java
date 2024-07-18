package com.scheduler.features.task.dto;

import java.time.LocalDateTime;

public record NewTaskDTO(String title, String description, LocalDateTime expiresIn) {
}
