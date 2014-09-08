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
    public Optional<String> param(final String name) {
        return Optional.of(params().get(name));
    }

    @Override
    public Map<String, String> postParams() {
        if (method().equals(HttpMethod.POST)) {
            final Map<String, String> paramMap = new HashMap<>();
            final String[] params = body().split("&");
            for (String param : params) {
                final String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    paramMap.put(keyValue[0], keyValue[1]);
                }
            }
            return paramMap;
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public Optional<String> postParam(final String name) {
        final Map<String, String> params = postParams();
        return params.containsKey(name) ? Optional.ofNullable(params.get(name)) : Optional.empty();
    }

    @Override
    public Map<String, String> query() {
        final Map<String, String> query = new HashMap<>();

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(fullHttpRequest.getUri());
        Map<String, List<String>> params = queryStringDecoder.parameters();

        for (Entry<String, List<String>> entry : params.entrySet()) {
            final StringBuilder buf = new StringBuilder();
            List<String> vals = entry.getValue();
            vals.forEach(buf::append);
            query.put(entry.getKey(), buf.toString());
        }

        return query;
    }

    @Override
    public Optional<String> query(final String name) {
        return Optional.ofNullable(query().get(name));
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
    public Optional<Cookie> cookie(final String name) {
        for (Cookie cookie : cookies()) {
            if (cookie.getName().equals(name)) {
                return Optional.ofNullable(cookie);
            }
        }
        return Optional.empty();
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
    public Optional<String> get(final String field) {
        if (fullHttpRequest.headers().get(field) != null) {
            return Optional.ofNullable(fullHttpRequest.headers().get(field));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> accepts(final String... types) {
        // text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
        // The MIME types are separated by commas.
        if (get(HttpHeaders.Names.ACCEPT).isPresent()) {
            final String[] acceptHeadersTokens = get(HttpHeaders.Names.ACCEPT).get().split(",");
            return acceptsHeader(acceptHeadersTokens, types);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> acceptsCharset(final String... charsets) {
        if (get(HttpHeaders.Names.ACCEPT_CHARSET).isPresent()) {
            final String[] headersTokens = get(HttpHeaders.Names.ACCEPT_CHARSET).get().split(",");
            return acceptsHeader(headersTokens, charsets);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> acceptsLanguage(final String... lang) {
        if (get(HttpHeaders.Names.ACCEPT_LANGUAGE).isPresent()) {
            final String[] headersTokens = get(HttpHeaders.Names.ACCEPT_LANGUAGE).get().split(",");
            return acceptsHeader(headersTokens, lang);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean is(final String type) {
        final Optional<String> contentType = get(HttpHeaders.Names.CONTENT_TYPE);
        return contentType.isPresent() && contentType.get().matches(type.replaceAll("\\*", ".*"));
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
        return get("X-Requested-With").isPresent() && "XMLHttpRequest".equals(get("X-Requested-With").get());
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
    public <T> Optional<T> attribute(final String name, final Class<T> attributeClass) {
        return Optional.ofNullable(requestResponseContext.getAttribute(name, attributeClass).get());
    }

    @Override
    public void attribute(final String name, final Object obj) {
        requestResponseContext.setAttribute(name, obj);
    }

    @Override
    public HttpMethod method() {
        return fullHttpRequest.getMethod();
    }

    private class HeaderComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
        }
    }

    private Optional<String> acceptsHeader(final String[] headersTokens, final String... types) {
        // We need an ordered map, ordered by the qualifiers
        final Map<String, List<String>> qualifiedHeaders = new TreeMap<>(new HeaderComparator());

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
                        return Optional.ofNullable(type);
                    }
                }
            }
        }

        return Optional.empty();
    }

}
