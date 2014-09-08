package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.http.ContentType;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a HTTP request.
 *
 * @author Marcel Härle
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
     * @param <T>   the type of the object
     * @param clazz the class of the parsed content
     * @return the request body as parsed JSON
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
     * @param name the name
     * @return returns the query for the given name
     */
    Optional<String> query(String name);

    /**
     * Return the value of param name when present.
     *
     * @param name the name of the param
     * @return returns null if no such param exists
     */
    Optional<String> param(String name);

    /**
     * Returns a map containing the parsed parameters of a post request.
     * @return returns an empty map if no parameters where found
     */
    Map<String, String> postParams();

    /**
     * Returns the parameter of a post request for the given name.
     * @param name the name of the parameter
     * @return returns the parameter of a post request for the given name
     */
    Optional<String> postParam(String name);

    /**
     * Returns the cookies sent by the user-agent.
     *
     * @return retusn an empty list of no cookies were sent
     */
    List<Cookie> cookies();

    /**
     * Returns the cookie for the given name sent by the user-agent.
     *
     * @param name the name
     * @return returns the cookie for the given name sent by the user-agent
     */
    Optional<Cookie> cookie(String name);

    /**
     * Set the given cookie. Will overwrite an existing cookie.
     *
     * @param cookie the cookie.
     */
    void setCookie(Cookie cookie);

    /**
     * Get the case-insensitive request header field. The Referrer and Referer
     * fields are interchangeable.
     *
     * @param field the name of the request header field
     * @return returns the request header field value
     */
    Optional<String> get(String field);

    /**
     * Check if the given types are acceptable, returning the best match when
     * true, otherwise with an empty optional - in which case you should respond with
     * 406 "Not Acceptable".
     *
     * @param types the types to be checked
     * @return returns an empty optional if no type is acceptable
     */
    Optional<String> accepts(String... types);

    /**
     * Check if the given charsets are acceptable.
     *
     * @param charsets the charsets to be checked
     * @return returns an empty optional if no charset is acceptable
     */
    Optional<String> acceptsCharset(String... charsets);

    /**
     * Check if the given lang are acceptable.
     *
     * @param lang the languages to be checked
     * @return returns an empty optional if no language is acceptable
     */
    Optional<String> acceptsLanguage(String... lang);

    /**
     * Check if the incoming request contains the "Content-Type" header field,
     * and it matches the give mime type.
     *
     * @param type the mime type
     * @return returns <code>true</code> when the mime type matches, otherwise
     * <code>false</code>
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
     * Returns the hostname from the "Host" header field.
     *
     * @return the hostname
     */
    String host();

    /**
     * Check if the request was issued with the "X-Requested-With" header field
     * set to "XMLHttpRequest" (jQuery etc).
     *
     * @return returns <code>true</code> when xhr is enabled, otherwise
     * <code>false</code>
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
     * otherwise <code>false</code>
     */
    boolean secure();

    /**
     * Get a request attribute.
     *
     * @param name           name of the attribute
     * @param attributeClass the class of the attribute
     * @return returns the request attribute for the given name and class
     */
    <T> Optional<T> attribute(final String name, final Class<T> attributeClass);

    /**
     * Set a request attribute.
     *
     * @param name the name of the attribute
     * @param attr the value of the attribute
     */
    void attribute(final String name, Object attr);

    /**
     * Get the HTTP method.
     *
     * @return the HTTP method
     */
    HttpMethod method();

}
