package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.staticresources.StaticResources;
import io.netty.handler.codec.http.HttpMethod;

/**
 * Provides the API for a JExpresso web application.
 *
 * @author Marcel Härle
 */
public class JExpresso {

    private final JExpressoBase base;

    /**
     * Create a new JExpresso application instance.
     */
    public JExpresso() {
        base = new JExpressoBase();
    }

    protected JExpresso(final JExpressoBase base) {
        this.base = base;
    }

    /**
     * Register the given callback handler for a GET request on the given path.
     *
     * @param path    the path
     * @param handler the callback handler
     */
    public void get(final String path, final RouteHandler handler) {
        base.addRoute(new Route(path, HttpMethod.GET, handler));
    }

    /**
     * Register the given callback handler for a POST request on the given path.
     *
     * @param path    the path
     * @param handler the callback handler
     */
    public void post(final String path, final RouteHandler handler) {
        base.addRoute(new Route(path, HttpMethod.POST, handler));
    }

    /**
     * Register the given callback handler for a HEAD request on the given path.
     *
     * @param path    the path
     * @param handler the callback handler
     */
    public void head(final String path, final RouteHandler handler) {
        base.addRoute(new Route(path, HttpMethod.HEAD, handler));
    }

    /**
     * Register the given callback handler for a PUT request on the given path.
     *
     * @param path    the path
     * @param handler the callback handler
     */
    public void put(final String path, final RouteHandler handler) {
        base.addRoute(new Route(path, HttpMethod.PUT, handler));
    }

    /**
     * Register the given callback handler for a OPTIONS request on the given
     * path.
     *
     * @param path    the path
     * @param handler the callback handler
     */
    public void options(final String path, final RouteHandler handler) {
        base.addRoute(new Route(path, HttpMethod.OPTIONS, handler));
    }

    /**
     * Register the given callback handler for a DELETE request on the given
     * path.
     *
     * @param path    the path
     * @param handler the callback handler
     */
    public void delete(final String path, final RouteHandler handler) {
        base.addRoute(new Route(path, HttpMethod.DELETE, handler));
    }

    /**
     * Register the given callback handler for a TRACE request on the given
     * path.
     *
     * @param path    the path
     * @param handler the callback handler
     */
    public void trace(final String path, final RouteHandler handler) {
        base.addRoute(new Route(path, HttpMethod.TRACE, handler));
    }

    /**
     * Register the given callback handler for a CONNECT request on the given
     * path.
     *
     * @param path    the path
     * @param handler the callback handler
     */
    public void connect(final String path, final RouteHandler handler) {
        base.addRoute(new Route(path, HttpMethod.CONNECT, handler));
    }

    /**
     * Register the template engine for the given file extension.
     *
     * @param ext            the file extension
     * @param templateEngine the template engine
     */
    public void engine(final String ext, final TemplateEngine templateEngine) {
        base.addTemplateEngine(ext, templateEngine);
    }

    /**
     * Register a custom exception handler, that is called when the given class
     * of exception is thrown during the request processing.
     *
     * @param exceptionClass the class of the exception
     * @param routeHandler   the callback handler
     */
    public void exception(final Class<? extends Exception> exceptionClass, final RouteHandler routeHandler) {
        base.addExceptionHandler(exceptionClass, routeHandler);
    }

    /**
     * Register the given middleware handler.
     *
     * @param handler the callback handler
     */
    public void use(final MiddlewareHandler handler) {
        base.addMiddleware(handler);
    }

    /**
     * Enable the feature for the given name. The middleware will have access to
     * this setting.
     *
     * @param name the name of the feature
     */
    public void enable(final String name) {
        throw new RuntimeException("not implemented yet");
    }

    /**
     * Starts the server on the given port.
     *
     * @param port the port
     */
    public void listen(final int port) {
        base.startNetty(port);
    }

    /**
     * Starts the server on the given port and executes the callback when the
     * server is started.
     *
     * @param port            the port
     * @param starterCallback the callback handler
     */
    public void listen(final int port, final StarterCallback starterCallback) {
        base.startNetty(port);
        starterCallback.handle();
    }

    /**
     * Serve static resources from the given folder.
     *
     * @param folder   the folder for static resources
     * @param useCache whether to use a cache
     */
    public void staticResources(final String folder, final boolean useCache) {
        base.addMiddleware(new StaticResources(folder, useCache));
    }

}
