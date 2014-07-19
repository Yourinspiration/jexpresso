package de.yourinspiration.jexpresso;


/**
 * Callback handler for middleware.
 * 
 * @author Marcel Härle
 *
 */
public interface MiddlewareHandler {

    /**
     * Handles the request and response.
     * 
     * @param request
     *            the current request
     * @param response
     *            the current response
     */
    void handle(Request request, Response response);

}
