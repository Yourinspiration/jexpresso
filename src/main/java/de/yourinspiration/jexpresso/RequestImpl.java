package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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
    private final List<Cookie> customCookies = new ArrayList<>();

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
        return fullHttpRequest.content().toString(CharsetUtil.UTF_8);
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
            if (pathTokens[i].startsWith(":")) {
                params.put(pathTokens[i].replace(":", ""), currentPathTokens[i]);
            }
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
        for (Cookie cookie : customCookies) {
            for (int i = 0, l = cookies.size(); i < l; i++) {
                if (cookies.get(i).getName().equals(cookie.getName())) {
                    cookies.remove(i);
                    break;
                }
            }
            cookies.add(cookie);
        }
        return cookies;
    }

    @Override
    public Cookie cookie(final String name) {
        for (Cookie cookie : customCookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }
        final String cookieString = fullHttpRequest.headers().get(COOKIE);
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
    public void setCookie(final Cookie cookie) {
        for (int i = 0, l = customCookies.size(); i < l; i++) {
            if (customCookies.get(i).getName().equals(cookie.getName())) {
                customCookies.remove(i);
                break;
            }
        }
        customCookies.add(cookie);
    }

    @Override
    public String get(final String field) {
        return fullHttpRequest.headers().get(field);
    }

    @Override
    public String accepts(final String... types) {
        // text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
        // The MIME types are separated by commas.
        final String[] acceptHeadersTokens = get(HttpHeaders.Names.ACCEPT).split(",");

        // We need an ordered map, ordered by the qualifiers
        final Map<String, List<String>> qualifiedAcceptHeaders = new TreeMap<>(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                // Because q=1.0 is before q=0.9 we must order the other way
                // round. The natural order would put q=1.0 after q=0.9
                return o2.compareTo(o1);
            }
        });

        for (int i = 0, l = acceptHeadersTokens.length; i < l; i++) {
            // MIME types may have a qualifier or not. If the MIME type has no
            // qualifier it has the highest priority.
            if (acceptHeadersTokens[i].matches(".*;q=.*")) {
                final String qualifier = acceptHeadersTokens[i].substring(acceptHeadersTokens[i].indexOf(";") + 1);
                final String acceptHeader = acceptHeadersTokens[i].substring(0, acceptHeadersTokens[i].indexOf(";"));
                List<String> acceptHeaders = qualifiedAcceptHeaders.get(qualifier);
                if (acceptHeaders == null) {
                    acceptHeaders = new ArrayList<>();
                }
                acceptHeaders.add(acceptHeader);
                qualifiedAcceptHeaders.put(qualifier, acceptHeaders);
            } else {
                List<String> acceptHeaders = qualifiedAcceptHeaders.get("q=1.0");
                if (acceptHeaders == null) {
                    acceptHeaders = new ArrayList<>();
                }
                acceptHeaders.add(acceptHeadersTokens[i]);
                qualifiedAcceptHeaders.put("q=1.0", acceptHeaders);
            }
        }

        // Take the first best match.
        for (Entry<String, List<String>> entry : qualifiedAcceptHeaders.entrySet()) {
            for (String type : types) {
                for (String acceptHeader : entry.getValue()) {
                    if (type.matches(acceptHeader.replace("*", ".*"))) {
                        return type;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public String acceptsCharset(final String... charsets) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public String acceptsLanguage(final String... lang) {
        throw new RuntimeException("not implemented yet");
    }

    @Override
    public boolean is(final String type) {
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
