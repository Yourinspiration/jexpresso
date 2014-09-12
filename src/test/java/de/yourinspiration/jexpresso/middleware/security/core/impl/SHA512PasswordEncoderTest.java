package de.yourinspiration.jexpresso.middleware.security.core.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test case for {@link SHA512PasswordEncoder}.
 *
 * @author Marcel Härle
 */
public class SHA512PasswordEncoderTest {

    private SHA512PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        passwordEncoder = new SHA512PasswordEncoder();
    }

    @Test
    public void testCheckpw() {
        final String plainText = "test";
        final String encoded = "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff";
        assertTrue(passwordEncoder.checkpw(plainText, encoded));
    }

    @Test
    public void testEncode() {
        final String plainText = "test";
        final String expected = "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff";
        assertEquals(expected, passwordEncoder.encode(plainText));
    }

}
