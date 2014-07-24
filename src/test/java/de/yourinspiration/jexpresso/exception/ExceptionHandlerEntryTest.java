package de.yourinspiration.jexpresso.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.yourinspiration.jexpresso.Request;
import de.yourinspiration.jexpresso.Response;
import de.yourinspiration.jexpresso.RouteHandler;

/**
 * Test case for {@link ExceptionHandlerEntry}.
 * 
 * @author Marcel Härle
 *
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
