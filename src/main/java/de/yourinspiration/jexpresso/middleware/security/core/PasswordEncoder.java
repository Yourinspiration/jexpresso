package de.yourinspiration.jexpresso.middleware.security.core;

/**
 * Compares plain text with encrypted passwords.
 *
 * @author Marcel Härle
 */
public interface PasswordEncoder {

    /**
     * Checks if the plain text password matches the encoded password.
     *
     * @param plaintext the plain text password
     * @param encoded   the encoded password
     * @return returns <code>true</code> if the passwords matches, otherwise
     * <code>false</code>
     */
    boolean checkpw(String plaintext, String encoded);

    /**
     * Returns a hashed or encoded string of the passed plain text.
     *
     * @param plaintext the plain text
     * @return returns a hashed or encoded representation of the plain text.
     */
    String encode(String plaintext);
}
