package de.yourinspiration.jexpresso.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link ConflictException}.
 *
 * @author Marcel Härle
 */
public class ConflictExceptionTest {

    @Test
    public void testDefaultConstructor() {
        final ConflictException exception = new ConflictException();

        assertEquals("should have the right status", ConflictException.STATUS, exception.getStatus());
        assertEquals("should have the right message", ConflictException.DEFAULT_MSG, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomCause() {
        final Throwable myCause = new RuntimeException();

        final ConflictException exception = new ConflictException(myCause);

        assertEquals("should have the right status", ConflictException.STATUS, exception.getStatus());
        assertEquals("should have the right message", ConflictException.DEFAULT_MSG, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

    @Test
    public void testConstructorForCustomMessage() {
        final String customMessage = "my custome message";

        final ConflictException exception = new ConflictException(customMessage);

        assertEquals("should have the right status", ConflictException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomMessageAndCause() {
        final String customMessage = "my custome message";
        final Throwable myCause = new RuntimeException();

        final ConflictException exception = new ConflictException(customMessage, myCause);

        assertEquals("should have the right status", ConflictException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

}
