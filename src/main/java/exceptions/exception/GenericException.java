package exceptions.exception;

import exceptions.MessageTranslator;
import jakarta.ws.rs.core.Response;

public class GenericException extends RuntimeException {

    public final transient Response.StatusType status;

    public GenericException(String message, Response.StatusType status) {
        super(MessageTranslator.translate(message));
        this.status = status;
    }
}
