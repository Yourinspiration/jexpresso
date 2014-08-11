package de.yourinspiration.jexpresso.baseauth.algorithm;

import org.pmw.tinylog.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Implementation of PBKDF2.
 *
 * @author Michael Malcharek
 */
public class PBKDF2 {
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 64 * 8;
    private static final String ALGORITHM_PBKDF2 = "PBKDF2WithHmacSHA1";
    private static final String ALGORITHM_SALT = "SHA1PRNG";
    private static final String DELIMITER = ":";
    private static final int ITERATIONS = 32000;

    /**
     * Check the plain text password against the encoded password.
     *
     * @param plainText   the plain text password
     * @param encodedText the encoded password
     * @return returns <code>true</code> when the passwords match
     */
    public static boolean checkpw(final String plainText, final String encodedText) {
        final String[] splitted = encodedText.split(DELIMITER);
        if (splitted.length != 2)
            return false;

        final String salt = splitted[0];

        final String encodedPlainText = hashpw(plainText, salt);
        return encodedText.equals(encodedPlainText);
    }

    /**
     * Generate the password hash for the given plain text password.
     *
     * @param plainText  the plain text password
     * @param base64salt the salt
     * @return returns the password hash
     */
    public static String hashpw(final String plainText, final String base64salt) {
        final char[] chars = plainText.toCharArray();
        final byte[] salt = Base64.getDecoder().decode(base64salt);

        try {
            final PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEY_LENGTH);
            final SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM_PBKDF2);
            final byte[] encoded = skf.generateSecret(spec).getEncoded();

            return new Result(salt, encoded).toString();
        } catch (InvalidKeySpecException ex) {
            Logger.error("Error creating key for " + ALGORITHM_PBKDF2);
            return null;
        } catch (NoSuchAlgorithmException ex) {
            Logger.error("Error creating " + ALGORITHM_PBKDF2);
            return null;
        }
    }

    /**
     * Generate a random salt.
     *
     * @return returns the salt string
     */
    public static String gensalt() {
        try {
            final SecureRandom sr = SecureRandom.getInstance(ALGORITHM_SALT);
            final byte[] salt = new byte[SALT_LENGTH];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException ex) {
            Logger.error("Error creating " + ALGORITHM_SALT);
            return null;
        }
    }

    private static final class Result {
        private final String _salt;
        private final String _content;

        public Result(final byte[] salt, final byte[] content) {
            _salt = Base64.getEncoder().encodeToString(salt);
            _content = Base64.getEncoder().encodeToString(content);
        }

        @Override
        public String toString() {
            return _salt + DELIMITER + _content;
        }
    }
}
