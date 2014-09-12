package de.yourinspiration.jexpresso.middleware.security.core.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link PasswordEncoders}.
 *
 * @author Marcel Härle
 */
public class PasswordEncodersTest {

    @Test
    public void testMd5() {
        assertEquals(MD5PasswordEncoder.class, PasswordEncoders.md5().getClass());
    }

    @Test
    public void testBcrypt() {
        assertEquals(BCryptPasswordEncoder.class, PasswordEncoders.bcrypt().getClass());
    }

    @Test
    public void testPbkdf2() {
        assertEquals(PBKDF2PasswordEncoder.class, PasswordEncoders.pbkdf2().getClass());
    }

    @Test
    public void testPlaintext() {
        assertEquals(PlainTextPasswordEncoder.class, PasswordEncoders.plaintext().getClass());
    }

    @Test
    public void testSha256() {
        assertEquals(SHA256PasswordEncoder.class, PasswordEncoders.sha256().getClass());
    }

    @Test
    public void testSha512() {
        assertEquals(SHA512PasswordEncoder.class, PasswordEncoders.sha512().getClass());
    }

}
