package de.yourinspiration.jexpresso.core;

import io.netty.handler.codec.http.HttpMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates the inforation about route mappings.
 *
 * @author Marcel Härle
 */
public class Route {

    private final String path;
    private final HttpMethod method;
    private final Pattern pathPattern;
    private final RouteHandler routeHandler;

    protected Route(final String path, final HttpMethod method, final RouteHandler routeHandler) {
        this.path = path;
        this.method = method;
        final String adjustedPath = path.replaceAll("\\*\\*", ".*").replaceAll(":\\w*", ".*");
        this.pathPattern = Pattern.compile(adjustedPath);
        this.routeHandler = routeHandler;
    }

    public boolean matchesPathAndMethod(final String pathToMatch, final HttpMethod method) {
        final Matcher m = pathPattern.matcher(pathToMatch);
        return path.split("/").length == pathToMatch.split("/").length && method.equals(this.method) && m.matches();
    }

    public void handle(final Request request, final Response response) {
        this.routeHandler.handle(request, response);
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "[path=" + path + ",method=" + method + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + path.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Route other = (Route) obj;
        if (method != other.method)
            return false;
        return path.equals(other.path);
    }

}
