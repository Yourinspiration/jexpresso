package de.yourinspiration.jexpresso;


/**
 * Callback handler for HTTP requests.
 *
 * @author Marcel Härle
 */
public interface RouteHandler {

    /**
     * Handles the HTTP request.
     *
     * @param request  the request
     * @param response the response
     */
    void handle(Request request, Response response);

}
