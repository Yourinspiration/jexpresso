package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.exception.ExceptionHandlerEntry;
import de.yourinspiration.jexpresso.exception.HttpStatusException;
import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.http.HttpStatus;
import de.yourinspiration.jexpresso.transformer.PlainTextTransformer;
import de.yourinspiration.jexpresso.transformer.ResponseTransformer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.Attribute;
import io.netty.util.CharsetUtil;
import org.pmw.tinylog.Logger;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;

/**
 * Handles the HTTP request.
 *
 * @author Marcel Härle
 */
public class HttpJExpressoServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final byte[] EMPTY_BYTES = new byte[0];

    private final JExpressoBase base;

    private RequestImpl requestImpl;
    private ResponseImpl responseImpl;

    protected HttpJExpressoServerHandler(final JExpressoBase base) {
        this.base = base;
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) throws Exception {
        Attribute<RequestResponseContext> attr = ctx.channel().attr(RequestResponseContext.ATTR_KEY);

        requestImpl = new RequestImpl(request, attr.get());

        // The response object is created by the MiddlewareChannelHandler.
        responseImpl = attr.get().getResponse();

        // Returns false when no route matched the request path
        // and method.
        if (!findAndCallRoute(requestImpl, responseImpl)) {
            // Send 404 to the client because no route or static
            // resource matched the request.
            sendNotFound(ctx, responseImpl);
        }

        responseImpl.invokeResponseListeners(requestImpl);

        ctx.write(responseImpl.fullHttpReponse());

        if (!isKeepAlive(request)) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * Try to handle the exception by a custom exception handler.
     *
     * @param request  the current request
     * @param response the current response
     * @param route    the route
     * @param e        the exception thrown by the route
     */
    private void handleCustomException(final Request request, final ResponseImpl response, final Route route,
                                       final Exception e) {
        final ExceptionHandlerEntry entry = getExceptionHandlerEntryForException(e);

        if (entry != null) {
            Logger.debug("Invoked custom exception handler {0} for exception {1}", entry.toString(), e.getClass()
                    .getName());

            requestImpl.setRoute(route);
            entry.invokeHandler(request, response);
            final Object model = responseImpl.getContent();

            final String renderedModel = new PlainTextTransformer().render(model);

            Logger.debug("Rendered model {0}", renderedModel);

            response.fullHttpReponse().content().writeBytes(renderedModel.getBytes());
        } else {
            Logger.debug("No custom exception handler found for exception {0}", e.getClass().getName());
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve the {@link ExceptionHandlerEntry} for the given exception.
     *
     * @param e the exception the handler entry should match on
     * @return returns <code>null</code> if no handler entry matched the
     * exception
     */
    private ExceptionHandlerEntry getExceptionHandlerEntryForException(final Exception e) {
        for (ExceptionHandlerEntry exceptionHandlerEntry : base.getExceptionHandlerEntries()) {
            if (exceptionHandlerEntry.isInstanceOf(e)) {
                return exceptionHandlerEntry;
            }
        }
        return null;
    }

    /**
     * Find a matching route and invoke its handler.
     *
     * @param request  the current request
     * @param response the current response
     * @return returns <code>true</code> if a matching route was found,
     * otherwise <code>false</code>
     */
    private boolean findAndCallRoute(final RequestImpl request, final ResponseImpl response) {
        for (Route route : base.getRoutes()) {

            if (route.matchesPathAndMethod(getBasePath(request), request.method())) {
                Logger.debug("Found matching route {0}", route);

                request.setRoute(route);

                try {
                    route.handle(request, response);
                    setContentType(response);

                    if (!responseImpl.isRedirect()) {
                        String renderedModel = getRenderedModel(request, response);
                        response.fullHttpReponse().headers()
                                .set(HttpHeaders.Names.CONTENT_LENGTH, renderedModel.getBytes().length);
                        response.fullHttpReponse().content().writeBytes(renderedModel.getBytes());
                    } else {
                        response.fullHttpReponse().headers().set(HttpHeaders.Names.CONTENT_LENGTH, EMPTY_BYTES.length);
                        response.fullHttpReponse().content().writeBytes(EMPTY_BYTES);
                    }
                } catch (final Exception e) {
                    if (e instanceof HttpStatusException) {
                        handleHttpStatusException(response, e);
                    } else {
                        handleCustomException(request, response, route, e);
                    }
                }

                return true;
            }
        }

        return false;
    }

    private String getRenderedModel(final RequestImpl request, final ResponseImpl response) {
        String renderedModel;

        if (response.isTemplate()) {
            renderedModel = renderTemplate(response);
        } else {
            final Object model = response.getContent();

            Logger.debug("Route model created {0}", model);

            renderedModel = renderModel(response, model);

            if (response.isJsonp()) {
                final String callback = request.query("callback").isPresent() ? request.query("callback").get() : "";
                renderedModel = callback + "(" + renderedModel + ");";
            }

            Logger.debug("Rendered model {0}", renderedModel);
        }
        return renderedModel;
    }

    private void setContentType(final ResponseImpl response) {
        // java.lang.NullPointerException: Header values cannot be
        // null
        if (response.type() != null) {
            response.fullHttpReponse().headers().set(HttpHeaders.Names.CONTENT_TYPE, response.type());
        } else {
            response.fullHttpReponse().headers().set(HttpHeaders.Names.CONTENT_TYPE, ContentType.TEXT_HTML.type());
        }
    }

    private String getBasePath(final RequestImpl request) {
        return request.path().contains("?") ? request.path().substring(0, request.path().indexOf("?"))
                : request.path();
    }

    private String renderTemplate(final ResponseImpl response) {
        String renderedModel;
        final String ext = response.getTemplate().contains(".") ? response.getTemplate().substring(
                response.getTemplate().indexOf(".") + 1) : null;
        if (ext == null) {
            throw new RuntimeException("template file must contain a file extension");
        }
        final TemplateEngine templateEngine = base.getTemplateEngines().get(ext);
        if (templateEngine == null) {
            throw new RuntimeException("no template engine registered for extension " + ext);
        }
        renderedModel = templateEngine.render(response.getTemplate().substring(0, response.getTemplate().indexOf(".")),
                response.getOptions());
        return renderedModel;
    }

    private String renderModel(final ResponseImpl response, final Object model) {
        ResponseTransformer responseTransformer = new PlainTextTransformer();

        switch (response.type()) {
            case APPLICATION_JSON:
                if (base.getResponseTransformerMap().get(ContentType.APPLICATION_JSON) != null) {
                    responseTransformer = base.getResponseTransformerMap().get(ContentType.APPLICATION_JSON);
                }
                break;
            case TEXT_HTML:
                if (base.getResponseTransformerMap().get(ContentType.TEXT_HTML) != null) {
                    responseTransformer = base.getResponseTransformerMap().get(ContentType.TEXT_HTML);
                }
                break;
            default:
                break;
        }
        return responseTransformer.render(model);
    }

    private void sendNotFound(final ChannelHandlerContext ctx, final ResponseImpl responseImpl) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.copiedBuffer(
                HttpStatus.NOT_FOUND.getReasonPhrase() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8");

        responseImpl.status(HttpStatus.NOT_FOUND);

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void handleHttpStatusException(final ResponseImpl response, final Exception e) {
        HttpStatusException hse = (HttpStatusException) e;

        Logger.debug("Caught HTTP Status Exception {0}", hse.getStatus());

        response.status(HttpStatus.valueOf(hse.getStatus()));
        response.fullHttpReponse().headers().set(HttpHeaders.Names.CONTENT_TYPE, ContentType.TEXT_PLAIN.type());

        response.fullHttpReponse().content().writeBytes(hse.getMessage().getBytes());
    }
}
