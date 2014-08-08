package de.yourinspiration.jexpresso.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link UnauthorizedException}.
 *
 * @author Marcel Härle
 */
public class UnauthorizedExceptionTest {

    @Test
    public void testDefaultConstructor() {
        final UnauthorizedException exception = new UnauthorizedException();

        assertEquals("should have the right status", UnauthorizedException.STATUS, exception.getStatus());
        assertEquals("should have the right message", UnauthorizedException.DEFAULT_MSG, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomCause() {
        final Throwable myCause = new RuntimeException();

        final UnauthorizedException exception = new UnauthorizedException(myCause);

        assertEquals("should have the right status", UnauthorizedException.STATUS, exception.getStatus());
        assertEquals("should have the right message", UnauthorizedException.DEFAULT_MSG, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

    @Test
    public void testConstructorForCustomMessage() {
        final String customMessage = "my custome message";

        final UnauthorizedException exception = new UnauthorizedException(customMessage);

        assertEquals("should have the right status", UnauthorizedException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomMessageAndCause() {
        final String customMessage = "my custome message";
        final Throwable myCause = new RuntimeException();

        final UnauthorizedException exception = new UnauthorizedException(customMessage, myCause);

        assertEquals("should have the right status", UnauthorizedException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

}
