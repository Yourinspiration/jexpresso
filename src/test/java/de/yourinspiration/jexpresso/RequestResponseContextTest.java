package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.junit.Assert.assertEquals;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.net.InetSocketAddress;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link RequestResponseContext}.
 * 
 * @author Marcel Härle
 *
 */
public class RequestResponseContextTest {

    private RequestResponseContext ctx;

    @Mock
    private Channel channel;
    @Mock
    private ResponseImpl response;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ctx = new RequestResponseContext(channel, response);
    }

    @Test
    public void testRemoteAddress() {
        final InetSocketAddress socketAddress = Mockito.mock(InetSocketAddress.class);
        Mockito.when(channel.remoteAddress()).thenReturn(socketAddress);
        ctx.remoteAddress();
        Mockito.verify(channel).remoteAddress();
    }

    @Test
    public void testLocalAddress() {
        ctx.localAddress();
        Mockito.verify(channel).localAddress();
    }

    @Test
    public void testSetAndGetAttribute() {
        ctx.setAttribute("test", "value");
        assertEquals("value", ctx.getAttribute("test"));
    }

    @Test
    public void testSetCookie() {
        final FullHttpResponse fullHttpResponse = Mockito.mock(FullHttpResponse.class);
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);

        Mockito.when(response.fullHttpReponse()).thenReturn(fullHttpResponse);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        final Cookie cookie = new DefaultCookie("test", "value");

        ctx.setCookie(cookie);

        Mockito.verify(headers).add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Test
    public void testGetResponse() {
        assertEquals(response, ctx.getResponse());
    }

}
