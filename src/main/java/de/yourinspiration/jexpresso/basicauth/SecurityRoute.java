package de.yourinspiration.jexpresso.basicauth;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Arrays;

/**
 * This class holds the data for secured routes.
 *
 * @author Marcel Härle
 */
public class SecurityRoute {

    private final String path;
    private final String authorities;
    private final HttpMethod[] methods;
    private final String pathPattern;

    /**
     * Constructs a new security route object.
     *
     * @param path        the path to be secured
     * @param authorities the authorities
     * @param methods     the HTTP methods to secure
     */
    protected SecurityRoute(final String path, final String authorities, final HttpMethod[] methods) {
        this.path = path;
        this.authorities = authorities;

        // No methods mean that all methods should be secured.
        if (methods.length == 0) {
            this.methods = new HttpMethod[]{HttpMethod.GET, HttpMethod.CONNECT, HttpMethod.DELETE, HttpMethod.HEAD,
                    HttpMethod.OPTIONS, HttpMethod.POST, HttpMethod.PUT, HttpMethod.TRACE};
        } else {
            this.methods = methods;
        }

        Arrays.sort(this.methods);

        final String adjustedPath = this.path.replaceAll("\\*", ".*");
        this.pathPattern = "^" + adjustedPath;

    }

    /**
     * Get the secured path.
     *
     * @return the secured path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the authorities
     *
     * @return the authorities
     */
    public String getAuthorities() {
        return authorities;
    }

    /**
     * Get the HTTP methods to be secured.
     *
     * @return the secured HTTP methods
     */
    public HttpMethod[] getMethods() {
        return methods;
    }

    /**
     * Check if the provided path and HTTP method match the secured path and
     * methods.
     *
     * @param pathToMatch the path of the current route
     * @param method      the HTTP emthod of the current route
     * @return returns <code>true</code> if the path and method matched
     */
    public boolean matchesPathAndMethod(String pathToMatch, HttpMethod method) {
        return Arrays.binarySearch(this.methods, method) > -1 && pathToMatch.matches(pathPattern);
    }

}
