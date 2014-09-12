package de.yourinspiration.jexpresso.middleware.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple implementation for {@link SessionStore} that stores the de.yourinspiration.jexpresso.middleware.session data
 * in memory. The data will be lost after restarting the application.
 *
 * @author Marcel Härle
 */
public class InMemoryStore implements SessionStore {

    private final ConcurrentHashMap<String, Map<String, Serializable>> sessions = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Serializable> T get(final String key, final String sessionId, final Class<T> clazz) {
        Map<String, Serializable> sessionData = sessions.get(sessionId);
        if (sessionData == null) {
            sessionData = new HashMap<>();
            sessions.put(sessionId, sessionData);
        }
        return (T) sessionData.get(key);
    }

    @Override
    public void set(final String key, final Serializable value, final String sessionId) {
        Map<String, Serializable> sessionData = sessions.get(sessionId);
        if (sessionData == null) {
            sessionData = new HashMap<>();
        }
        sessionData.put(key, value);
        sessions.put(sessionId, sessionData);
    }

    @Override
    public long size() {
        return sessions.size();
    }

    @Override
    public void clear() {
        sessions.clear();
    }

    @Override
    public void clear(String sessionId) {
        sessions.remove(sessionId);
    }

}
