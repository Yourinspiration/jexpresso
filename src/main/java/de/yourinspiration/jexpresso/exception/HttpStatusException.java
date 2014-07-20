package de.yourinspiration.jexpresso.exception;

/**
 * Base class for HTTP status exceptions.
 * 
 * @author Marcel Härle
 *
 */
public abstract class HttpStatusException extends RuntimeException {

    private static final long serialVersionUID = 3035062141739867642L;

    private final int status;

    public HttpStatusException(final int status) {
        super();
        this.status = status;
    }

    public HttpStatusException(final int status, final Throwable cause) {
        super(cause);
        this.status = status;
    }

    public HttpStatusException(final int status, final String msg) {
        super(msg);
        this.status = status;
    }

    public HttpStatusException(final int status, final String msg, final Throwable cause) {
        super(msg, cause);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
