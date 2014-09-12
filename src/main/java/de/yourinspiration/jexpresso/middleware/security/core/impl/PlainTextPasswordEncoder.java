package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.PasswordEncoder;

/**
 * A very simple password encoder that compares plain text strings without any
 * encryption.
 *
 * @author Marcel Härle
 */
public class PlainTextPasswordEncoder implements PasswordEncoder {

    @Override
    public boolean checkpw(final String plaintext, final String encoded) {
        return plaintext.equals(encoded);
    }

    @Override
    public String encode(String plaintext) {
        return plaintext;
    }

}
