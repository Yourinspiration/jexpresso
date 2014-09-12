package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.PasswordEncoder;
import org.pmw.tinylog.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password encoder for MD5 algorithm.
 *
 * @author Marcel Härle
 */
public class MD5PasswordEncoder implements PasswordEncoder {

    private final String MD5_ALGORITHM = "MD5";

    @Override
    public boolean checkpw(final String plaintext, final String encoded) {
        try {
            final MessageDigest md5Digest = MessageDigest.getInstance(MD5_ALGORITHM);
            final byte[] bytes = md5Digest.digest(plaintext.getBytes());

            final String encodedPlainText = SecurityUtils.toHex(bytes);

            return encodedPlainText.equals(encoded);
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Error creating md5 message digest");
            return false;
        }
    }

    @Override
    public String encode(String plaintext) {
        try {
            final MessageDigest md5Digest = MessageDigest.getInstance(MD5_ALGORITHM);
            final byte[] bytes = md5Digest.digest(plaintext.getBytes());
            return SecurityUtils.toHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            Logger.error("Error creating md5 message digest");
            return null;
        }
    }

}
