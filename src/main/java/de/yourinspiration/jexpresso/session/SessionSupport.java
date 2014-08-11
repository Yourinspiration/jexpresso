package de.yourinspiration.jexpresso.session;

import de.yourinspiration.jexpresso.core.MiddlewareHandler;
import de.yourinspiration.jexpresso.core.Next;
import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;

import java.util.UUID;

/**
 * A middleware component for de.yourinspiration.jexpresso.session support. A de.yourinspiration.jexpresso.session id (JEXPRESSIONID) will
 * be stored as a cookie.
 *
 * @author Marcel Härle
 */
public class SessionSupport implements MiddlewareHandler {

    public static final String COOKIE_NAME = "JEXPRESSOSESSIONID";
    public static final String SESSION_ATTR = "session";

    private final SessionStore sessionStore;

    /**
     * Constructs a new de.yourinspiration.jexpresso.session middleware with the given de.yourinspiration.jexpresso.session store.
     *
     * @param sessionStore the de.yourinspiration.jexpresso.session store
     */
    public SessionSupport(final SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    /**
     * Helper method to retrieve the de.yourinspiration.jexpresso.session of the current request.
     *
     * @param request the current request
     * @return returns <code>null</code> if no de.yourinspiration.jexpresso.session exists
     */
    public static Session session(final Request request) {
        return (Session) request.attribute(SessionSupport.SESSION_ATTR);
    }

    @Override
    public void handle(final Request request, final Response response, final Next next) {
        if (request.cookie(COOKIE_NAME) == null) {
            final String sessionId = generateSessionId();
            final Cookie cookie = new DefaultCookie(COOKIE_NAME, sessionId);
            response.cookie(cookie);
            request.setCookie(cookie);
        }

        request.attribute(SESSION_ATTR, new SessionImpl(request, sessionStore));

        next.next();
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString().replaceAll("-", "") + UUID.randomUUID().toString().replaceAll("-", "");
    }

}
