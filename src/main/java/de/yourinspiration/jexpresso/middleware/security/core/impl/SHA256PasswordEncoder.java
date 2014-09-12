package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.PasswordEncoder;
import org.pmw.tinylog.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password encoder for SHA-256 algorithm.
 *
 * @author Marcel Härle
 */
public class SHA256PasswordEncoder implements PasswordEncoder {

    private final String SHA256_ALGORITHM = "SHA-256";

    @Override
    public boolean checkpw(final String plaintext, final String encoded) {
        try {
            final MessageDigest sha256Digest = MessageDigest.getInstance(SHA256_ALGORITHM);
            final byte[] bytes = sha256Digest.digest(plaintext.getBytes());

            final String encodedPlainText = SecurityUtils.toHex(bytes);

            return encodedPlainText.equals(encoded);
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Error creating sha256 message digest");
            return false;
        }
    }

    @Override
    public String encode(String plaintext) {
        try {
            final MessageDigest sha256Digest = MessageDigest.getInstance(SHA256_ALGORITHM);
            final byte[] bytes = sha256Digest.digest(plaintext.getBytes());
            return SecurityUtils.toHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            Logger.error("Error creating sha256 message digest");
            return null;
        }
    }

}
