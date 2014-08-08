package de.yourinspiration.jexpresso.exception;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link NotFoundException}.
 *
 * @author Marcel Härle
 */
public class NotFoundExceptionTest {

    @Test
    public void testDefaultConstructor() {
        final NotFoundException exception = new NotFoundException();

        assertEquals("should have the right status", NotFoundException.STATUS, exception.getStatus());
        assertEquals("should have the right message", NotFoundException.DEFAULT_MSG, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomCause() {
        final Throwable myCause = new RuntimeException();

        final NotFoundException exception = new NotFoundException(myCause);

        assertEquals("should have the right status", NotFoundException.STATUS, exception.getStatus());
        assertEquals("should have the right message", NotFoundException.DEFAULT_MSG, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

    @Test
    public void testConstructorForCustomMessage() {
        final String customMessage = "my custome message";

        final NotFoundException exception = new NotFoundException(customMessage);

        assertEquals("should have the right status", NotFoundException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
    }

    @Test
    public void testConstructorForCustomMessageAndCause() {
        final String customMessage = "my custome message";
        final Throwable myCause = new RuntimeException();

        final NotFoundException exception = new NotFoundException(customMessage, myCause);

        assertEquals("should have the right status", NotFoundException.STATUS, exception.getStatus());
        assertEquals("should have the right message", customMessage, exception.getMessage());
        assertEquals("should have the right cause", myCause, exception.getCause());
    }

}
