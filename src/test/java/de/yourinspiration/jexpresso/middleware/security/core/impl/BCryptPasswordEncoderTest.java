package de.yourinspiration.jexpresso.middleware.security.core.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link BCryptPasswordEncoder}.
 *
 * @author Marcel Härle
 */
public class BCryptPasswordEncoderTest {

    private BCryptPasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    public void testEncodeAndCheck() {
        final String plaintext = "test";
        final String encoded = passwordEncoder.encode(plaintext);
        assertTrue(passwordEncoder.checkpw(plaintext, encoded));
    }

}
