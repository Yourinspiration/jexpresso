package de.yourinspiration.jexpresso.core;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

/**
 * Provides access to data shared by the entire request-response-chain.
 *
 * @author Marcel Härle
 */
public class RequestResponseContext {

    public static final AttributeKey<RequestResponseContext> ATTR_KEY;

    static {
        ATTR_KEY = AttributeKey.valueOf(RequestResponseContext.class.getName());
    }

    private final Map<String, Object> attributes = new HashMap<>();
    private final Channel channel;
    private final ResponseImpl response;

    protected RequestResponseContext(final Channel channel, final ResponseImpl response) {
        this.channel = channel;
        this.response = response;
    }

    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    public SocketAddress localAddress() {
        return channel.localAddress();
    }

    public <T> Optional<T> getAttribute(final String name, final Class<T> attributeClass) {
        if (attributes.get(name).getClass().equals(attributeClass)) {
            return Optional.ofNullable((T) attributes.get(name));
        } else {
            return Optional.empty();
        }
    }

    public void setAttribute(final String name, final Object value) {
        attributes.put(name, value);
    }

    public void setCookie(final Cookie cookie) {
        response.fullHttpReponse().headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    public ResponseImpl getResponse() {
        return response;
    }

}
