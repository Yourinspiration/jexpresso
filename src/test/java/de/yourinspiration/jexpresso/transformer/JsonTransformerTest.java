package de.yourinspiration.jexpresso.transformer;

import de.yourinspiration.jexpresso.http.ContentType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link JsonTransformer}.
 *
 * @author Marcel Härle
 */
public class JsonTransformerTest {

    private JsonTransformer transformer;

    @Before
    public void setUp() {
        transformer = new JsonTransformer();
    }

    @Test
    public void testRender() {
        final Customer model = new Customer();
        model.firstName = "Max";
        model.lastName = "Mustermann";
        assertEquals("{\"firstName\":\"Max\",\"lastName\":\"Mustermann\"}", transformer.render(model));
    }

    @Test
    public void testToString() {
        assertEquals(JsonTransformer.NAME, transformer.toString());
    }

    @Test
    public void testContentType() {
        assertEquals(ContentType.APPLICATION_JSON, transformer.contentType());
    }

    class Customer {
        String firstName;
        String lastName;
    }

}
