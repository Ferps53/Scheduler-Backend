package com.scheduler.core.exceptions;

import com.scheduler.core.exceptions.exception.GenericException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;

@RegisterForReflection
public record ExceptionDTO(int code, String status, String message, LocalDateTime timestamp) {

    public ExceptionDTO(String message) {
        this(
                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );
        System.out.println(message);
    }

    public ExceptionDTO(GenericException exception) {
        this(
                exception.status.getStatusCode(),
                exception.status.getReasonPhrase(),
                exception.getMessage(),
                LocalDateTime.now()
        );
        System.out.println(message);
    }

    public ExceptionDTO(Response.Status status, String message) {
        this(
                status.getStatusCode(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );
    }
}
