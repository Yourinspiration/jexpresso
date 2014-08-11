package de.yourinspiration.jexpresso.session;

import java.io.Serializable;

/**
 * Stores de.yourinspiration.jexpresso.session data as key value pairs.
 *
 * @author Marcel Härle
 */
public interface SessionStore {

    /**
     * Retrieves the value of the de.yourinspiration.jexpresso.session data for the given name. The name must
     * not be null.
     *
     * @param name      the name of the de.yourinspiration.jexpresso.session data
     * @param sessionId the current sessionId
     * @param clazz     the class of the de.yourinspiration.jexpresso.session data
     * @return returns <code>null</code> if no such name exists
     */
    <T extends Serializable> T get(final String name, final String sessionId, final Class<T> clazz);

    /**
     * Sets value for the given name. The name must not be null.
     *
     * @param name      the name for the de.yourinspiration.jexpresso.session data
     * @param value     the value
     * @param sessionId the current sessionId
     */
    void set(final String name, final Serializable value, final String sessionId);

    /**
     * Get the current count of the existing sessions.
     *
     * @return the count of the existing sessions
     */
    long size();

    /**
     * Deletes all sessions and data.
     */
    void clear();

    /**
     * Deletes all de.yourinspiration.jexpresso.session data for the given sessionId.
     *
     * @param sessionId the sessionId
     */
    void clear(final String sessionId);

}
