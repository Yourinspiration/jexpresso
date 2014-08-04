package de.yourinspiration.jexpresso;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.http.HttpStatus;

/**
 * Implementation for {@link Response}.
 * 
 * @author Marcel Härle
 *
 */
public class ResponseImpl implements Response {

    private final FullHttpResponse fullHttpResponse;
    private Object content;
    private byte[] bytes;
    private boolean isBinary = false;
    private final List<ResponseListener> responseListener = new ArrayList<ResponseListener>();

    private Map<String, Object> options;
    private String template;
    private boolean isTemplate = false;
    private boolean isRedirect = false;
    private boolean isJsonp = false;

    protected ResponseImpl(final FullHttpResponse fullHttpResponse) {
        this.fullHttpResponse = fullHttpResponse;
    }

    protected Object getContent() {
        return content;
    }

    protected byte[] getBytes() {
        return bytes;
    }

    protected String getTemplate() {
        return template;
    }

    protected Map<String, Object> getOptions() {
        return options;
    }

    protected boolean isTemplate() {
        return isTemplate;
    }

    protected boolean isBinary() {
        return isBinary;
    }

    protected FullHttpResponse fullHttpReponse() {
        return fullHttpResponse;
    }

    protected boolean isRedirect() {
        return isRedirect;
    }

    protected boolean isJsonp() {
        return isJsonp;
    }

    protected void invokeResponseListeners(final Request request) {
        for (ResponseListener listener : responseListener) {
            listener.callback(request, this);
        }
    }

    // ========================================================
    // API ====================================================
    // ========================================================

    @Override
    public void status(final HttpStatus status) {
        fullHttpResponse.setStatus(HttpResponseStatus.valueOf(status.value()));
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.valueOf(fullHttpResponse.getStatus().code());
    }

    @Override
    public void set(final String field, final String value) {
        fullHttpResponse.headers().set(field, value);
    }

    @Override
    public void set(final Map<String, String> fields) {
        for (Entry<String, String> entry : fields.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String get(final String field) {
        return fullHttpResponse.headers().get(field);
    }

    @Override
    public void cookie(final Cookie cookie) {
        fullHttpResponse.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Override
    public void clearCookie(final String name) {
        final Cookie cookie = new DefaultCookie(name, "");
        cookie.setMaxAge(0);
        fullHttpResponse.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Override
    public void redirect(final String url) {
        isRedirect = true;
        set(HttpHeaders.Names.LOCATION, url);
        status(HttpStatus.TEMPORARY_REDIRECT);
    }

    @Override
    public void location(final String location) {
        set(HttpHeaders.Names.LOCATION, location);
    }

    @Override
    public void send(final Object content) {
        this.content = content;
        if (content instanceof String) {
            type(ContentType.TEXT_HTML.type());
        } else {
            type(ContentType.APPLICATION_JSON.type());
        }
    }

    @Override
    public void send(final HttpStatus status, final Object content) {
        status(status);
        this.content = content;
        if (content instanceof String) {
            type(ContentType.TEXT_HTML.type());
        } else {
            type(ContentType.APPLICATION_JSON.type());
        }
    }

    @Override
    public void send(final byte[] content) {
        this.bytes = content;
        this.isBinary = true;
        type(ContentType.APPLICATION_OCTETSTREAM.type());
    }

    @Override
    public void send(final HttpStatus status, final byte[] content) {
        status(status);
        send(content);
    }

    @Override
    public void send(final HttpStatus status) {
        status(status);
        send(status.getReasonPhrase());
    }

    @Override
    public void json(final Object content) {
        this.content = content;
        type(ContentType.APPLICATION_JSON.type());
    }

    @Override
    public void json(final HttpStatus status, final Object content) {
        status(status);
        json(content);
    }

    @Override
    public void jsonp(final Object content) {
        isJsonp = true;
        json(content);
    }

    @Override
    public void jsonp(final HttpStatus status, final Object content) {
        status(status);
        jsonp(content);
    }

    @Override
    public void type(final String type) {
        fullHttpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, type);
    }

    @Override
    public String type() {
        return fullHttpResponse.headers().get(HttpHeaders.Names.CONTENT_TYPE);
    }

    @Override
    public void render(final String view, final Options options) {
        render(view, options.create());
    }

    @Override
    public void render(final String template, final Map<String, Object> options) {
        type(ContentType.TEXT_HTML.type());
        this.template = template;
        this.options = options;
        this.isTemplate = true;
    }

    @Override
    public void addListener(ResponseListener listener) {
        responseListener.add(listener);
    }

}
