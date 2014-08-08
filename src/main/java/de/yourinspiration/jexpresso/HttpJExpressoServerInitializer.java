package de.yourinspiration.jexpresso;

import de.yourinspiration.jexpresso.exception.ExceptionHandlerEntry;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.List;
import java.util.Map;

/**
 * Netty channel initializer.
 *
 * @author Marcel Härle
 */
public class HttpJExpressoServerInitializer extends ChannelInitializer<Channel> {

    public static final int MAX_CONTENT_LENGTH = 1048576;

    private final List<Route> routes;
    private final List<ExceptionHandlerEntry> exceptionHandlerEntries;
    private final Map<String, TemplateEngine> templateEngines;
    private final List<MiddlewareHandler> middlewareHandlers;

    protected HttpJExpressoServerInitializer(final List<Route> routes,
                                             final List<ExceptionHandlerEntry> exceptionHandlerEntries,
                                             final List<MiddlewareHandler> middlewareHandlers, final Map<String, TemplateEngine> templateEngines) {
        this.routes = routes;
        this.exceptionHandlerEntries = exceptionHandlerEntries;
        this.middlewareHandlers = middlewareHandlers;
        this.templateEngines = templateEngines;
    }

    @Override
    protected void initChannel(final Channel ch) throws Exception {
        final ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpContentCompressor());
        p.addLast(new MiddlewareChannelHandler(middlewareHandlers), new HttpJExpressoServerHandler(routes,
                exceptionHandlerEntries, templateEngines));
    }
}
