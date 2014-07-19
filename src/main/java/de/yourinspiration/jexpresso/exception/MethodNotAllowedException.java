package de.yourinspiration.jexpresso.exception;

public class MethodNotAllowedException extends HttpStatusException {

    private static final long serialVersionUID = 7209209316987049325L;

    private static final int STATUS = 405;
    private static final String DEFAULT_MSG = "Method not allowed";

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
