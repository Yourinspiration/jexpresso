package de.yourinspiration.jexpresso.middleware.security.core.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link SHA256PasswordEncoder}.
 *
 * @author Marcel Härle
 */
public class SHA256PasswordEncoderTest {

    private SHA256PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        passwordEncoder = new SHA256PasswordEncoder();
    }

    @Test
    public void testCheckpw() {
        final String plaintext = "test";
        final String encoded = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
        assertTrue(passwordEncoder.checkpw(plaintext, encoded));
    }

    @Test
    public void testEncode() {
        final String plaintext = "test";
        final String expected = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
        assertEquals(expected, passwordEncoder.encode(plaintext));
    }

}
