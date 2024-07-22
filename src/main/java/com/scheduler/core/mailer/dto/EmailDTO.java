package com.scheduler.core.mailer.dto;

import com.scheduler.core.mailer.enums.EmailImages;
import com.scheduler.core.mailer.enums.EmailModels;

import java.util.List;

public record EmailDTO(String address, EmailModels model, List<EmailContentsDTO> contents, List<EmailImages> images) {
}
