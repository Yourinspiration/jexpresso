package de.yourinspiration.jexpresso.http;

/**
 * Constants for common HTTP content types.
 * 
 * @author Marcel Härle
 *
 */
public enum ContentType {

    /**
     * {@code application/json}
     */
    APPLICATION_JSON("application/json"),
    /**
     * {@code application/xml}
     */
    APPLICATION_XML("application/xml"),
    /**
     * {@code text/plain}
     */
    TEXT_PLAIN("text/plain"),
    /**
     * {@code text/html}
     */
    TEXT_HTML("text/html"),
    /**
     * {@code application/octet-stream}
     */
    APPLICATION_OCTETSTREAM("application/octet-stream");

    private String type;

    private ContentType(final String type) {
        this.type = type;
    }

    /**
     * Get the text representation of the content type.
     * 
     * @return the text representation of the content type
     */
    public String type() {
        return type;
    }

    /**
     * Return a string representation of this content type.
     */
    @Override
    public String toString() {
        return type;
    }

}
