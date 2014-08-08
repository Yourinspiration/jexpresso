package de.yourinspiration.jexpresso;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link Options}.
 *
 * @author Marcel Härle
 */
public class OptionsTest {

    @Test
    public void testAddAndCreate() {
        final Options options = new Options("firstName", "Max").add("lastName", "Mustermann");
        final Map<String, Object> map = options.create();

        assertEquals("Max", map.get("firstName"));
        assertEquals("Mustermann", map.get("lastName"));
    }

}
