package de.yourinspiration.jexpresso;

/**
 * A {@link ResponseListener} is called after the response has been created.
 * 
 * @author Marcel Härle
 *
 */
public interface ResponseListener {

    /**
     * Callback handler.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     */
    void callback(Request request, Response response);

}
