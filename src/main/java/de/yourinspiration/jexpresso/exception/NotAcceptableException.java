package de.yourinspiration.jexpresso.exception;

/**
 * Represents a HTTP 406 status.
 * 
 * @author Marcel Härle
 *
 */
public class NotAcceptableException extends HttpStatusException {

    private static final long serialVersionUID = -4487720404831554702L;

    public static final int STATUS = 406;
    public static final String DEFAULT_MSG = "Not acceptable";

    public NotAcceptableException() {
        super(STATUS, DEFAULT_MSG);
    }

    public NotAcceptableException(final Throwable cause) {
        super(STATUS, DEFAULT_MSG, cause);
    }

    public NotAcceptableException(final String msg) {
        super(STATUS, msg);
    }

    public NotAcceptableException(final String msg, final Throwable cause) {
        super(STATUS, msg, cause);
    }

}
