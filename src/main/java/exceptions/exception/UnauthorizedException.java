package exceptions.exception;

import jakarta.ws.rs.core.Response;

public class UnauthorizedException extends GenericException {

    public UnauthorizedException() {
        super("msg.unauthorized", Response.Status.UNAUTHORIZED);
    }

    public UnauthorizedException(String msg) {
        super(msg, Response.Status.UNAUTHORIZED);
    }
}
