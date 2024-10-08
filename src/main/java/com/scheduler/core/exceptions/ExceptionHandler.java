package com.scheduler.core.exceptions;

import com.scheduler.core.exceptions.exception.GenericException;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@RegisterForReflection
public class ExceptionHandler implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException e) {
        Throwable cause = e;

        e.printStackTrace();

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        if (cause instanceof GenericException exception) {

            if (exception.status.getStatusCode() == 500) {
                e.printStackTrace();
            }

            return Response.status(exception.status)
                    .entity(new ExceptionDTO(exception))
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        final ExceptionDTO error = new ExceptionDTO(e.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON).
                entity(error)
                .build();

    }
}