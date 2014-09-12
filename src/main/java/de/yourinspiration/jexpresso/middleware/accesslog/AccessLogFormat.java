package de.yourinspiration.jexpresso.middleware.accesslog;

/**
 * Format for the access log message.
 *
 * @author Marcel Härle
 */
public enum AccessLogFormat {

    /**
     * Tiny format: <code>12.10.14 2:33:21: GET /path</code>
     */
    TINY,

    /**
     * Small format: <code>12.10.14 2:33:21: GET /path 166.156.23.21 200</code>
     */
    SMALL,

    /**
     * Detailed format:
     * <code>12.10.14 2:33:21: GET /path 194.123.22.34 200 OK 120ms</code>
     */
    DEVELOPER

}
