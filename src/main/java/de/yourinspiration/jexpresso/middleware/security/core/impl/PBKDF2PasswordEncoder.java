package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.PasswordEncoder;
import de.yourinspiration.jexpresso.middleware.security.core.algorithm.PBKDF2;

/**
 * Password encoder for PBKDF2 algorithm.
 *
 * @author Michael Malcharek
 */
public class PBKDF2PasswordEncoder implements PasswordEncoder {
    @Override
    public boolean checkpw(final String plaintext, final String encoded) {
        return PBKDF2.checkpw(plaintext, encoded);
    }

    @Override
    public String encode(String plaintext) {
        return PBKDF2.hashpw(plaintext, PBKDF2.gensalt());
    }
}
