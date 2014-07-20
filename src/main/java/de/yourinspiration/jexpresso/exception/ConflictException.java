package de.yourinspiration.jexpresso.exception;

/**
 * Represents a HTTP 409 status.
 * 
 * @author Marcel Härle
 *
 */
public class ConflictException extends HttpStatusException {

    private static final long serialVersionUID = 3402243537072618740L;

    private static final int STATUS = 409;
    private static final String DEFAULT_MSG = "Conflict";

    public ConflictException() {
        super(STATUS, DEFAULT_MSG);
    }

    public ConflictException(final Throwable cause) {
        super(STATUS, DEFAULT_MSG, cause);
    }

    public ConflictException(final String msg) {
        super(STATUS, msg);
    }

    public ConflictException(final String msg, final Throwable cause) {
        super(STATUS, msg, cause);
    }

}
