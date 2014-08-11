package de.yourinspiration.jexpresso.session;

import de.yourinspiration.jexpresso.core.Next;
import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;
import io.netty.handler.codec.http.DefaultCookie;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link SessionSupport}.
 *
 * @author Marcel Härle
 */
public class SessionSupportTest {

    private SessionSupport jexpressoSessionSupport;

    @Mock
    private SessionStore sessionStore;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jexpressoSessionSupport = new SessionSupport(sessionStore);
    }

    @Test
    public void testHandle() {
        final Request request = Mockito.mock(Request.class);
        final Response response = Mockito.mock(Response.class);
        final Next next = Mockito.mock(Next.class);

        Mockito.when(request.cookie(SessionSupport.COOKIE_NAME)).thenReturn(
                new DefaultCookie(SessionSupport.COOKIE_NAME, "4711"));

        jexpressoSessionSupport.handle(request, response, next);

        Mockito.verify(request).attribute(Matchers.eq(SessionSupport.SESSION_ATTR), Matchers.any(SessionImpl.class));
        Mockito.verify(next).next();
    }
}
