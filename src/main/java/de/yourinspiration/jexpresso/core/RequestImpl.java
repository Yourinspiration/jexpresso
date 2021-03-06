package de.yourinspiration.jexpresso.core;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.yourinspiration.jexpresso.exception.BadRequestException;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;

/**
 * Implementation for {@link Request}.
 *
 * @author Marcel Härle
 */
public class RequestImpl implements Request {

    private final FullHttpRequest fullHttpRequest;
    private final Gson gson;
    private final RequestResponseContext requestResponseContext;
    private final List<Cookie> customCookies = new ArrayList<>();
    private Route route;

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
        try {
            return gson.fromJson(body(), clazz);
        } catch (JsonSyntaxException jsonException) {
            throw new BadRequestException("invalid json: " + body());
        }
    }

    @Override
    public Map<String, String> params() {
        final Map<String, String> params = new HashMap<>();

        final String[] currentPathTokens = fullHttpRequest.getUri().split("/");
        final String[] pathTokens = route.getPath().split("/");

        for (int i = 0, l = currentPathTokens.length; i < l; i++) {
            if (pathTokens[i].startsWith(":")) {
                int queryIndex = currentPathTokens[i].lastIndexOf("?");
                if (queryIndex > 0) {
                    params.put(pathTokens[i].replace(":", ""), currentPathTokens[i].substring(0, queryIndex));
                } else {
                    params.put(pathTokens[i].replace(":", ""), currentPathTokens[i]);
                }
            }
        }

        return params;
    }

    @Override
    public String param(final String name) {
        return params().get(name);
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
        return query().get(name);
    }

    @Override
    public List<Cookie> cookies() {
        final List<Cookie> cookies = new ArrayList<>();
        String cookieString = fullHttpRequest.headers().get(COOKIE);
        if (cookieString != null) {
            Set<Cookie> decoded = CookieDecoder.decode(cookieString);
            cookies.addAll(decoded.stream().collect(Collectors.toList()));
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
        for (Cookie cookie : cookies()) {
            if (cookie.getName().equals(name)) {
                return cookie;
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
        return acceptsHeader(acceptHeadersTokens, types);
    }

    @Override
    public String acceptsCharset(final String... charsets) {
        final String[] headersTokens = get(HttpHeaders.Names.ACCEPT_CHARSET).split(",");
        return acceptsHeader(headersTokens, charsets);
    }

    @Override
    public String acceptsLanguage(final String... lang) {
        final String[] headersTokens = get(HttpHeaders.Names.ACCEPT_LANGUAGE).split(",");
        return acceptsHeader(headersTokens, lang);
    }

    @Override
    public boolean is(final String type) {
        final String contentType = get(HttpHeaders.Names.CONTENT_TYPE);
        return contentType != null && contentType.matches(type.replaceAll("\\*", ".*"));
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
    public boolean xhr() {
        return "XMLHttpRequest".equals(get("X-Requested-With"));
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

    private String acceptsHeader(final String[] headersTokens, final String... types) {
        // We need an ordered map, ordered by the qualifiers
        final Map<String, List<String>> qualifiedHeaders = new TreeMap<>((o1, o2) -> o2.compareTo(o1));

        for (String headersToken : headersTokens) {
            // Types may have a qualifier or not. If the type has no
            // qualifier it has the highest priority.
            final String headerToken = headersToken.trim();
            if (headerToken.matches(".*;q=.*")) {
                final String qualifier = headerToken.substring(headerToken.indexOf(";") + 1);
                final String header = headerToken.substring(0, headerToken.indexOf(";"));
                List<String> headers = qualifiedHeaders.get(qualifier);
                if (headers == null) {
                    headers = new ArrayList<>();
                }
                headers.add(header);
                qualifiedHeaders.put(qualifier, headers);
            } else {
                List<String> headers = qualifiedHeaders.get("q=1.0");
                if (headers == null) {
                    headers = new ArrayList<>();
                }
                headers.add(headersToken.trim());
                qualifiedHeaders.put("q=1.0", headers);
            }
        }

        // Take the first best match.
        for (Entry<String, List<String>> entry : qualifiedHeaders.entrySet()) {
            for (String type : types) {
                for (String header : entry.getValue()) {
                    if (type.matches(header.replace("*", ".*"))) {
                        return type;
                    }
                }
            }
        }

        return null;
    }

}
