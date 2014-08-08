package de.yourinspiration.jexpresso;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;

/**
 * Provides access to data shared by the entire request-response-chain.
 *
 * @author Marcel Härle
 */
public class RequestResponseContext {

    public static AttributeKey<RequestResponseContext> ATTR_KEY;

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

    public Object getAttribute(final String name) {
        return attributes.get(name);
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
