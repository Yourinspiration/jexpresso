package de.yourinspiration.jexpresso.exception;

/**
 * Represents a HTTP 405 status.
 *
 * @author Marcel Härle
 */
public class MethodNotAllowedException extends HttpStatusException {

    public static final int STATUS = 405;
    public static final String DEFAULT_MSG = "Method not allowed";
    private static final long serialVersionUID = 7209209316987049325L;

    public MethodNotAllowedException() {
        super(STATUS, DEFAULT_MSG);
    }

    public MethodNotAllowedException(final Throwable cause) {
        super(STATUS, DEFAULT_MSG, cause);
    }

    public MethodNotAllowedException(final String msg) {
        super(STATUS, msg);
    }

    public MethodNotAllowedException(final String msg, final Throwable cause) {
        super(STATUS, msg, cause);
    }

}
