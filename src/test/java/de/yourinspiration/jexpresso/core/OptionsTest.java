package de.yourinspiration.jexpresso.core;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link de.yourinspiration.jexpresso.core.Options}.
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
