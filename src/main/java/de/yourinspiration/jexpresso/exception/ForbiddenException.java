package de.yourinspiration.jexpresso.exception;

/**
 * Represents a HTTP 403 status.
 * 
 * @author Marcel Härle
 *
 */
public class ForbiddenException extends HttpStatusException {

    private static final long serialVersionUID = 1107196534826049580L;

    private static final int STATUS = 403;
    private static final String DEFAULT_MSG = "Forbidden";

    public ForbiddenException() {
        super(STATUS, DEFAULT_MSG);
    }

    public ForbiddenException(final Throwable cause) {
        super(STATUS, DEFAULT_MSG, cause);
    }

    public ForbiddenException(final String msg) {
        super(STATUS, msg);
    }

    public ForbiddenException(final String msg, final Throwable cause) {
        super(STATUS, msg, cause);
    }

}
