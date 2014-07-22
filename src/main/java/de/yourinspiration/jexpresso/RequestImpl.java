package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpHeaders.Names.ACCEPT;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;

/**
 * Implementation for {@link Request}.
 * 
 * @author Marcel Härle
 *
 */
public class RequestImpl implements Request {

    private final FullHttpRequest fullHttpRequest;
    private Route route;
    private final Gson gson;
    private final RequestResponseContext requestResponseContext;

    protected RequestImpl(final FullHttpRequest fullHttpRequest, final RequestResponseContext requestResponseContext) {
        this(fullHttpRequest, requestResponseContext, null);
    }

    protected RequestImpl(final FullHttpRequest fullHttpRequest, final RequestResponseContext requestContext,
            final Route route) {
        this.fullHttpRequest = fullHttpRequest;
        this.requestResponseContext = requestContext;
        this.route = route;
        this.gson = new Gson();
    }

    protected void setRoute(final Route route) {
        this.route = route;
    }

    protected FullHttpRequest fullHttpRequest() {
        return fullHttpRequest;
    }

    protected RequestResponseContext context() {
        return requestResponseContext;
    }

    // ================================================================
    // API ============================================================
    // ================================================================

    @Override
    public String body() {
        final StringBuilder body = new StringBuilder();
        if (fullHttpRequest.content().isReadable()) {
            body.append(fullHttpRequest.content().toString(CharsetUtil.UTF_8));
        }
        return body.toString();
    }

    @Override
    public byte[] bytes() {
        return fullHttpRequest.content().array();
    }

    @Override
    public <T> T json(Class<T> clazz) {
        return gson.fromJson(body(), clazz);
    }

    @Override
    public Map<String, String> params() {
        final Map<String, String> params = new HashMap<>();

        final String[] currentPathTokens = fullHttpRequest.getUri().split("/");
        final String[] pathTokens = route.getPath().split("/");

        for (int i = 0, l = pathTokens.length; i < l; i++) {
            params.put(pathTokens[i], currentPathTokens[i]);
        }

        return params;
    }

    @Override
    public String param(final String name) {
        String param = "";

        final String[] currentPathTokens = fullHttpRequest.getUri().split("/");
        final String[] pathTokens = route.getPath().split("/");
        for (int i = 0, l = pathTokens.length; i < l; i++) {
            if (pathTokens[i].equals(":" + name)) {
                param = currentPathTokens[i];
                break;
            }
        }

        return param;
    }

    @Override
    public Map<String, String> query() {
        final Map<String, String> query = new HashMap<>();

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(fullHttpRequest.getUri());
        Map<String, List<String>> params = queryStringDecoder.parameters();

        for (Entry<String, List<String>> entry : params.entrySet()) {
            final StringBuilder buf = new StringBuilder();
            List<String> vals = entry.getValue();
            for (String val : vals) {
                buf.append(val);
            }
            query.put(entry.getKey(), buf.toString());
        }

        return query;
    }

    @Override
    public String query(final String name) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(fullHttpRequest.getUri());
        Map<String, List<String>> params = queryStringDecoder.parameters();
        final StringBuilder buf = new StringBuilder();
        if (params.get(name) != null) {

            List<String> vals = params.get(name);
            for (String val : vals) {
                buf.append(val);
            }
        }
        return buf.toString();
    }

    @Override
    public List<Cookie> cookies() {
        final List<Cookie> cookies = new ArrayList<>();
        String cookieString = fullHttpRequest.headers().get(COOKIE);
        if (cookieString != null) {
            Set<Cookie> decoded = CookieDecoder.decode(cookieString);
            for (Cookie cookie : decoded) {
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    @Override
    public Cookie cookie(final String name) {
        String cookieString = fullHttpRequest.headers().get(COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = CookieDecoder.decode(cookieString);
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    @Override
    public String get(String field) {
        return fullHttpRequest.headers().get(field);
    }

    @Override
    public String accepts(String... types) {
        fullHttpRequest.headers().get(ACCEPT);
        return null;
    }

    @Override
    public String acceptsCharset(String... charsets) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public String acceptsLanguage(String... lang) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean is(String type) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public String ip() {
        return requestResponseContext.remoteAddress().getAddress().getHostAddress();
    }

    @Override
    public String path() {
        return fullHttpRequest.getUri();
    }

    @Override
    public String host() {
        return fullHttpRequest.headers().get(HOST);
    }

    @Override
    public boolean fresh() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean stale() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean xhr() {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public String protocol() {
        return fullHttpRequest.getProtocolVersion().protocolName();
    }

    @Override
    public boolean secure() {
        final String protocol = protocol();
        return protocol.equalsIgnoreCase("https");
    }

    @Override
    public Object attribute(final String name) {
        return requestResponseContext.getAttribute(name);
    }

    @Override
    public void attribute(final String name, final Object obj) {
        requestResponseContext.setAttribute(name, obj);
    }

    @Override
    public HttpMethod method() {
        return fullHttpRequest.getMethod();
    }

}
