package de.yourinspiration.jexpresso.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link MethodNotAllowedException}.
 *
 * @author Marcel Härle
 */
public class MethodNotAllowedExceptionTest {

    @Test
    public void testDefaultConstructor() {
        final MethodNotAllowedException exception = new MethodNotAllowedException();

        assertEquals("should have the right status", MethodNotAllowedException.STATUS, exception.getStatus());
        assertEquals("should have the right message", MethodNotAllowedException.DEFAULT_MSG, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomCause() {
        final Throwable myCause = new RuntimeException();

        final MethodNotAllowedException exception = new MethodNotAllowedException(myCause);

        assertEquals("should have the right status", MethodNotAllowedException.STATUS, exception.getStatus());
        assertEquals("should have the right message", MethodNotAllowedException.DEFAULT_MSG, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

    @Test
    public void testConstructorForCustomMessage() {
        final String customMessage = "my custome message";

        final MethodNotAllowedException exception = new MethodNotAllowedException(customMessage);

        assertEquals("should have the right status", MethodNotAllowedException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomMessageAndCause() {
        final String customMessage = "my custome message";
        final Throwable myCause = new RuntimeException();

        final MethodNotAllowedException exception = new MethodNotAllowedException(customMessage, myCause);

        assertEquals("should have the right status", MethodNotAllowedException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

}
