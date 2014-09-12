package de.yourinspiration.jexpresso.middleware.security.core.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link PBKDF2PasswordEncoder}.
 *
 * @author Marcel Härle
 */
public class PBKDF2PasswordEncoderTest {

    private PBKDF2PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        passwordEncoder = new PBKDF2PasswordEncoder();
    }

    @Test
    public void testEncodeAndCheckpw() {
        final String plaintext = "test";
        final String encoded = passwordEncoder.encode(plaintext);
        assertTrue(passwordEncoder.checkpw(plaintext, encoded));
    }

}
