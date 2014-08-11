package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.http.HttpStatus;
import io.netty.handler.codec.http.Cookie;

import java.util.Map;

/**
 * Represents a HTTP response.
 *
 * @author Marcel Härle
 */
public interface Response {

    /**
     * Sets the HTTP status code.
     *
     * @param status the HTTP status code
     */
    void status(HttpStatus status);

    /**
     * Get the HTTP status code.
     *
     * @return the HTTP status code
     */
    HttpStatus status();

    /**
     * Set header field to value.
     *
     * @param field the header field
     * @param value the value
     */
    void set(String field, String value);

    /**
     * Set header fields.
     *
     * @param fields the header fields
     */
    void set(Map<String, String> fields);

    /**
     * Get the case-insensitive response header field.
     *
     * @param field the header field
     * @return returns null if no such header field exists
     */
    String get(String field);

    /**
     * Set the given cookie.
     *
     * @param cookie the cookie to be set
     */
    void cookie(Cookie cookie);

    /**
     * Clear the cookie with the given name.
     *
     * @param name the cookie's name
     */
    void clearCookie(String name);

    /**
     * Redirect to the given url.
     *
     * @param url the url
     */
    void redirect(String url);

    /**
     * Set the location header.
     *
     * @param location the location
     */
    void location(String location);

    /**
     * Send a response with the given content. The Content-Type will
     * automatically be detected. If the content is a string the Content-Type
     * will be "text/html", if the content is a object, the Content-Type will be
     * "application/json" and the object will be rendered to JSON.
     *
     * @param content the content to be sent
     */
    void send(Object content);

    /**
     * Send a response with the given status code and content. The Content-Type
     * will automatically be detected. If the content is a string the
     * Content-Type will be "text/html", if the content is a object, the
     * Content-Type will be "application/json" and the object will be rendered
     * to JSON.
     *
     * @param status  the status code
     * @param content the content to be sent
     */
    void send(HttpStatus status, Object content);

    /**
     * Send binary content. The Content-Type is set to
     * "application/octet-stream"
     *
     * @param content the content to be sent
     */
    void send(byte[] content);

    /**
     * Send binary content with the given status code. The Content-Type is set
     * to "application/octet-stream"
     *
     * @param status  the status code
     * @param content the content to be sent
     */
    void send(HttpStatus status, byte[] content);

    /**
     * Send the given status code. The response body is assigned for you
     * depending of the status code. For example 200 will respond will the text
     * "OK", and 404 "Not Found" and so on.
     *
     * @param status the status code
     */
    void send(HttpStatus status);

    /**
     * Send explicit JSON content.
     *
     * @param content the content to be sent
     */
    void json(Object content);

    /**
     * Send explicit JSON content with the given status code.
     *
     * @param status  the status code
     * @param content the content to be sent
     */
    void json(HttpStatus status, Object content);

    /**
     * Send a JSON response with JSONP support. This method is identical to
     * json(Object content) however opts-in to JSONP callback support. By
     * default the JSONP callback name is simply callback, however you may alter
     * this with the jsonp callback name setting.
     *
     * @param content the content to be sent
     */
    void jsonp(Object content);

    /**
     * Send a JSON response with JSONP support and given status code. This
     * method is identical to json(int status, Object content) however opts-in
     * to JSONP callback support. By default the JSONP callback name is simply
     * callback, however you may alter this with the jsonp callback name
     * setting.
     *
     * @param status  the status code
     * @param content the content to be sent
     */
    void jsonp(HttpStatus status, Object content);

    /**
     * Sets the Content-Type to the mime lookup of type, or when "/" is present
     * the Content-Type is simply set to this literal value.
     *
     * @param type the Content-Type to be set
     */
    void type(String type);

    /**
     * Get the Content-Type.
     *
     * @return the Conten-Type
     */
    String type();

    /**
     * Render the view with the given options.
     *
     * @param view    the view
     * @param options the options
     */
    void render(String view, Options options);

    /**
     * Render the view with the given options.
     *
     * @param view    the view
     * @param options the options
     */
    void render(final String view, final Map<String, Object> options);

    /**
     * Add a listener, that will be called after the response has been created.
     *
     * @param listener the listener to be added
     */
    void addListener(ResponseListener listener);

    /**
     * Send the file to the client. The file must be on the classpath.
     *
     * @param filename the full path to the file
     */
    void sendFile(String filename);

}
