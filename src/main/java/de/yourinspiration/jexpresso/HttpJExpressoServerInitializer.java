package de.yourinspiration.jexpresso;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.List;
import java.util.Map;

import com.google.common.cache.LoadingCache;

import de.yourinspiration.jexpresso.exception.ExceptionHandlerEntry;

/**
 * Netty channel initializer.
 * 
 * @author Marcel Härle
 *
 */
public class HttpJExpressoServerInitializer extends ChannelInitializer<Channel> {

    private final List<Route> routes;
    private final List<ExceptionHandlerEntry> exceptionHandlerEntries;
    private final String staticResources;
    private final LoadingCache<String, FileCacheEntry> fileCache;
    private final boolean useFileCache;
    private final Map<String, TemplateEngine> templateEngines;

    private final List<MiddlewareHandler> middlewareHandlers;

    protected HttpJExpressoServerInitializer(final List<Route> routes,
            final List<ExceptionHandlerEntry> exceptionHandlerEntries, final String staticResources,
            final LoadingCache<String, FileCacheEntry> fileCache, final boolean useFileCache,
            final List<MiddlewareHandler> middlewareHandlers, final Map<String, TemplateEngine> templateEngines) {
        this.routes = routes;
        this.exceptionHandlerEntries = exceptionHandlerEntries;
        this.staticResources = staticResources;
        this.fileCache = fileCache;
        this.useFileCache = useFileCache;
        this.middlewareHandlers = middlewareHandlers;
        this.templateEngines = templateEngines;
    }

    @Override
    protected void initChannel(final Channel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new HttpRequestDecoder());
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        p.addLast(new HttpContentCompressor());
        p.addLast(new MiddlewareChannelHandler(middlewareHandlers), new HttpJExpressoServerHandler(routes,
                exceptionHandlerEntries, staticResources, fileCache, useFileCache, templateEngines));
    }
}
