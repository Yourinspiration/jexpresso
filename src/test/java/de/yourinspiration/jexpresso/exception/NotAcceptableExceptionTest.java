package de.yourinspiration.jexpresso.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link NotAcceptableException}.
 *
 * @author Marcel Härle
 */
public class NotAcceptableExceptionTest {

    @Test
    public void testDefaultConstructor() {
        final NotAcceptableException exception = new NotAcceptableException();

        assertEquals("should have the right status", NotAcceptableException.STATUS, exception.getStatus());
        assertEquals("should have the right message", NotAcceptableException.DEFAULT_MSG, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomCause() {
        final Throwable myCause = new RuntimeException();

        final NotAcceptableException exception = new NotAcceptableException(myCause);

        assertEquals("should have the right status", NotAcceptableException.STATUS, exception.getStatus());
        assertEquals("should have the right message", NotAcceptableException.DEFAULT_MSG, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

    @Test
    public void testConstructorForCustomMessage() {
        final String customMessage = "my custome message";

        final NotAcceptableException exception = new NotAcceptableException(customMessage);

        assertEquals("should have the right status", NotAcceptableException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomMessageAndCause() {
        final String customMessage = "my custome message";
        final Throwable myCause = new RuntimeException();

        final NotAcceptableException exception = new NotAcceptableException(customMessage, myCause);

        assertEquals("should have the right status", NotAcceptableException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

}
