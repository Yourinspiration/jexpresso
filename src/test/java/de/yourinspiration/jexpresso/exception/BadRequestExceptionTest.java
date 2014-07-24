package de.yourinspiration.jexpresso.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test case for {@link BadRequestException}.
 * 
 * @author Marcel Härle
 *
 */
public class BadRequestExceptionTest {

    @Test
    public void testDefaultConstructor() {
        final BadRequestException badRequestException = new BadRequestException();

        assertEquals("should have the right status", BadRequestException.STATUS, badRequestException.getStatus());
        assertEquals("should have the right message", BadRequestException.DEFAULT_MSG, badRequestException.getMessage());
    }

    @Test
    public void testConstructorForCustomCause() {
        final Throwable myCause = new RuntimeException();

        final BadRequestException badRequestException = new BadRequestException(myCause);

        assertEquals("should have the right status", BadRequestException.STATUS, badRequestException.getStatus());
        assertEquals("should have the right message", BadRequestException.DEFAULT_MSG, badRequestException.getMessage());
        assertEquals("should have the right cause", myCause, badRequestException.getCause());
    }

    @Test
    public void testConstructorForCustomMessage() {
        final String customMessage = "my custome message";

        final BadRequestException badRequestException = new BadRequestException(customMessage);

        assertEquals("should have the right status", BadRequestException.STATUS, badRequestException.getStatus());
        assertEquals("should have the right message", customMessage, badRequestException.getMessage());
    }

    @Test
    public void testConstructorForCustomMessageAndCause() {
        final String customMessage = "my custome message";
        final Throwable myCause = new RuntimeException();

        final BadRequestException badRequestException = new BadRequestException(customMessage, myCause);

        assertEquals("should have the right status", BadRequestException.STATUS, badRequestException.getStatus());
        assertEquals("should have the right message", customMessage, badRequestException.getMessage());
        assertEquals("should have the right cause", myCause, badRequestException.getCause());
    }

}
