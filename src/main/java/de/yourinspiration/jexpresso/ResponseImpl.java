package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LOCATION;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.ServerCookieEncoder;

import java.util.Map;
import java.util.Map.Entry;

import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.template.Options;

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

    private Map<String, Object> options;
    private String template;
    private boolean isTemplate = false;

    private boolean finished = false;

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

    protected boolean isBinafy() {
        return isBinary;
    }

    protected FullHttpResponse fullHttpReponse() {
        return fullHttpResponse;
    }

    // ========================================================
    // API ====================================================
    // ========================================================

    @Override
    public void status(final int status) {
        fullHttpResponse.setStatus(HttpResponseStatus.valueOf(status));
    }

    @Override
    public int status() {
        return fullHttpResponse.getStatus().code();
    }

    @Override
    public void set(final String field, final String value) {
        fullHttpResponse.headers().add(field, value);
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
        fullHttpResponse.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Override
    public void clearCookie(String name) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void redirect(String url) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void location(String location) {
        set(LOCATION, location);
    }

    @Override
    public void send(final Object content) {
        if (finished) {
            throw new RuntimeException("send, json or render can only by called once");
        }
        this.content = content;
        if (content instanceof String) {
            type(ContentType.TEXT_HTML.type());
        } else {
            type(ContentType.APPLICATION_JSON.type());
        }
        this.finished = true;
    }

    @Override
    public void send(int status, Object content) {
        if (finished) {
            throw new RuntimeException("send, json or render can only by called once");
        }
        status(status);
        this.content = content;
        if (content instanceof String) {
            type(ContentType.TEXT_HTML.type());
        } else {
            type(ContentType.APPLICATION_JSON.type());
        }
        this.finished = true;
    }

    @Override
    public void send(byte[] content) {
        if (finished) {
            throw new RuntimeException("send, json or render can only by called once");
        }
        this.bytes = content;
        type(ContentType.APPLICATION_OCTETSTREAM.type());
        this.finished = true;
    }

    @Override
    public void send(int status, byte[] content) {
        status(status);
        send(content);
    }

    @Override
    public void send(int status) {
        status(status);
        send("");
    }

    @Override
    public void json(final Object content) {
        if (finished) {
            throw new RuntimeException("send, json or render can only by called once");
        }
        this.content = content;
        type(ContentType.APPLICATION_JSON.type());
        this.finished = true;
    }

    @Override
    public void json(int status, Object content) {
        status(status);
        json(content);
    }

    @Override
    public void jsonp(Object content) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public void jsonp(int status, Object content) {
        status(status);
        jsonp(content);
    }

    @Override
    public void type(String type) {
        fullHttpResponse.headers().set(CONTENT_TYPE, type);
    }

    @Override
    public String type() {
        return fullHttpResponse.headers().get(CONTENT_TYPE);
    }

    @Override
    public void render(String view, Options options) {
        render(view, options.create());
    }

    @Override
    public void render(final String template, final Map<String, Object> options) {
        if (finished) {
            throw new RuntimeException("send, json or render can only by called once");
        }
        type(ContentType.TEXT_HTML.type());
        this.template = template;
        this.options = options;
        this.isTemplate = true;
        this.finished = true;
    }

}
