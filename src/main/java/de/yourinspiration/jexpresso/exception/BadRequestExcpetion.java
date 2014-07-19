package de.yourinspiration.jexpresso.exception;

public class BadRequestExcpetion extends HttpStatusException {

    private static final long serialVersionUID = -645544858331385287L;

    private static final int STATUS = 400;
    private static final String DEFAULT_MSG = "Bad request";

    public BadRequestExcpetion() {
        super(STATUS, DEFAULT_MSG);
    }

    public BadRequestExcpetion(final Throwable cause) {
        super(STATUS, DEFAULT_MSG, cause);
    }

    public BadRequestExcpetion(final String msg) {
        super(STATUS, msg);
    }

    public BadRequestExcpetion(final String msg, final Throwable cause) {
        super(STATUS, msg, cause);
    }

}
