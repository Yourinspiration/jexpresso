package de.yourinspiration.jexpresso.middleware.session;

import java.io.Serializable;

/**
 * Represents a user session for HTTP requests.
 *
 * @author Marcel Härle
 */
public interface Session {

    /**
     * Get the session data for the given name.
     *
     * @param name  the name of the session value
     * @param clazz the class of the value
     * @param <T>   the value type
     * @return returns <code>null</code> if no such name exists
     */
    <T extends Serializable> T get(final String name, final Class<T> clazz);

    /**
     * Set the value for the given name.
     *
     * @param name  the name
     * @param value the value
     */
    void set(final String name, final Serializable value);

    /**
     * Invalidate the session.
     */
    void invalidate();

}
