package de.yourinspiration.jexpresso.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link ForbiddenException}.
 *
 * @author Marcel Härle
 */
public class ForbiddenExceptionTest {

    @Test
    public void testDefaultConstructor() {
        final ForbiddenException exception = new ForbiddenException();

        assertEquals("should have the right status", ForbiddenException.STATUS, exception.getStatus());
        assertEquals("should have the right message", ForbiddenException.DEFAULT_MSG, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomCause() {
        final Throwable myCause = new RuntimeException();

        final ForbiddenException exception = new ForbiddenException(myCause);

        assertEquals("should have the right status", ForbiddenException.STATUS, exception.getStatus());
        assertEquals("should have the right message", ForbiddenException.DEFAULT_MSG, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

    @Test
    public void testConstructorForCustomMessage() {
        final String customMessage = "my custome message";

        final ForbiddenException exception = new ForbiddenException(customMessage);

        assertEquals("should have the right status", ForbiddenException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomMessageAndCause() {
        final String customMessage = "my custome message";
        final Throwable myCause = new RuntimeException();

        final ForbiddenException exception = new ForbiddenException(customMessage, myCause);

        assertEquals("should have the right status", ForbiddenException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

}
