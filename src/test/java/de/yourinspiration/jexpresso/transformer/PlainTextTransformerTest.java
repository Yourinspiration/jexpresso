package de.yourinspiration.jexpresso.transformer;

import de.yourinspiration.jexpresso.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link PlainTextTransformer}.
 *
 * @author Marcel Härle
 */
public class PlainTextTransformerTest {

    private PlainTextTransformer transformer;

    @Before
    public void setUp() {
        transformer = new PlainTextTransformer();
    }

    @Test
    public void testRender() {
        final String model = "test";
        assertEquals(model, transformer.render(model));
        assertEquals("", transformer.render(null));
    }

    @Test
    public void testContentType() {
        assertEquals(ContentType.TEXT_PLAIN, transformer.contentType());
    }

}
