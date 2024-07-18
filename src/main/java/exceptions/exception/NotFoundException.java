package exceptions.exception;

import jakarta.ws.rs.core.Response;

public class NotFoundException extends GenericException {

    public NotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }

    public NotFoundException() {
        super("msg.notfound", Response.Status.NOT_FOUND);
    }
}
