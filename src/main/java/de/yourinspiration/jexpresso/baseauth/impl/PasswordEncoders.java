package de.yourinspiration.jexpresso.baseauth.impl;

import de.yourinspiration.jexpresso.baseauth.PasswordEncoder;

/**
 * Simple factory to get password encoder instances.
 *
 * @author Marcel Härle
 */
public final class PasswordEncoders {

    private static final MD5PasswordEncoder md5PasswordEncoder = new MD5PasswordEncoder();
    private static final BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
    private static final PBKDF2PasswordEncoder pbkdf2PasswordEncoder = new PBKDF2PasswordEncoder();
    private static final PlainTextPasswordEncoder plaintextPasswordEncoder = new PlainTextPasswordEncoder();
    private static final SHA256PasswordEncoder sha256PasswordEncoder = new SHA256PasswordEncoder();
    private static final SHA512PasswordEncoder sha512PasswordEncoder = new SHA512PasswordEncoder();

    /**
     * Get an instance for the MD5 algorithm.
     *
     * @return a MD5 password encoder
     */
    public static PasswordEncoder md5() {
        return md5PasswordEncoder;
    }

    /**
     * Get an instance for the BCrypt algorithm.
     *
     * @return a BCrypt password encoder
     */
    public static PasswordEncoder bcrypt() {
        return bcryptPasswordEncoder;
    }

    /**
     * Get an instance for the PBKDF2 algorithm.
     *
     * @return a PBKDF2 password encoder
     */
    public static PasswordEncoder pbkdf2() {
        return pbkdf2PasswordEncoder;
    }

    /**
     * Get an instance for plain text.
     *
     * @return a plain text password encoder
     */
    public static PasswordEncoder plaintext() {
        return plaintextPasswordEncoder;
    }

    /**
     * Get an instance for the SHA-256 algorithm.
     *
     * @return a SHA-256 password encoder
     */
    public static PasswordEncoder sha256() {
        return sha256PasswordEncoder;
    }

    /**
     * Get an instance for the SHA-512 algorithm.
     *
     * @return a SHA-512 password encoder
     */
    public static PasswordEncoder sha512() {
        return sha512PasswordEncoder;
    }

}
