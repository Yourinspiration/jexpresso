package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.exception.BadRequestException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test case for {@link de.yourinspiration.jexpresso.core.RequestImpl}.
 *
 * @author Marcel Härle
 */
public class RequestImplTest {

    private RequestImpl requestImpl;

    @Mock
    private FullHttpRequest fullHttpRequest;
    @Mock
    private RequestResponseContext requestResponseContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        requestImpl = new RequestImpl(fullHttpRequest, requestResponseContext);
    }

    @Test
    public void testBody() {
        final ByteBuf byteBuf = Mockito.mock(ByteBuf.class);
        Mockito.when(byteBuf.toString(Matchers.any(Charset.class))).thenReturn("test");
        Mockito.when(fullHttpRequest.content()).thenReturn(byteBuf);

        assertEquals("test", requestImpl.body());
    }

    @Test
    public void testBytes() {
        final ByteBuf byteBuf = Mockito.mock(ByteBuf.class);
        Mockito.when(byteBuf.array()).thenReturn("test".getBytes());
        Mockito.when(fullHttpRequest.content()).thenReturn(byteBuf);

        assertArrayEquals("test".getBytes(), requestImpl.bytes());
    }

    @Test
    public void testJson() {
        final ByteBuf byteBuf = Mockito.mock(ByteBuf.class);
        Mockito.when(byteBuf.toString(Matchers.any(Charset.class))).thenReturn("{\"name\":\"Max\"}");
        Mockito.when(fullHttpRequest.content()).thenReturn(byteBuf);

        Customer customer = requestImpl.json(Customer.class);

        assertEquals("Max", customer.name);
    }

    @Test(expected = BadRequestException.class)
    public void testJsonForInvalidJson() {
        final ByteBuf byteBuf = Mockito.mock(ByteBuf.class);
        Mockito.when(byteBuf.toString(Matchers.any(Charset.class))).thenReturn("{\"name\":Max");
        Mockito.when(fullHttpRequest.content()).thenReturn(byteBuf);

        requestImpl.json(Customer.class);
    }

    @Test
    public void testParams() {
        final Route route = new Route("/customer/:id", HttpMethod.GET, (request, response) -> {
        });

        requestImpl.setRoute(route);

        Mockito.when(fullHttpRequest.getUri()).thenReturn("/customer/123");

        final Map<String, String> params = requestImpl.params();

        assertEquals(1, params.size());
        assertEquals("123", params.get("id"));
    }

    @Test
    public void testParam() {
        final Route route = new Route("/customer/:id", HttpMethod.GET, (request, response) -> {
        });

        requestImpl.setRoute(route);

        Mockito.when(fullHttpRequest.getUri()).thenReturn("/customer/123");

        assertEquals("123", requestImpl.param("id"));
    }

    @Test
    public void testParamWithQueryParam() {
        final Route route = new Route("/customer/:id", HttpMethod.GET, (request, response) -> {
        });

        requestImpl.setRoute(route);

        Mockito.when(fullHttpRequest.getUri()).thenReturn("/customer/123?token=asldkjasldkjalskdjalskdj");

        assertEquals("123", requestImpl.param("id"));
    }

    @Test
    public void testQuery() {
        Mockito.when(fullHttpRequest.getUri()).thenReturn("/customer?name=Max&age=21");

        final Map<String, String> queries = requestImpl.query();

        assertEquals(2, queries.size());
        assertEquals("Max", queries.get("name"));
        assertEquals("21", queries.get("age"));
    }

    @Test
    public void testQueryString() {
        Mockito.when(fullHttpRequest.getUri()).thenReturn("/customer?name=Max&age=21");

        assertEquals("Max", requestImpl.query("name"));
        assertEquals("21", requestImpl.query("age"));
    }

    @Test
    public void testCookies() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Cookie", ServerCookieEncoder.encode(new DefaultCookie("testKey", "testValue")));

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        final List<Cookie> cookies = requestImpl.cookies();

        assertEquals(1, cookies.size());
        assertEquals("testKey", cookies.get(0).getName());
        assertEquals("testValue", cookies.get(0).getValue());
    }

    @Test
    public void testCookie() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Cookie", ServerCookieEncoder.encode(new DefaultCookie("testKey", "testValue")));

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        final Cookie cookie = requestImpl.cookie("testKey");

        assertNotNull(cookie);
        assertEquals("testKey", cookie.getName());
        assertEquals("testValue", cookie.getValue());
    }

    @Test
    public void testCookieForMissingCookie() {
        final HttpHeaders headers = new DefaultHttpHeaders();

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        final Cookie cookie = requestImpl.cookie("testKey");

        assertNull(cookie);
    }

    @Test
    public void testGet() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Content-Type", "text/html");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertEquals("text/html", requestImpl.get("Content-Type"));
    }

    @Test
    public void testAccepts() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertEquals("text/html", requestImpl.accepts("test/test", "text/html"));
    }

    @Test
    public void testAcceptsForNoAcceptableType() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertNull(requestImpl.accepts("test/test", "text/nothing"));
    }

    @Test
    public void testAcceptsCharset() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Accept-Charset", "iso-8859-5, unicode-1-1;q=0.8");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertEquals("unicode-1-1", requestImpl.acceptsCharset("utf-8", "unicode-1-1"));
    }

    @Test
    public void testAcceptsCharsetForNotAcceptableCharset() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Accept-Charset", "iso-8859-5, unicode-1-1;q=0.8");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertNull(requestImpl.acceptsCharset("utf-8"));
    }

    @Test
    public void testAcceptsLanguage() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Accept-Language", "da, en-gb;q=0.8, en;q=0.7");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertEquals("en", requestImpl.acceptsLanguage("de", "en", "fr"));
    }

    @Test
    public void testAcceptsLanguageForNoAcceptableLanguage() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Accept-Language", "da, en-gb;q=0.8, en;q=0.7");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertNull(requestImpl.acceptsLanguage("de", "fr"));
    }

    @Test
    public void testIs() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Content-Type", "application/json");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertTrue(requestImpl.is("application/json"));
        assertTrue(requestImpl.is("application/*"));
        assertFalse(requestImpl.is("application/xml"));
    }

    @Test
    public void testPath() {
        final String path = "/test/path";
        Mockito.when(fullHttpRequest.getUri()).thenReturn(path);
        assertEquals(path, requestImpl.path());
    }

    @Test
    public void testHost() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("Host", "someHost");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertEquals("someHost", requestImpl.host());
    }

    @Test
    public void testXhr() {
        final HttpHeaders headers = new DefaultHttpHeaders();
        headers.add("X-Requested-With", "XMLHttpRequest");

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertTrue(requestImpl.xhr());
    }

    @Test
    public void testXhrForMissingHeader() {
        final HttpHeaders headers = new DefaultHttpHeaders();

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        assertFalse(requestImpl.xhr());
    }

    @Test
    public void testProtocol() {
        final HttpVersion httpVersion = Mockito.mock(HttpVersion.class);
        Mockito.when(httpVersion.protocolName()).thenReturn("http");
        Mockito.when(fullHttpRequest.getProtocolVersion()).thenReturn(httpVersion);
        assertEquals("http", requestImpl.protocol());
    }

    @Test
    public void testSecure() {
        final HttpVersion httpVersion = Mockito.mock(HttpVersion.class);
        Mockito.when(httpVersion.protocolName()).thenReturn("http");
        Mockito.when(fullHttpRequest.getProtocolVersion()).thenReturn(httpVersion);
        assertFalse(requestImpl.secure());
    }

    @Test
    public void testGetAttribute() {
        Mockito.when(requestResponseContext.getAttribute("test")).thenReturn("value");
        assertEquals("value", requestImpl.attribute("test"));
    }

    @Test
    public void testSetAttribute() {
        requestImpl.attribute("test", "value");
        Mockito.verify(requestResponseContext).setAttribute("test", "value");
    }

    @Test
    public void testMethod() {
        Mockito.when(fullHttpRequest.getMethod()).thenReturn(HttpMethod.GET);
        assertEquals(HttpMethod.GET, requestImpl.method());
    }

    @Test
    public void testCustomCookie() {
        final HttpHeaders headers = new DefaultHttpHeaders();

        Mockito.when(fullHttpRequest.headers()).thenReturn(headers);

        requestImpl.setCookie(new DefaultCookie("key", "value"));

        assertEquals("value", requestImpl.cookie("key").getValue());
    }

    class Customer {
        String name;
    }

}
