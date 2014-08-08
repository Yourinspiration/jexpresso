package de.yourinspiration.jexpresso.transformer;

import de.yourinspiration.jexpresso.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link HtmlTransformer}.
 *
 * @author Marcel Härle
 */
public class HtmlTransformerTest {

    private HtmlTransformer transformer;

    @Before
    public void setUp() {
        transformer = new HtmlTransformer();
    }

    @Test
    public void testRender() {
        final String model = "<h1>Test</h1>";
        assertEquals(model, transformer.render(model));
        assertEquals("", transformer.render(null));
    }

    @Test
    public void testContentType() {
        assertEquals(ContentType.TEXT_HTML, transformer.contentType());
    }

}
