package de.yourinspiration.jexpresso.middleware.session;

import de.yourinspiration.jexpresso.core.Request;
import io.netty.handler.codec.http.DefaultCookie;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link SessionImpl}.
 *
 * @author Marcel Härle
 */
public class SessionImplTest {

    private final String sessionId = "4711";
    private SessionImpl sessionImpl;
    @Mock
    private Request request;
    @Mock
    private SessionStore sessionStore;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(request.cookie(SessionSupport.COOKIE_NAME)).thenReturn(Optional.of(
                new DefaultCookie(SessionSupport.COOKIE_NAME, sessionId)));

        sessionImpl = new SessionImpl(request, sessionStore);
    }

    @Test
    public void testGet() {
        Mockito.when(sessionStore.get("test", sessionId, String.class)).thenReturn("myValue");
        assertEquals("myValue", sessionImpl.get("test", String.class));
    }

    @Test
    public void testSet() {
        sessionImpl.set("test", "myValue");
        Mockito.verify(sessionStore).set("test", "myValue", sessionId);
    }

    @Test
    public void testInvalidate() {
        sessionImpl.invalidate();
        Mockito.verify(sessionStore).clear(sessionId);
    }

}
