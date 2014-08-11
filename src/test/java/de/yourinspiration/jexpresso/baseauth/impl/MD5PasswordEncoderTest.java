package de.yourinspiration.jexpresso.baseauth.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link MD5PasswordEncoder}.
 *
 * @author Marcel Härle
 */
public class MD5PasswordEncoderTest {

    private MD5PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        passwordEncoder = new MD5PasswordEncoder();
    }

    @Test
    public void testEncodeAndCheck() {
        final String plaintext = "test";
        final String encoded = passwordEncoder.encode(plaintext);
        assertTrue(passwordEncoder.checkpw(plaintext, encoded));
    }

}
