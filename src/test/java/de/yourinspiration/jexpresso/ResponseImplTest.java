package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.yourinspiration.jexpresso.http.HttpStatus;

/**
 * Test case for {@link ResponseImpl}.
 * 
 * @author Marcel Härle
 *
 */
public class ResponseImplTest {

    private ResponseImpl responseImpl;

    @Mock
    private FullHttpResponse fullHttpResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        responseImpl = new ResponseImpl(fullHttpResponse);
    }

    @Test
    public void testStatusHttpStatus() {
        responseImpl.status(HttpStatus.CREATED);
        Mockito.verify(fullHttpResponse).setStatus(HttpResponseStatus.valueOf(201));
    }

    @Test
    public void testStatus() {
        Mockito.when(fullHttpResponse.getStatus()).thenReturn(HttpResponseStatus.FORBIDDEN);
        assertEquals(HttpStatus.FORBIDDEN, responseImpl.status());
    }

    @Test
    public void testSetStringString() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.set("key", "value");

        Mockito.verify(headers).set("key", "value");
    }

    @Test
    public void testSetMapOfStringString() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        final Map<String, String> fields = new HashMap<>();
        fields.put("key", "value");

        responseImpl.set(fields);

        Mockito.verify(headers).set("key", "value");
    }

    @Test
    public void testGet() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);
        Mockito.when(headers.get("field")).thenReturn("value");

        assertEquals("value", responseImpl.get("field"));
    }

    @Test
    public void testCookie() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        final Cookie cookie = new DefaultCookie("key", "value");

        responseImpl.cookie(cookie);

        Mockito.verify(headers).add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Test(expected = RuntimeException.class)
    public void testClearCookie() {
        responseImpl.clearCookie("");
    }

    @Test
    public void testRedirect() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.redirect("/test");

        assertTrue(responseImpl.isRedirect());
        Mockito.verify(headers).set("Location", "/test");
        Mockito.verify(fullHttpResponse).setStatus(HttpResponseStatus.TEMPORARY_REDIRECT);
    }

    @Test
    public void testLocation() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.location("location");

        Mockito.verify(headers).set(LOCATION, "location");
    }

    @Test
    public void testSendTextHtml() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.send("<h1>test</h1>");

        Mockito.verify(headers).set(CONTENT_TYPE, "text/html");
        assertEquals("<h1>test</h1>", responseImpl.getContent());
    }

    @Test
    public void testSendJson() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        final Customer customer = new Customer();
        customer.name = "Max Mustermann";

        responseImpl.send(customer);

        Mockito.verify(headers).set(CONTENT_TYPE, "application/json");
        assertEquals(customer, responseImpl.getContent());
    }

    @Test
    public void testSendHtmlWithHttpStatus() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.send(HttpStatus.CREATED, "<h1>test</h1>");

        Mockito.verify(headers).set(CONTENT_TYPE, "text/html");
        Mockito.verify(fullHttpResponse).setStatus(HttpResponseStatus.CREATED);
        assertEquals("<h1>test</h1>", responseImpl.getContent());
    }

    @Test
    public void testSendJsonWithHttpStatus() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        final Customer customer = new Customer();
        customer.name = "Max Mustermann";

        responseImpl.send(HttpStatus.CREATED, customer);

        Mockito.verify(headers).set(CONTENT_TYPE, "application/json");
        assertEquals(customer, responseImpl.getContent());
        Mockito.verify(fullHttpResponse).setStatus(HttpResponseStatus.CREATED);
    }

    @Test
    public void testSendByteArray() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.send("test".getBytes());

        Mockito.verify(headers).set(CONTENT_TYPE, "application/octet-stream");
        assertArrayEquals("test".getBytes(), responseImpl.getBytes());
        assertTrue(responseImpl.isBinary());
    }

    @Test
    public void testSendByteArrayWithHttpStatus() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.send(HttpStatus.CREATED, "test".getBytes());

        Mockito.verify(headers).set(CONTENT_TYPE, "application/octet-stream");
        Mockito.verify(fullHttpResponse).setStatus(HttpResponseStatus.CREATED);
        assertArrayEquals("test".getBytes(), responseImpl.getBytes());
        assertTrue(responseImpl.isBinary());
    }

    @Test
    public void testSendHttpStatus() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.send(HttpStatus.CREATED);

        Mockito.verify(fullHttpResponse).setStatus(HttpResponseStatus.CREATED);
    }

    @Test
    public void testJsonObject() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        final Customer customer = new Customer();
        customer.name = "Max Mustermann";

        responseImpl.json(customer);

        Mockito.verify(headers).set(CONTENT_TYPE, "application/json");
        assertEquals(customer, responseImpl.getContent());
    }

    @Test
    public void testJsonWithHttpStatus() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        final Customer customer = new Customer();
        customer.name = "Max Mustermann";

        responseImpl.json(HttpStatus.CREATED, customer);

        Mockito.verify(headers).set(CONTENT_TYPE, "application/json");
        Mockito.verify(fullHttpResponse).setStatus(HttpResponseStatus.CREATED);
        assertEquals(customer, responseImpl.getContent());
    }

    @Test(expected = RuntimeException.class)
    public void testJsonpObject() {
        responseImpl.jsonp(null);
    }

    @Test(expected = RuntimeException.class)
    public void testJsonpHttpStatusObject() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.jsonp(HttpStatus.CREATED, null);
    }

    @Test
    public void testSetType() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        responseImpl.type("application/json");

        Mockito.verify(headers).set(CONTENT_TYPE, "application/json");
    }

    @Test
    public void testGetType() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);
        Mockito.when(headers.get("Content-Type")).thenReturn("application/json");

        assertEquals("application/json", responseImpl.type());
    }

    @Test
    public void testRender() {
        final HttpHeaders headers = Mockito.mock(HttpHeaders.class);
        Mockito.when(fullHttpResponse.headers()).thenReturn(headers);

        final Options options = new Options("test", "value");

        responseImpl.render("view", options);

        Mockito.verify(headers).set(CONTENT_TYPE, "text/html");
        assertEquals("view", responseImpl.getTemplate());
        assertTrue(responseImpl.isTemplate());
        assertEquals(options.create(), responseImpl.getOptions());
    }

    class Customer {
        String name;
    }

}
