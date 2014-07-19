package de.yourinspiration.jexpresso.exception;

import de.yourinspiration.jexpresso.Request;
import de.yourinspiration.jexpresso.Response;
import de.yourinspiration.jexpresso.RouteHandler;

/**
 * Aggregates the information for exception callback handlers.
 * 
 * @author Marcel Härle
 *
 */
public class ExceptionHandlerEntry {

    private final Class<? extends Exception> exceptionClass;
    private final RouteHandler routeHandler;

    /**
     * Constructs a new object.
     * 
     * @param exceptionClass
     *            the exception class to be handled
     * @param routeHandler
     *            the callback handler
     */
    public ExceptionHandlerEntry(final Class<? extends Exception> exceptionClass, final RouteHandler routeHandler) {
        this.exceptionClass = exceptionClass;
        this.routeHandler = routeHandler;
    }

    /**
     * Checks if the exception is the of the same class.
     * 
     * @param e
     *            the exception to be checked
     * @return returns <code>true</code> if the excpetion class is the same
     */
    public boolean isInstanceOf(final Exception e) {
        return e.getClass().equals(exceptionClass);
    }

    /**
     * Invokes the callback handler.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     * @return the result of the callback handler
     */
    public void invokeHandler(final Request request, final Response response) {
        routeHandler.handle(request, response);
    }

    @Override
    public String toString() {
        return "[exceptionClass=" + exceptionClass.getName() + "]";
    }

}
