package de.yourinspiration.jexpresso.middleware.security.core;

/**
 * Should be thrown if a user could not be found be the given username.
 *
 * @author Marcel Härle
 */
public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = -7119183728405557748L;

    /**
     * Constructs a new exception with the given message.
     *
     * @param message the detail message
     */
    public UserNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public UserNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
