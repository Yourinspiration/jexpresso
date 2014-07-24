package de.yourinspiration.jexpresso.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test case for {@link ContentType}.
 * 
 * @author Marcel Härle
 *
 */
public class ContentTypeTest {

    @Test
    public void testType() {
        assertEquals("application/json", ContentType.APPLICATION_JSON.type());
        assertEquals("application/xml", ContentType.APPLICATION_XML.type());
        assertEquals("text/plain", ContentType.TEXT_PLAIN.type());
        assertEquals("text/html", ContentType.TEXT_HTML.type());
    }

    @Test
    public void testToString() {
        assertEquals("application/json", ContentType.APPLICATION_JSON.toString());
        assertEquals("application/xml", ContentType.APPLICATION_XML.toString());
        assertEquals("text/plain", ContentType.TEXT_PLAIN.toString());
        assertEquals("text/html", ContentType.TEXT_HTML.toString());
    }

}
