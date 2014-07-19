package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.Attribute;

import java.util.List;

/**
 * Handles the middleware chain.
 * 
 * @author Marcel Härle
 *
 */
public class MiddlewareChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final List<MiddlewareHandler> handlers;

    protected MiddlewareChannelHandler(final List<MiddlewareHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        // Create both the FullHttpResponse and Response, so that the middleware
        // and the final routes operate on the some data.
        final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                request.getDecoderResult().isSuccess() ? OK : BAD_REQUEST, Unpooled.buffer());

        final ResponseImpl sparkleResponse = new ResponseImpl(response);

        final RequestResponseContext requestResponseContext = new RequestResponseContext(ctx.channel(), sparkleResponse);
        Attribute<RequestResponseContext> attr = ctx.channel().attr(RequestResponseContext.ATTR_KEY);
        attr.set(requestResponseContext);

        for (MiddlewareHandler handler : handlers) {
            handler.handle(new RequestImpl(request, requestResponseContext), sparkleResponse);
        }

        request.retain();
        ctx.fireChannelRead(request);
    }

}
