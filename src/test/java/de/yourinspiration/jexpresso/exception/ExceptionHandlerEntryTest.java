package de.yourinspiration.jexpresso.exception;

import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;
import de.yourinspiration.jexpresso.core.RouteHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

/**
 * Test case for {@link ExceptionHandlerEntry}.
 *
 * @author Marcel Härle
 */
public class ExceptionHandlerEntryTest {

    private ExceptionHandlerEntry entry;

    private Class<? extends Exception> exceptionClass;
    @Mock
    private RouteHandler routeHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        exceptionClass = IllegalArgumentException.class;
        entry = new ExceptionHandlerEntry(exceptionClass, routeHandler);
    }

    @Test
    public void testIsInstanceOf() throws InstantiationException, IllegalAccessException {
        assertTrue("should be true for the right exception", entry.isInstanceOf(exceptionClass.newInstance()));
        assertFalse("should be false for the false excpetion", entry.isInstanceOf(new IllegalAccessException("failed")));
    }

    @Test
    public void testInvokeHandler() {
        final Request request = Mockito.mock(Request.class);
        final Response response = Mockito.mock(Response.class);

        entry.invokeHandler(request, response);

        Mockito.verify(routeHandler).handle(request, response);
    }

    @Test
    public void testToString() {
        assertEquals("[exceptionClass=" + exceptionClass.getName() + "]", entry.toString());
    }

}
