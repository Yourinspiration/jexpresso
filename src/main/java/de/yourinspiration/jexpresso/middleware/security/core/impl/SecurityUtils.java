package de.yourinspiration.jexpresso.middleware.security.core.impl;

/**
 * Helper class for security issues.
 *
 * @author Marcel Härle
 */
public final class SecurityUtils {

    private SecurityUtils() {

    }

    /**
     * Converts the given text to a hex encoded string.
     *
     * @param text the text to be converted
     * @return the hex encoded string
     */
    public static String toHex(final byte[] text) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length; i++) {
            sb.append(Integer.toString((text[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

}
