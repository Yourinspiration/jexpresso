package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpHeaders.Names.CACHE_CONTROL;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.DATE;
import static io.netty.handler.codec.http.HttpHeaders.Names.EXPIRES;
import static io.netty.handler.codec.http.HttpHeaders.Names.IF_MODIFIED_SINCE;
import static io.netty.handler.codec.http.HttpHeaders.Names.LAST_MODIFIED;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_MODIFIED;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.Attribute;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.resource.Resource;
import org.pmw.tinylog.Logger;

import com.google.common.cache.LoadingCache;

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
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private final List<Route> routes;
    private final List<ExceptionHandlerEntry> exceptionHandlerEntries;
    private final String staticResources;
    private final LoadingCache<String, FileCacheEntry> fileCache;
    private final boolean useFileCache;
    private final Map<String, TemplateEngine> templateEngines;

    private RequestImpl requestImpl;
    private ResponseImpl responseImpl;

    protected HttpJExpressoServerHandler(final List<Route> routes,
            final List<ExceptionHandlerEntry> exceptionHandlerEntries, final String staticResources,
            final LoadingCache<String, FileCacheEntry> fileCache, final boolean useFileCache,
            final Map<String, TemplateEngine> templateEngines) {
        this.routes = routes;
        this.exceptionHandlerEntries = exceptionHandlerEntries;
        this.staticResources = staticResources;
        this.fileCache = fileCache;
        this.useFileCache = useFileCache;
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
            // Returns true when the path matched a static resource.
            if (!sendStaticFile(ctx, request, response)) {
                // Returns false when no route matched the request path
                // and method.
                if (!findAndCallRoute(requestImpl, responseImpl)) {
                    // Send 404 to the client because no route or static
                    // resource matched the request.
                    sendError(ctx, NOT_FOUND);
                }
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

    /**
     * Try to find a static resource and send it to the client.
     * 
     * @param ctx
     *            the current context
     * @param request
     *            the current request
     * @param response
     *            the current response
     * @return returns <code>true</code> if a static resource was found and
     *         sent, otherwise <code>false</code>
     * @throws IOException
     * @throws ParseException
     * @throws URISyntaxException
     */
    private boolean sendStaticFile(final ChannelHandlerContext ctx, final FullHttpRequest request,
            final FullHttpResponse response) throws IOException, ParseException, URISyntaxException {
        // Files can only by requested by a HTTP GET request.
        if (!request.getMethod().equals(GET)) {
            return false;
        }

        final String uri = request.getUri();
        String path = sanitizeUri(uri);

        // If the path is not valid/secure it is set to null.
        if (path == null) {
            sendError(ctx, FORBIDDEN);
            return true;
        }

        if (path.equals("") || path.endsWith("/")) {
            path += "index.html";
        }

        String resource = "";
        if (staticResources.equals("")) {
            resource = path;
        } else if (staticResources.endsWith("/")) {
            resource = staticResources + path;
        } else {
            resource = staticResources + "/" + path;
        }

        if (useFileCache) {
            try {
                FileCacheEntry fileCacheEntry = fileCache.get(resource);

                if (fileCacheEntry.isDirectory()) {
                    sendError(ctx, FORBIDDEN);
                    return true;
                }

                if (fileCacheEntry.isFound()) {
                    if (checkIfModified(request, fileCacheEntry.lastModified())) {
                        sendNotModified(ctx);
                        return true;
                    } else {
                        setContentLength(response, fileCacheEntry.getBytes().length);
                        setContentTypeHeader(response, fileCacheEntry.getPath());
                        setDateAndCacheHeaders(response, fileCacheEntry.lastModified());
                        if (isKeepAlive(request)) {
                            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                        }

                        response.content().writeBytes(fileCacheEntry.getBytes());
                    }
                } else {
                    return false;
                }
            } catch (ExecutionException e) {
                Logger.error("Error getting file from file cache: {0}", e.getMessage());
                return false;
            }
        } else {
            // Lookup the classpath for the requested resource.
            final URL fileUrl = getClass().getResource("/" + resource);

            // null will be returned when there is no such resource on the
            // classpath.
            if (fileUrl == null) {
                return false;
            }

            final Resource fileResource = Resource.newResource(fileUrl);

            if (!fileResource.exists()) {
                return false;
            }

            if (fileResource.isDirectory()) {
                sendError(ctx, FORBIDDEN);
                return true;
            }

            final InputStream fileInputStream = fileUrl.openStream();

            byte[] bytes;

            // Somehow there is a NPE when accessing a directory when compressed
            // to a JAR.
            try {
                bytes = IOUtils.toByteArray(fileInputStream);
            } catch (NullPointerException npe) {
                sendError(ctx, NOT_FOUND);
                return true;
            } finally {
                fileInputStream.close();
            }

            fileInputStream.close();

            setContentLength(response, bytes.length);
            setContentTypeHeader(response, resource);
            setDateAndCacheHeaders(response, System.currentTimeMillis());
            if (isKeepAlive(request)) {
                response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            response.content().writeBytes(bytes);
        }

        return true;
    }

    private boolean checkIfModified(final FullHttpRequest request, final long lastModified) throws ParseException {
        String ifModifiedSince = request.headers().get(IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = lastModified / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                return true;
            }
        }
        return false;
    }

    private String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (!uri.startsWith("/")) {
            return null;
        }

        // TODO Check path seriously!
        if (uri.contains(File.separator + '.') || uri.contains('.' + File.separator) || uri.startsWith(".")
                || uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        return uri.substring(1);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: "
                + status + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a
     * "304 Not Modified"
     *
     * @param ctx
     *            Context
     */
    private void sendNotModified(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);
        setDateHeader(response);

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response
     *            HTTP response
     */
    private void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param last
     *            modified time of fileToCache last modified time of file to
     *            extract content type
     */
    private void setDateAndCacheHeaders(final HttpResponse response, final long lastModified) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.headers().set(EXPIRES, dateFormatter.format(time.getTime()));
        response.headers().set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(LAST_MODIFIED, dateFormatter.format(new Date(lastModified)));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response
     *            HTTP response
     * @param file
     *            file to extract content type
     */
    private void setContentTypeHeader(final HttpResponse response, final String path) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE, mimeTypesMap.getContentType(path));
    }

    private void handleHttpStatusException(final ResponseImpl response, final Exception e) throws IOException {
        HttpStatusException hse = (HttpStatusException) e;

        Logger.debug("Caught HTTP Status Exception {0}", hse.getStatus());

        response.status(HttpStatus.valueOf(hse.getStatus()));
        response.fullHttpReponse().headers().set(CONTENT_TYPE, ContentType.TEXT_PLAIN.type());

        response.fullHttpReponse().content().writeBytes(hse.getMessage().getBytes());
    }
}
