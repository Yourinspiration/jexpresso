package de.yourinspiration.jexpresso.exception;

/**
 * Represents a HTTP 404 status.
 * 
 * @author Marcel Härle
 *
 */
public class NotFoundException extends HttpStatusException {

    private static final long serialVersionUID = -2906328483151240449L;

    public static final int STATUS = 404;
    public static final String DEFAULT_MSG = "Not found";

    public NotFoundException() {
        super(STATUS, DEFAULT_MSG);
    }

    public NotFoundException(final Throwable cause) {
        super(STATUS, DEFAULT_MSG, cause);
    }

    public NotFoundException(final String msg) {
        super(STATUS, msg);
    }

    public NotFoundException(final String msg, final Throwable cause) {
        super(STATUS, msg, cause);
    }

}
