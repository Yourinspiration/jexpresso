package de.yourinspiration.jexpresso.exception;

public class NotAcceptableException extends HttpStatusException {

    private static final long serialVersionUID = -4487720404831554702L;

    private static final int STATUS = 406;
    private static final String DEFAULT_MSG = "Not acceptable";

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
