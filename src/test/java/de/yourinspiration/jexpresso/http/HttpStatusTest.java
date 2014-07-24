package de.yourinspiration.jexpresso.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test case for {@link HttpStatus}.
 * 
 * @author Marcel Härle
 *
 */
public class HttpStatusTest {

    @Test
    public void testValue() {
        assertEquals(100, HttpStatus.CONTINUE.value());
    }

    @Test
    public void testReasonPhrase() {
        assertEquals("Continue", HttpStatus.CONTINUE.getReasonPhrase());
    }

    @Test
    public void testToString() {
        assertEquals("100", HttpStatus.CONTINUE.toString());
    }

    @Test
    public void testValueOf() {
        assertEquals(HttpStatus.CONTINUE, HttpStatus.valueOf(100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfForNotExistingType() {
        HttpStatus.valueOf(999999);
    }

}
