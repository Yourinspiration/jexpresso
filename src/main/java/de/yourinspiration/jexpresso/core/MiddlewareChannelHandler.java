package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.transformer.HtmlTransformer;
import de.yourinspiration.jexpresso.transformer.JsonTransformer;
import de.yourinspiration.jexpresso.transformer.PlainTextTransformer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.Attribute;
import org.pmw.tinylog.Logger;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Handles the middleware chain.
 *
 * @author Marcel Härle
 */
public class MiddlewareChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final List<MiddlewareHandler> handlers;

    protected ChannelHandlerContext ctx;
    protected RequestImpl requestImpl;
    protected ResponseImpl responseImpl;

    protected MiddlewareChannelHandler(final List<MiddlewareHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        this.ctx = ctx;

        // Create both the FullHttpResponse and Response, so that the middleware
        // and the final routes operate on the some data.
        final FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                request.getDecoderResult().isSuccess() ? OK : BAD_REQUEST, Unpooled.buffer());

        responseImpl = new ResponseImpl(response);

        final RequestResponseContext requestResponseContext = new RequestResponseContext(ctx.channel(), responseImpl);
        Attribute<RequestResponseContext> attr = ctx.channel().attr(RequestResponseContext.ATTR_KEY);
        attr.set(requestResponseContext);

        requestImpl = new RequestImpl(request, requestResponseContext);

        if (handlers.size() > 0) {
            final NextImpl next = new NextImpl(0, this);
            final MiddlewareHandler handler = handlers.get(0);
            handler.handle(requestImpl, responseImpl, next);
        } else {
            request.retain();
            ctx.fireChannelRead(request);
        }
    }

    /**
     * Call the next middleware handler.
     *
     * @param currentIndex the index of the current finished handler
     */
    protected void next(final int currentIndex) {
        if (currentIndex < handlers.size() - 1) {
            final NextImpl next = new NextImpl(currentIndex + 1, this);
            final MiddlewareHandler handler = handlers.get(currentIndex + 1);
            handler.handle(requestImpl, responseImpl, next);
        } else {
            // Next is called on the last middleware handler. So finish the
            // middleware channel handler.
            requestImpl.fullHttpRequest().retain();
            ctx.fireChannelRead(requestImpl.fullHttpRequest());
        }
    }

    /**
     * Cancel the request-response-chain and respond immediately to the client.
     */
    protected void cancel() {
        responseImpl.fullHttpReponse().headers().set(CONTENT_TYPE, responseImpl.type());

        if (responseImpl.isBinary()) {
            responseImpl.fullHttpReponse().content().writeBytes(responseImpl.getBytes());
        } else {
            final Object model = responseImpl.getContent();

            Logger.debug("Route model created {0}", model);

            String renderedModel;

            switch (responseImpl.type()) {
                case "application/json":
                    renderedModel = new JsonTransformer().render(model);
                    break;
                case "text/html":
                    renderedModel = new HtmlTransformer().render(model);
                    break;
                default:
                    renderedModel = new PlainTextTransformer().render(model);
                    break;
            }

            Logger.debug("Rendered model {0}", renderedModel);

            responseImpl.fullHttpReponse().content().writeBytes(renderedModel.getBytes());
        }

        ctx.writeAndFlush(responseImpl.fullHttpReponse()).addListener(ChannelFutureListener.CLOSE);
    }

}
