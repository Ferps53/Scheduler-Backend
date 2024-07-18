package exceptions.exception;

import exceptions.MessageTranslator;
import jakarta.ws.rs.core.Response;

public class GenericException extends RuntimeException {

    public final transient Response.StatusType statusType;

    public GenericException(String message, Response.StatusType statusType) {
        super(MessageTranslator.translate(message));
        this.statusType = statusType;
    }
}
