package de.yourinspiration.jexpresso.exception;

/**
 * Represents a HTTP 400 status.
 * 
 * @author Marcel Härle
 *
 */
public class BadRequestException extends HttpStatusException {

    private static final long serialVersionUID = -645544858331385287L;

    public static final int STATUS = 400;
    public static final String DEFAULT_MSG = "Bad request";

    public BadRequestException() {
        super(STATUS, DEFAULT_MSG);
    }

    public BadRequestException(final Throwable cause) {
        super(STATUS, DEFAULT_MSG, cause);
    }

    public BadRequestException(final String msg) {
        super(STATUS, msg);
    }

    public BadRequestException(final String msg, final Throwable cause) {
        super(STATUS, msg, cause);
    }

}
