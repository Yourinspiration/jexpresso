package de.yourinspiration.jexpresso.basicauth;

import io.netty.handler.codec.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for {@link SecurityRoute}.
 *
 * @author Marcel Härle
 */
public class SecurityRouteTest {

    private final String path = "/test/path";
    private final String authorities = "USER";
    private final HttpMethod[] methods = new HttpMethod[]{HttpMethod.GET};
    private SecurityRoute securityRoute;

    @Before
    public void setUp() {
        securityRoute = new SecurityRoute(path, authorities, methods);
    }

    @Test
    public void testGetPath() {
        assertEquals(path, securityRoute.getPath());
    }

    @Test
    public void testGetAuthorities() {
        assertEquals(authorities, securityRoute.getAuthorities());
    }

    @Test
    public void testGetMethods() {
        assertArrayEquals(methods, securityRoute.getMethods());
    }

    @Test
    public void testMatchesPathAndMethod() {
        assertTrue(securityRoute.matchesPathAndMethod(path, HttpMethod.GET));
        assertFalse(securityRoute.matchesPathAndMethod("/false/path", HttpMethod.GET));
        assertFalse(securityRoute.matchesPathAndMethod(path, HttpMethod.POST));
        assertFalse(securityRoute.matchesPathAndMethod("/false/path", HttpMethod.POST));
    }

}
