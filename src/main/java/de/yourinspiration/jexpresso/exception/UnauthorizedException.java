package de.yourinspiration.jexpresso.exception;

/**
 * Represents a HTTP 401 status.
 * 
 * @author Marcel Härle
 *
 */
public class UnauthorizedException extends HttpStatusException {

    private static final long serialVersionUID = -6389042529808259602L;

    public static final int STATUS = 401;
    public static final String DEFAULT_MSG = "Unauthorized";

    public UnauthorizedException() {
        super(STATUS, DEFAULT_MSG);
    }

    public UnauthorizedException(final Throwable cause) {
        super(STATUS, DEFAULT_MSG, cause);
    }

    public UnauthorizedException(final String msg) {
        super(STATUS, msg);
    }

    public UnauthorizedException(final String msg, final Throwable cause) {
        super(STATUS, msg, cause);
    }

}
