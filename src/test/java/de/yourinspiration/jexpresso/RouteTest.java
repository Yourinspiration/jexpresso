package de.yourinspiration.jexpresso;

import io.netty.handler.codec.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

/**
 * Test case for {@link Route}.
 *
 * @author Marcel Härle
 */
public class RouteTest {

    private final String path = "/test/path";
    private final HttpMethod method = HttpMethod.GET;
    private Route route;
    @Mock
    private RouteHandler routeHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        route = new Route(path, method, routeHandler);
    }

    @Test
    public void testHashCode() {
        assertEquals(route.hashCode(), new Route(path, method, routeHandler).hashCode());
    }

    @Test
    public void testMatchesPathAndMethod() {
        assertTrue(route.matchesPathAndMethod("/test/path", HttpMethod.GET));
        assertFalse(route.matchesPathAndMethod("/test/path/false", HttpMethod.GET));
        assertFalse(route.matchesPathAndMethod("/test/path/false", HttpMethod.PUT));
        assertFalse(route.matchesPathAndMethod("/test/path", HttpMethod.POST));
    }

    @Test
    public void testHandle() {
        final Request request = Mockito.mock(Request.class);
        final Response response = Mockito.mock(Response.class);
        route.handle(request, response);
        Mockito.verify(routeHandler).handle(request, response);
    }

    @Test
    public void testGetPath() {
        assertEquals(path, route.getPath());
    }

    @Test
    public void testGetMethod() {
        assertEquals(method, route.getMethod());
    }

    @Test
    public void testEqualsObject() {
        assertTrue(route.equals(new Route(path, method, routeHandler)));
        assertFalse(route.equals(null));
        assertTrue(route.equals(route));
        assertFalse(route.equals(new Route("/different/path", method, routeHandler)));
        assertFalse(route.equals(new Route(path, HttpMethod.POST, routeHandler)));
    }

    @Test
    public void testToString() {
        assertEquals("[path=" + path + ",method=" + method + "]", route.toString());
    }

}
