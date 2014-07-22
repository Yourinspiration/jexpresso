package de.yourinspiration.jexpresso;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * Represents a HTTP request.
 * 
 * @author Marcel Härle
 *
 */
public interface Request {

    /**
     * Returns the request body as a parsed string.
     * 
     * @return the request body
     */
    String body();

    /**
     * Returns the binary data of the request body.
     * 
     * @return the request body
     */
    byte[] bytes();

    /**
     * Returns the request body as parsed JSON.
     * 
     * @param clazz
     *            the class of the parsed content
     * @return hte request body
     */
    <T> T json(Class<T> clazz);

    /**
     * Returns a map containing properties mapped to the named route
     * "parameters". For example if you have the route /user/:name, then the
     * "name" property is available to you as req.params().get("name").
     * 
     * @return returns an empty map if no parameters were found
     */
    Map<String, String> params();

    /**
     * Returns a map containing the parsed query-string,
     * 
     * @return returns an empty map if no query-strings were found
     */
    Map<String, String> query();

    /**
     * Returns the query for the given name.
     * 
     * @param name
     *            the name
     * @return returns null if no such query exists
     */
    String query(String name);

    /**
     * Return the value of param name when present.
     * 
     * @param name
     *            the name of the param
     * @return returns null if no such param exists
     */
    String param(String name);

    /**
     * Returns the cookies sent by the user-agent.
     * 
     * @return retusn an empty list of no cookies were sent
     */
    List<Cookie> cookies();

    /**
     * Returns the cookie for the given name sent by the user-agent.
     * 
     * @param name
     *            the name
     * @return returns null if no such cookie was sent
     */
    Cookie cookie(String name);

    /**
     * Get the case-insensitive request header field. The Referrer and Referer
     * fields are interchangeable.
     * 
     * @param field
     *            the name of the request header field
     * @return returns null if no such field exists
     */
    String get(String field);

    /**
     * Check if the given types are acceptable, returning the best match when
     * true, otherwise undefined - in which case you should respond with 406
     * "Not Acceptable".
     * 
     * The type value may be a single mime type string such as
     * "application/json", the extension name such as "json", a comma-delimited
     * list or an array. When a list or array is given the best match, if any is
     * returned.
     * 
     * @param types
     *            the types to be checked
     * @return returns null if no type is acceptable
     */
    String accepts(String... types);

    /**
     * Check if the given charset are acceptable.
     * 
     * @param charsets
     *            the charsets to be checked
     * @return returns null if no charset is acceptable
     */
    String acceptsCharset(String... charsets);

    /**
     * Check if the given lang are acceptable.
     * 
     * @param lang
     *            the languages to be checked
     * @return returns null if no language is acceptable
     */
    String acceptsLanguage(String... lang);

    /**
     * Check if the incoming request contains the "Content-Type" header field,
     * and it matches the give mime type.
     * 
     * @param type
     *            the mime type
     * @return returns <code>true</code> when the mime type matches, otherwise
     *         <code>false</code>
     */
    boolean is(String type);

    /**
     * Return the remote address.
     * 
     * @return the remote address
     */
    String ip();

    /**
     * Returns the request URL pathname.
     * 
     * @return the request URL pathname
     */
    String path();

    /**
     * Returns the hostname from the "Host" header field (void of portno).
     * 
     * @return the hostname
     */
    String host();

    /**
     * Check if the request is fresh - aka Last-Modified and/or the ETag still
     * match, indicating that the resource is "fresh".
     * 
     * @return returns <code>true</code> when the request is fresh, otherwise
     *         <code>false</code>
     */
    boolean fresh();

    /**
     * Check if the request is stale - aka Last-Modified and/or the ETag do not
     * match, indicating that the resource is "stale".
     * 
     * @return returns <code>true</code> when the request is stale, otherwise
     *         <code>false</code>
     */
    boolean stale();

    /**
     * Check if the request was issued with the "X-Requested-With" header field
     * set to "XMLHttpRequest" (jQuery etc).
     * 
     * @return returns <code>true</code> when xhr is enabled, otherwise
     *         </code>false</code>
     */
    boolean xhr();

    /**
     * Return the protocol string "http" or "https" when requested with TLS.
     * 
     * @return the protocol
     */
    String protocol();

    /**
     * Check if a TLS connection is established.
     * 
     * @return returns <code>true</code> when a TLS connection is established,
     *         otherwise <code>false</code>
     */
    boolean secure();

    /**
     * Get a request attribute.
     * 
     * @param name
     *            name of the attribute
     * @return returns <code>null</code> if no attribute with the given name
     *         exists.
     */
    Object attribute(final String name);

    /**
     * Set a request attribute.
     * 
     * @param name
     *            the name of the attribute
     * @param attr
     *            the value of the attribute
     */
    void attribute(final String name, Object attr);

    /**
     * Get the HTTP method.
     * 
     * @return the HTTP method
     */
    HttpMethod method();

}
