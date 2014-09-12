package de.yourinspiration.jexpresso.middleware.accesslog;

import de.yourinspiration.jexpresso.core.Next;
import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.OutputStream;

/**
 * Test case for {@link AccessLog}.
 *
 * @author Marcel Härle
 */
public class AccessLogTest {

    private final AccessLogFormat format = AccessLogFormat.DEVELOPER;
    private AccessLog accessLog;
    @Mock
    private OutputStream out;
    @Mock
    private Request request;
    @Mock
    private Response response;
    @Mock
    private Next next;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        accessLog = new AccessLog(format, out);
    }

    @Test
    public void testHandle() {
        accessLog.handle(request, response, next);
        Mockito.verify(response).addListener(Matchers.any(AccessLogResponseListener.class));
        Mockito.verify(next).next();
    }

}
