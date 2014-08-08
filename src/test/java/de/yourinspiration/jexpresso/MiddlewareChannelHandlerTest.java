package de.yourinspiration.jexpresso;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.Attribute;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Test case for {@link MiddlewareChannelHandler}.
 *
 * @author Marcel Härle
 */
public class MiddlewareChannelHandlerTest {

    private final List<MiddlewareHandler> handlers = new ArrayList<>();
    private MiddlewareChannelHandler handler;
    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private FullHttpRequest request;
    @Mock
    private Channel channel;
    @Mock
    private DecoderResult decoderResult;
    @Mock
    private Attribute<RequestResponseContext> attr;
    @Mock
    private HttpHeaders headers;
    @Mock
    private ByteBuf byteBuf;
    @Mock
    private ChannelFuture channelFuture;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(ctx.channel()).thenReturn(channel);
        Mockito.when(request.getDecoderResult()).thenReturn(decoderResult);
        Mockito.when(decoderResult.isSuccess()).thenReturn(true);
        Mockito.when(channel.attr(RequestResponseContext.ATTR_KEY)).thenReturn(attr);
        Mockito.when(request.headers()).thenReturn(headers);
        Mockito.when(request.content()).thenReturn(byteBuf);
        Mockito.when(ctx.writeAndFlush(Matchers.any())).thenReturn(channelFuture);

        handler = new MiddlewareChannelHandler(handlers);
    }

    @Test
    public void testNoHandlersRegistered() throws Exception {
        handler.channelRead0(ctx, request);

        Mockito.verify(request).retain();
        Mockito.verify(ctx).fireChannelRead(request);
    }

    @Test
    public void testForRegistedHandler() throws Exception {
        final MiddlewareHandler middlewareHandler = Mockito.mock(MiddlewareHandler.class);

        handlers.add(middlewareHandler);

        handler.channelRead0(ctx, request);

        Mockito.verify(middlewareHandler).handle(Matchers.any(Request.class), Matchers.any(Response.class),
                Matchers.any(Next.class));
    }

    @Test
    public void testNextForMoreHandlers() throws Exception {
        final MiddlewareHandler middlewareHandler1 = Mockito.mock(MiddlewareHandler.class);
        final MiddlewareHandler middlewareHandler2 = Mockito.mock(MiddlewareHandler.class);

        handlers.add(middlewareHandler1);
        handlers.add(middlewareHandler2);

        handler.channelRead0(ctx, request);

        handler.next(0);

        Mockito.verify(middlewareHandler2).handle(Matchers.any(Request.class), Matchers.any(Response.class),
                Matchers.any(Next.class));
    }

    @Test
    public void testNextForLastHandlers() throws Exception {
        final MiddlewareHandler middlewareHandler = Mockito.mock(MiddlewareHandler.class);

        handlers.add(middlewareHandler);

        handler.channelRead0(ctx, request);

        handler.next(0);

        Mockito.verify(request).retain();
        Mockito.verify(ctx).fireChannelRead(request);
    }

    @Test
    public void testCancelForHtml() throws Exception {
        handler.channelRead0(ctx, request);

        handler.responseImpl.send("test");

        handler.cancel();

        Mockito.verify(ctx).writeAndFlush(Matchers.any());
    }

    @Test
    public void testCancelForText() throws Exception {
        handler.channelRead0(ctx, request);

        handler.responseImpl.send("test");
        handler.responseImpl.type("text/plain");

        handler.cancel();

        Mockito.verify(ctx).writeAndFlush(Matchers.any());
    }

    @Test
    public void testCancelForJson() throws Exception {
        handler.channelRead0(ctx, request);

        handler.responseImpl.send("test");
        handler.responseImpl.type("application/json");

        handler.cancel();

        Mockito.verify(ctx).writeAndFlush(Matchers.any());
    }

}
