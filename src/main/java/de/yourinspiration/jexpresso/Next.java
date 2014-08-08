package de.yourinspiration.jexpresso;

/**
 * Provides methods to call the next middleware handler or cancel the middleware
 * chain, in order to responde immediately to the client.
 *
 * @author Marcel Härle
 */
public interface Next {

    /**
     * Call the next middleware handler.
     */
    void next();

    /**
     * Cancel the middleware chain and response immediately to the client.
     */
    void cancel();

}
