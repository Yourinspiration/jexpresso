package staticresources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import de.yourinspiration.jexpresso.MiddlewareHandler;
import de.yourinspiration.jexpresso.Next;
import de.yourinspiration.jexpresso.Request;
import de.yourinspiration.jexpresso.Response;
import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.http.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.pmw.tinylog.Logger;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

/**
 * Middleware component to service static resources for JExpresso applications.
 *
 * @author Marcel Härle
 */
public class StaticResources implements MiddlewareHandler {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private final String staticResources;
    private final boolean useFileCache;
    private final LoadingCache<String, FileCacheEntry> fileCache = CacheBuilder.newBuilder().build(
            new FileCacheLoader());

    /**
     * Constructs a new object.
     *
     * @param staticResources the path to the folder containing the static resources
     * @param useFileCache    whether using a file cache
     */
    public StaticResources(final String staticResources, final boolean useFileCache) {
        this.staticResources = staticResources;
        this.useFileCache = useFileCache;
    }

    @Override
    public void handle(final Request request, final Response response, final Next next) {
        final String uri = getUri(request);
        String path = sanitizeUri(uri);

        // If the path is not valid/secure it is set to null.
        if (path == null) {
            response.set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.status(HttpStatus.FORBIDDEN);
            next.cancel();
            return;
        }

        if (path.equals("") || path.endsWith("/")) {
            path += "index.html";
        }

        String resource;
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
                    response.set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                    response.status(HttpStatus.FORBIDDEN);
                    next.next();
                    return;
                }

                if (fileCacheEntry.isFound()) {
                    if (checkIfModified(request, fileCacheEntry.lastModified())) {
                        sendNotModified(response);
                        next.cancel();
                        return;
                    } else {
                        response.send(fileCacheEntry.getBytes());
                        response.set(CONTENT_LENGTH, "" + fileCacheEntry.getBytes().length);
                        setContentTypeHeader(response, fileCacheEntry.getPath());
                        setDateAndCacheHeaders(response, fileCacheEntry.lastModified());
                    }
                } else {
                    response.set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                    response.status(HttpStatus.NOT_FOUND);
                    next.cancel();
                    return;
                }
            } catch (ExecutionException e) {
                Logger.error("Error getting file from file cache: {0}", e.getMessage());
                response.set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                response.status(HttpStatus.NOT_FOUND);
                next.cancel();
                return;
            }
        } else {
            // Lookup the classpath for the requested resource.
            final URL fileUrl = getClass().getResource("/" + resource);

            // null will be returned when there is no such resource on the
            // classpath.
            if (fileUrl == null) {
                next.next();
                return;
            }

            Resource fileResource;
            try {
                fileResource = Resource.newResource(fileUrl);
            } catch (IOException e) {
                next.cancel();
                return;
            }

            if (!fileResource.exists()) {
                next.next();
                return;
            }

            if (fileResource.isDirectory()) {
                response.set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                response.status(HttpStatus.FORBIDDEN);
                next.cancel();
                return;
            }

            byte[] bytes = new byte[0];

            try (final InputStream fileInputStream = fileUrl.openStream()) {

                try {
                    bytes = IOUtils.toByteArray(fileInputStream);
                } catch (NullPointerException npe) {
                    response.set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                    response.status(HttpStatus.NOT_FOUND);
                    next.cancel();
                    return;
                } finally {
                    fileInputStream.close();
                }

                fileInputStream.close();
            } catch (IOException ioe) {
                response.set(CONTENT_TYPE, "text/plain; charset=UTF-8");
                response.status(HttpStatus.NOT_FOUND);
                next.cancel();
                return;
            }

            response.send(bytes);

            response.set(CONTENT_LENGTH, "" + bytes.length);
            setContentTypeHeader(response, resource);
            setDateAndCacheHeaders(response, System.currentTimeMillis());
        }

        next.cancel();
    }

    private String getUri(final Request request) {
        if (request.path().contains("?")) {
            return request.path().substring(0, request.path().indexOf("?"));
        } else {
            return request.path();
        }
    }

    private boolean checkIfModified(final Request request, final long lastModified) {
        String ifModifiedSince = request.get(IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
            Date ifModifiedSinceDate;
            try {
                ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);
            } catch (ParseException e) {
                return false;
            }
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            long fileLastModifiedSeconds = lastModified / 1000;
            if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
                return true;
            }
        }
        return false;
    }

    private String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (!uri.startsWith("/")) {
            return null;
        }

        if (uri.contains(File.separator + '.') || uri.contains('.' + File.separator) || uri.startsWith(".")
                || uri.endsWith(".") || INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        return uri.substring(1);
    }

    /**
     * When file timestamp is the same as what the browser is sending up, send a
     * "304 Not Modified"
     *
     * @param response the response
     */
    private void sendNotModified(final Response response) {
        setDateHeader(response);
        response.type(ContentType.TEXT_HTML.type());
        response.status(HttpStatus.NOT_MODIFIED);
    }

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response HTTP response
     */
    private void setDateHeader(Response response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.set(DATE, dateFormatter.format(time.getTime()));
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response     HTTP response
     * @param lastModified modified time of fileToCache last modified time of file to
     *                     extract content type
     */
    private void setDateAndCacheHeaders(final Response response, final long lastModified) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        // Date header
        Calendar time = new GregorianCalendar();
        response.set(DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        time.add(Calendar.SECOND, HTTP_CACHE_SECONDS);
        response.set(EXPIRES, dateFormatter.format(time.getTime()));
        response.set(CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.set(LAST_MODIFIED, dateFormatter.format(new Date(lastModified)));
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response HTTP response
     * @param file     file to extract content type
     */
    private void setContentTypeHeader(final Response response, final String file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        mimeTypesMap.addMimeTypes("image/png png PNG");
        mimeTypesMap.addMimeTypes("image/gif gif GIF");
        mimeTypesMap.addMimeTypes("image/jpeg jpeg JPEG jpg JPG");
        mimeTypesMap.addMimeTypes("image/tiff tiff TIFF");
        mimeTypesMap.addMimeTypes("text/javascript js JS");
        mimeTypesMap.addMimeTypes("application/json json JSON");
        mimeTypesMap.addMimeTypes("text/css css CSS");

        mimeTypesMap.addMimeTypes("application/ogg ogg OGG");
        mimeTypesMap.addMimeTypes("application/pdf pdf PDF");
        mimeTypesMap.addMimeTypes("application/postscript ps PS");
        mimeTypesMap.addMimeTypes("application/xml xml XML");
        mimeTypesMap.addMimeTypes("application/zip zip ZIP");
        mimeTypesMap.addMimeTypes("application/gzip gzip GZIP");

        mimeTypesMap.addMimeTypes("audio/mp4 mp4 MP4");
        mimeTypesMap.addMimeTypes("audio/mpeg mpeg mp3");

        Logger.debug("Resolved Content-Type {0}", mimeTypesMap.getContentType(file));

        response.set(CONTENT_TYPE, mimeTypesMap.getContentType(file));
    }

}
