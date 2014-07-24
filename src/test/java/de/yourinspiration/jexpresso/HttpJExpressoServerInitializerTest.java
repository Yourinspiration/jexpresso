package de.yourinspiration.jexpresso;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.AtLeast;

import de.yourinspiration.jexpresso.exception.ExceptionHandlerEntry;

/**
 * Test case for {@link HttpJExpressoServerInitializer}.
 * 
 * @author Marcel Härle
 *
 */
public class HttpJExpressoServerInitializerTest {

    @Test
    public void testInitChannelChannel() throws Exception {
        final List<Route> routes = new ArrayList<>();
        final List<ExceptionHandlerEntry> exceptionHandlerEntries = new ArrayList<>();
        final Map<String, TemplateEngine> templateEngines = new HashMap<>();
        final List<MiddlewareHandler> middlewareHandlers = new ArrayList<>();

        final HttpJExpressoServerInitializer initializer = new HttpJExpressoServerInitializer(routes,
                exceptionHandlerEntries, middlewareHandlers, templateEngines);

        final Channel ch = Mockito.mock(Channel.class);
        final ChannelPipeline p = Mockito.mock(ChannelPipeline.class);
        Mockito.when(ch.pipeline()).thenReturn(p);

        initializer.initChannel(ch);

        Mockito.verify(p, new AtLeast(1)).addLast(Matchers.any(HttpRequestDecoder.class));
        Mockito.verify(p, new AtLeast(1)).addLast(Matchers.any(HttpObjectAggregator.class));
        Mockito.verify(p, new AtLeast(1)).addLast(Matchers.any(HttpResponseEncoder.class));
        Mockito.verify(p, new AtLeast(1)).addLast(Matchers.any(HttpContentCompressor.class));
        Mockito.verify(p, new AtLeast(1)).addLast(Matchers.any(MiddlewareChannelHandler.class));
    }

}
