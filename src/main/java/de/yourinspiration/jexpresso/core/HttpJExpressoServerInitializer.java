package de.yourinspiration.jexpresso.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Netty channel initializer.
 *
 * @author Marcel Härle
 */
public class HttpJExpressoServerInitializer extends ChannelInitializer<Channel> {

    public static final int MAX_CONTENT_LENGTH = 1048576;

    private final JExpressoBase base;

    protected HttpJExpressoServerInitializer(final JExpressoBase base) {
        this.base = base;
    }

    @Override
    protected void initChannel(final Channel ch) throws Exception {
        final ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpContentCompressor());
        p.addLast(new MiddlewareChannelHandler(base.getMiddlewareHandlers()), new HttpJExpressoServerHandler(base));
    }
}
