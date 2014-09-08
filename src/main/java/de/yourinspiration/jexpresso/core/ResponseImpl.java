package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.exception.ForbiddenException;
import de.yourinspiration.jexpresso.exception.NotFoundException;
import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.http.HttpStatus;
import de.yourinspiration.jexpresso.staticresources.Resource;
import io.netty.handler.codec.http.*;
import org.apache.commons.io.IOUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * Implementation for {@link Response}.
 *
 * @author Marcel Härle
 */
public class ResponseImpl implements Response {

    private final FullHttpResponse fullHttpResponse;
    private final List<ResponseListener> responseListener = new ArrayList<>();
    private Object content;
    private byte[] bytes;
    private boolean isBinary = false;
    private Map<String, Object> options;
    private String template;
    private boolean isTemplate = false;
    private boolean isRedirect = false;
    private boolean isJsonp = false;
    private String jsonpCallback = "";

    protected ResponseImpl(final FullHttpResponse fullHttpResponse) {
        this.fullHttpResponse = fullHttpResponse;
        // Set default status and content type.
        status(HttpStatus.OK);
        type(ContentType.TEXT_HTML);
    }

    protected Object getContent() {
        return content;
    }

    protected byte[] getBytes() {
        return bytes;
    }

    protected String getTemplate() {
        return template;
    }

    protected Map<String, Object> getOptions() {
        return options;
    }

    protected boolean isTemplate() {
        return isTemplate;
    }

    protected boolean isBinary() {
        return isBinary;
    }

    protected FullHttpResponse fullHttpReponse() {
        return fullHttpResponse;
    }

    protected boolean isRedirect() {
        return isRedirect;
    }

    protected boolean isJsonp() {
        return isJsonp;
    }

    protected String getJsonpCallback() {
        return jsonpCallback;
    }

    protected void invokeResponseListeners(final Request request) {
        for (ResponseListener listener : responseListener) {
            listener.callback(request, this);
        }
    }

    // ========================================================
    // API ====================================================
    // ========================================================

    @Override
    public void status(final HttpStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        fullHttpResponse.setStatus(HttpResponseStatus.valueOf(status.value()));
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.valueOf(fullHttpResponse.getStatus().code());
    }

    @Override
    public void set(final String field, final String value) {
        if (field == null || value == null) {
            throw new IllegalArgumentException("field and value must not be null");
        }
        fullHttpResponse.headers().set(field, value);
    }

    @Override
    public void set(final Map<String, String> fields) {
        for (Entry<String, String> entry : fields.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Optional<String> get(final String field) {
        if (fullHttpResponse.headers().contains(field)) {
            return Optional.ofNullable(fullHttpResponse.headers().get(field));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void cookie(final Cookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("cookie must not be null");
        }
        // TODO Does this overwrite existing other cookies?
        fullHttpResponse.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Override
    public void clearCookie(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("nam must ot be null");
        }
        final Cookie cookie = new DefaultCookie(name, "");
        cookie.setMaxAge(0);
        fullHttpResponse.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Override
    public void redirect(final String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url must not be null or empty");
        }
        isRedirect = true;
        set(HttpHeaders.Names.LOCATION, url);
        status(HttpStatus.TEMPORARY_REDIRECT);
    }

    @Override
    public void location(final String location) {
        set(HttpHeaders.Names.LOCATION, location);
    }

    @Override
    public void send(final Object content) {
        send(HttpStatus.OK, content);
    }

    @Override
    public void send(final HttpStatus status, final Object content) {
        status(status);
        this.content = content;
    }

    @Override
    public void send(final byte[] content) {
        send(HttpStatus.OK, content);
    }

    @Override
    public void send(final HttpStatus status, final byte[] content) {
        status(status);
        this.bytes = content;
        this.isBinary = true;
        type(ContentType.APPLICATION_OCTETSTREAM);
    }

    @Override
    public void send(final HttpStatus status) {
        status(status);
        send(status.getReasonPhrase());
        type(ContentType.TEXT_PLAIN);
    }

    @Override
    public void json(final Object content) {
        this.content = content;
        type(ContentType.APPLICATION_JSON);
    }

    @Override
    public void json(final HttpStatus status, final Object content) {
        status(status);
        json(content);
    }

    @Override
    public void jsonp(final Object content, final String callback) {
        jsonp(HttpStatus.OK, content, callback);
    }

    @Override
    public void jsonp(final HttpStatus status, final Object content, final String callback) {
        isJsonp = true;
        jsonpCallback = callback;
        json(status, content);
    }

    @Override
    public void type(final ContentType type) {
        fullHttpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, type.type());
    }

    @Override
    public ContentType type() {
        final String contentTypeString = fullHttpResponse.headers().get(HttpHeaders.Names.CONTENT_TYPE);
        try {
            return ContentType.valueOf(contentTypeString.replaceAll("/", "_").toUpperCase());
        } catch (IllegalArgumentException e) {
            // If the content type is unkown, than use application/octetstream as a fallback
            return ContentType.APPLICATION_OCTETSTREAM;
        }
    }

    @Override
    public void render(final String view, final Options options) {
        render(view, options.create());
    }

    @Override
    public void render(final String template, final Map<String, Object> options) {
        this.template = template;
        this.options = options;
        this.isTemplate = true;
    }

    @Override
    public void addListener(final ResponseListener listener) {
        responseListener.add(listener);
    }

    @Override
    public void sendFile(final String filename) {
        final URL fileUrl = getClass().getResource("/" + filename);

        Resource fileResource;
        try {
            fileResource = Resource.newResource(fileUrl);
        } catch (Exception e) {
            throw new NotFoundException();
        }

        if (!fileResource.exists()) {
            throw new NotFoundException();
        }

        if (fileResource.isDirectory()) {
            throw new ForbiddenException();
        }

        byte[] bytes = new byte[0];

        try (final InputStream fileInputStream = fileUrl.openStream()) {
            try {
                bytes = IOUtils.toByteArray(fileInputStream);
            } catch (NullPointerException npe) {
                throw new NotFoundException();
            } finally {
                fileInputStream.close();
            }

            fileInputStream.close();
        } catch (IOException ioe) {
            throw new NotFoundException();
        }

        send(bytes);
        type(getContentType(filename));
    }

    private ContentType getContentType(final String path) {
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

        return ContentType.valueOf(mimeTypesMap.getContentType(path));
    }

}
