package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.Attribute;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.pmw.tinylog.Logger;

import de.yourinspiration.jexpresso.exception.ExceptionHandlerEntry;
import de.yourinspiration.jexpresso.exception.HttpStatusException;
import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.http.HttpStatus;
import de.yourinspiration.jexpresso.transformer.HtmlTransformer;
import de.yourinspiration.jexpresso.transformer.JsonTransformer;
import de.yourinspiration.jexpresso.transformer.PlainTextTransformer;

/**
 * Handles the HTTP request.
 * 
 * @author Marcel Härle
 *
 */
public class HttpJExpressoServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;

    private final List<Route> routes;
    private final List<ExceptionHandlerEntry> exceptionHandlerEntries;
    private final Map<String, TemplateEngine> templateEngines;

    private RequestImpl requestImpl;
    private ResponseImpl responseImpl;

    protected HttpJExpressoServerHandler(final List<Route> routes,
            final List<ExceptionHandlerEntry> exceptionHandlerEntries, final Map<String, TemplateEngine> templateEngines) {
        this.routes = routes;
        this.exceptionHandlerEntries = exceptionHandlerEntries;
        this.templateEngines = templateEngines;
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        Attribute<RequestResponseContext> attr = ctx.channel().attr(RequestResponseContext.ATTR_KEY);

        requestImpl = new RequestImpl(request, attr.get());

        // The response object is created by the MiddlewareChannelHandler.
        responseImpl = attr.get().getResponse();

        final FullHttpResponse response = responseImpl.fullHttpReponse();

        try {
            // Returns false when no route matched the request path
            // and method.
            if (!findAndCallRoute(requestImpl, responseImpl)) {
                // Send 404 to the client because no route or static
                // resource matched the request.
                sendError(ctx, NOT_FOUND);
            }

            ctx.write(response);

            if (!isKeepAlive(request)) {
                ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        } catch (final Exception e) {
            Logger.error("Error processing HTTP content: {0}", e.getMessage());
            e.printStackTrace();
            sendError(ctx, INTERNAL_SERVER_ERROR);
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
     * @param request
     *            the current request
     * @param response
     *            the current response
     * @param route
     *            the route
     * @param e
     *            the exception thrown by the route
     * @throws IOException
     *             if an input or output exception occurred
     */
    private void handleCustomException(final Request request, final ResponseImpl response, final Route route,
            final Exception e) throws IOException {
        final ExceptionHandlerEntry entry = getExceptionHandlerEntryForException(e);

        if (entry != null) {
            Logger.debug("Invoked custom exception handler {0} for exception {1}", entry.toString(), e.getClass()
                    .getName());

            requestImpl.setRoute(route);
            entry.invokeHandler(request, response);
            final Object model = responseImpl.getContent();

            if (model != null) {
                final String renderedModel = new PlainTextTransformer().render(model);

                Logger.debug("Rendered model {0}", renderedModel);

                response.fullHttpReponse().content().writeBytes(renderedModel.getBytes());
            }
        } else {
            Logger.debug("No custom exception handler found for exception {0}", e.getClass().getName());
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve the {@link ExceptionHandlerEntry} for the given exception.
     * 
     * @param e
     *            the exception the handler entry should match on
     * @return returns <code>null</code> if no handler entry matched the
     *         exception
     */
    private ExceptionHandlerEntry getExceptionHandlerEntryForException(final Exception e) {
        for (ExceptionHandlerEntry exceptionHandlerEntry : exceptionHandlerEntries) {
            if (exceptionHandlerEntry.isInstanceOf(e)) {
                return exceptionHandlerEntry;
            }
        }
        return null;
    }

    /**
     * Find a matching route and invoke its handler.
     * 
     * @param request
     *            the current request
     * @param response
     *            the current response
     * @return returns <code>true</code> if a matching route was found,
     *         otherwise <code>false</code>
     * @throws IOException
     */
    private boolean findAndCallRoute(final RequestImpl request, final ResponseImpl response) throws IOException {
        final String path = request.path();
        final HttpMethod method = request.method();

        boolean routeFound = false;

        for (Route route : routes) {

            if (route.matchesPathAndMethod(path, method)) {
                Logger.debug("Found matching route {0}", route);

                request.setRoute(route);

                try {
                    route.handle(request, response);
                    response.fullHttpReponse().headers().set(CONTENT_TYPE, response.type());

                    String renderedModel;

                    if (response.isTemplate()) {
                        renderedModel = renderTemplate(response);
                    } else {
                        final Object model = response.getContent();

                        Logger.debug("Route model created {0}", model);

                        renderedModel = renderModel(response, model);

                        Logger.debug("Rendered model {0}", renderedModel);
                    }

                    response.fullHttpReponse().content().writeBytes(renderedModel.getBytes());
                } catch (Exception e) {
                    if (e instanceof HttpStatusException) {
                        handleHttpStatusException(response, e);
                    } else {
                        handleCustomException(request, response, route, e);
                    }
                }

                routeFound = true;

                break;
            }
        }

        return routeFound;
    }

    private String renderTemplate(final ResponseImpl response) {
        String renderedModel;
        final String ext = response.getTemplate().indexOf(".") > -1 ? response.getTemplate().substring(
                response.getTemplate().indexOf(".") + 1) : null;
        if (ext == null) {
            throw new RuntimeException("template file must contain a file extension");
        }
        final TemplateEngine templateEngine = templateEngines.get(ext);
        if (templateEngine == null) {
            throw new RuntimeException("no template engine registered for extension " + ext);
        }
        renderedModel = templateEngine.render(response.getTemplate().substring(0, response.getTemplate().indexOf(".")),
                response.getOptions());
        return renderedModel;
    }

    private String renderModel(final ResponseImpl response, final Object model) {
        String renderedModel;
        switch (response.type()) {
        case "application/json":
            renderedModel = new JsonTransformer().render(model);
            break;
        case "text/html":
            renderedModel = new HtmlTransformer().render(model);
            break;
        case "text/plain":
            renderedModel = new PlainTextTransformer().render(model);
            break;
        default:
            renderedModel = new PlainTextTransformer().render(model);
            break;
        }
        return renderedModel;
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: "
                + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void handleHttpStatusException(final ResponseImpl response, final Exception e) throws IOException {
        HttpStatusException hse = (HttpStatusException) e;

        Logger.debug("Caught HTTP Status Exception {0}", hse.getStatus());

        response.status(HttpStatus.valueOf(hse.getStatus()));
        response.fullHttpReponse().headers().set(CONTENT_TYPE, ContentType.TEXT_PLAIN.type());

        response.fullHttpReponse().content().writeBytes(hse.getMessage().getBytes());
    }
}
