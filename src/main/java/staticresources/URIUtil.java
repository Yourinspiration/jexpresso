package staticresources;

import java.io.IOException;
import java.nio.charset.Charset;

/* ------------------------------------------------------------ */

/**
 * URI utils.
 */
public class URIUtil implements Cloneable {
    public static final Charset __CHARSET = Charset.forName("UTF-8");

    private URIUtil() {
    }

    /**
     * Encode a URI path. This is the same encoding offered by URLEncoder,
     * except that the '/' character is not encoded.
     *
     * @param path The path the encode
     * @return The encoded path
     */
    public static String encodePath(final String path) {
        if (path == null || path.length() == 0)
            return path;

        StringBuilder buf = encodePath(null, path);
        return buf == null ? path : buf.toString();
    }

    /**
     * Encode a URI path.
     *
     * @param buf  StringBuilder to encode path into (or null)
     * @param path The path the encode
     * @return The StringBuilder or null if no substitutions required.
     */
    public static StringBuilder encodePath(StringBuilder buf, String path) {
        byte[] bytes = null;
        if (buf == null) {
            loop:
            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                switch (c) {
                    case '%':
                    case '?':
                    case ';':
                    case '#':
                    case '\'':
                    case '"':
                    case '<':
                    case '>':
                    case ' ':
                        buf = new StringBuilder(path.length() * 2);
                        break loop;
                    default:
                        if (c > 127) {
                            bytes = path.getBytes(URIUtil.__CHARSET);
                            buf = new StringBuilder(path.length() * 2);
                            break loop;
                        }

                }
            }
            if (buf == null)
                return null;
        }

        synchronized (buf) {
            if (bytes != null) {
                for (int i = 0; i < bytes.length; i++) {
                    byte c = bytes[i];
                    switch (c) {
                        case '%':
                            buf.append("%25");
                            continue;
                        case '?':
                            buf.append("%3F");
                            continue;
                        case ';':
                            buf.append("%3B");
                            continue;
                        case '#':
                            buf.append("%23");
                            continue;
                        case '"':
                            buf.append("%22");
                            continue;
                        case '\'':
                            buf.append("%27");
                            continue;
                        case '<':
                            buf.append("%3C");
                            continue;
                        case '>':
                            buf.append("%3E");
                            continue;
                        case ' ':
                            buf.append("%20");
                            continue;
                        default:
                            if (c < 0) {
                                buf.append('%');
                                toHex(c, buf);
                            } else
                                buf.append((char) c);
                            continue;
                    }
                }

            } else {
                for (int i = 0; i < path.length(); i++) {
                    char c = path.charAt(i);
                    switch (c) {
                        case '%':
                            buf.append("%25");
                            continue;
                        case '?':
                            buf.append("%3F");
                            continue;
                        case ';':
                            buf.append("%3B");
                            continue;
                        case '#':
                            buf.append("%23");
                            continue;
                        case '"':
                            buf.append("%22");
                            continue;
                        case '\'':
                            buf.append("%27");
                            continue;
                        case '<':
                            buf.append("%3C");
                            continue;
                        case '>':
                            buf.append("%3E");
                            continue;
                        case ' ':
                            buf.append("%20");
                            continue;
                        default:
                            buf.append(c);
                            continue;
                    }
                }
            }
        }

        return buf;
    }

    /**
     * Decode a URI path and strip parameters
     *
     * @param path The path the encode
     * @return returns the decoded path
     */
    public static String decodePath(String path) {
        if (path == null)
            return null;
        // Array to hold all converted characters
        char[] chars = null;
        int n = 0;
        // Array to hold a sequence of %encodings
        byte[] bytes = null;
        int b = 0;

        int len = path.length();

        for (int i = 0; i < len; i++) {
            char c = path.charAt(i);

            if (c == '%' && (i + 2) < len) {
                if (chars == null) {
                    chars = new char[len];
                    bytes = new byte[len];
                    path.getChars(0, i, chars, 0);
                }
                bytes[b++] = (byte) (0xff & parseInt(path, i + 1, 2, 16));
                i += 2;
                continue;
            } else if (c == ';') {
                if (chars == null) {
                    chars = new char[len];
                    path.getChars(0, i, chars, 0);
                    n = i;
                }
                break;
            } else if (bytes == null) {
                n++;
                continue;
            }

            // Do we have some bytes to convert?
            if (b > 0) {
                String s = new String(bytes, 0, b, __CHARSET);
                s.getChars(0, s.length(), chars, n);
                n += s.length();
                b = 0;
            }

            chars[n++] = c;
        }

        if (chars == null)
            return path;

        // if we have a remaining sequence of bytes
        if (b > 0) {
            String s = new String(bytes, 0, b, __CHARSET);
            s.getChars(0, s.length(), chars, n);
            n += s.length();
        }

        return new String(chars, 0, n);
    }

    /**
     * Parse an integer from a substring. Negative numbers are not handled.
     *
     * @param s      String
     * @param offset Offset within string
     * @param length Length of integer or -1 for remainder of string
     * @param base   base of the integer
     * @return the parsed integer
     * @throws NumberFormatException if the string cannot be parsed
     */
    public static int parseInt(String s, int offset, int length, int base) throws NumberFormatException {
        int value = 0;

        if (length < 0)
            length = s.length() - offset;

        for (int i = 0; i < length; i++) {
            char c = s.charAt(offset + i);

            int digit = convertHexDigit(c);
            if (digit < 0 || digit >= base)
                throw new NumberFormatException(s.substring(offset, offset + length));
            value = value * base + digit;
        }
        return value;
    }

    /**
     * Converts hex to digit.
     *
     * @param c An ASCII encoded character 0-9 a-f A-F
     * @return The byte value of the character 0-16.
     */
    public static byte convertHexDigit(byte c) {
        byte b = (byte) ((c & 0x1f) + ((c >> 6) * 0x19) - 0x10);
        if (b < 0 || b > 15)
            throw new IllegalArgumentException("!hex " + c);
        return b;
    }

    /**
     * Converts hex to digit.
     *
     * @param c An ASCII encoded character 0-9 a-f A-F
     * @return The byte value of the character 0-16.
     */
    public static int convertHexDigit(int c) {
        int d = ((c & 0x1f) + ((c >> 6) * 0x19) - 0x10);
        if (d < 0 || d > 15)
            throw new NumberFormatException("!hex " + c);
        return d;
    }

    /**
     * Converts to hex.
     *
     * @param b   the byte to be converted
     * @param buf the buffer to be appended
     */
    public static void toHex(byte b, Appendable buf) {
        try {
            int d = 0xf & ((0xF0 & b) >> 4);
            buf.append((char) ((d > 9 ? ('A' - 10) : '0') + d));
            d = 0xf & b;
            buf.append((char) ((d > 9 ? ('A' - 10) : '0') + d));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
