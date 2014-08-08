package de.yourinspiration.jexpresso;

import de.yourinspiration.jexpresso.exception.ForbiddenException;
import de.yourinspiration.jexpresso.exception.NotFoundException;
import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.http.HttpStatus;
import io.netty.handler.codec.http.*;
import org.apache.commons.io.IOUtils;
import staticresources.Resource;

import javax.activation.MimetypesFileTypeMap;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implementation for {@link Response}.
 * 
 * @author Marcel Härle
 *
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

    protected ResponseImpl(final FullHttpResponse fullHttpResponse) {
        this.fullHttpResponse = fullHttpResponse;
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
        fullHttpResponse.setStatus(HttpResponseStatus.valueOf(status.value()));
    }

    @Override
    public HttpStatus status() {
        return HttpStatus.valueOf(fullHttpResponse.getStatus().code());
    }

    @Override
    public void set(final String field, final String value) {
        fullHttpResponse.headers().set(field, value);
    }

    @Override
    public void set(final Map<String, String> fields) {
        for (Entry<String, String> entry : fields.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String get(final String field) {
        return fullHttpResponse.headers().get(field);
    }

    @Override
    public void cookie(final Cookie cookie) {
        fullHttpResponse.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Override
    public void clearCookie(final String name) {
        final Cookie cookie = new DefaultCookie(name, "");
        cookie.setMaxAge(0);
        fullHttpResponse.headers().add(HttpHeaders.Names.SET_COOKIE, ServerCookieEncoder.encode(cookie));
    }

    @Override
    public void redirect(final String url) {
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
        this.content = content;
        if (content instanceof String) {
            type(ContentType.TEXT_HTML.type());
        } else {
            type(ContentType.APPLICATION_JSON.type());
        }
    }

    @Override
    public void send(final HttpStatus status, final Object content) {
        status(status);
        this.content = content;
        if (content instanceof String) {
            type(ContentType.TEXT_HTML.type());
        } else {
            type(ContentType.APPLICATION_JSON.type());
        }
    }

    @Override
    public void send(final byte[] content) {
        this.bytes = content;
        this.isBinary = true;
        type(ContentType.APPLICATION_OCTETSTREAM.type());
    }

    @Override
    public void send(final HttpStatus status, final byte[] content) {
        status(status);
        send(content);
    }

    @Override
    public void send(final HttpStatus status) {
        status(status);
        send(status.getReasonPhrase());
    }

    @Override
    public void json(final Object content) {
        this.content = content;
        type(ContentType.APPLICATION_JSON.type());
    }

    @Override
    public void json(final HttpStatus status, final Object content) {
        status(status);
        json(content);
    }

    @Override
    public void jsonp(final Object content) {
        isJsonp = true;
        json(content);
    }

    @Override
    public void jsonp(final HttpStatus status, final Object content) {
        status(status);
        jsonp(content);
    }

    @Override
    public void type(final String type) {
        fullHttpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, type);
    }

    @Override
    public String type() {
        return fullHttpResponse.headers().get(HttpHeaders.Names.CONTENT_TYPE);
    }

    @Override
    public void render(final String view, final Options options) {
        render(view, options.create());
    }

    @Override
    public void render(final String template, final Map<String, Object> options) {
        type(ContentType.TEXT_HTML.type());
        this.template = template;
        this.options = options;
        this.isTemplate = true;
    }

    @Override
    public void addListener(ResponseListener listener) {
        responseListener.add(listener);
    }

    @Override
    public void sendFile(final String filename) {
        final URL fileUrl = getClass().getResource("/" + filename);

        Resource fileResource;
        try {
            fileResource = Resource.newResource(fileUrl);
        } catch (IOException e) {
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

    private String getContentType(final String path) {
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

        return mimeTypesMap.getContentType(path);
    }

}
