package de.yourinspiration.jexpresso.middleware.accesslog;

import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;
import de.yourinspiration.jexpresso.http.HttpStatus;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * Test case for {@link AccessLogResponseListener}.
 *
 * @author Marcel Härle
 */
public class AccessLogResponseListenerTest {

    private final HttpMethod method = HttpMethod.GET;
    private final String path = "/test";
    private final String ip = "156.135.125.22";
    private final HttpStatus status = HttpStatus.OK;
    private AccessLogResponseListener accessLogResponseListener;
    @Mock
    private WritableByteChannel channel;
    @Mock
    private Request request;
    @Mock
    private Response response;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(request.method()).thenReturn(method);
        Mockito.when(request.path()).thenReturn(path);
        Mockito.when(request.ip()).thenReturn(ip);
        Mockito.when(response.status()).thenReturn(status);
    }

    @Test
    public void testCallbackForDeveloperFormat() throws IOException {
        accessLogResponseListener = new AccessLogResponseListener(AccessLogFormat.DEVELOPER, channel);
        accessLogResponseListener.callback(request, response);
        Mockito.verify(channel).write(Matchers.any(ByteBuffer.class));
    }

    @Test
    public void testCallbackForSmallFormat() throws IOException {
        accessLogResponseListener = new AccessLogResponseListener(AccessLogFormat.SMALL, channel);
        accessLogResponseListener.callback(request, response);
        Mockito.verify(channel).write(Matchers.any(ByteBuffer.class));
    }

    @Test
    public void testCallbackForTinyFormat() throws IOException {
        accessLogResponseListener = new AccessLogResponseListener(AccessLogFormat.TINY, channel);
        accessLogResponseListener.callback(request, response);
        Mockito.verify(channel).write(Matchers.any(ByteBuffer.class));
    }

}
