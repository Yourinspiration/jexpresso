package de.yourinspiration.jexpresso.middleware.accesslog;

import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;
import de.yourinspiration.jexpresso.core.ResponseListener;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.text.DateFormat;
import java.util.Date;

/**
 * Implementation for {@link de.yourinspiration.jexpresso.core.ResponseListener} to create the access log message
 * after the response has been created.
 *
 * @author Marcel Härle
 */
public class AccessLogResponseListener implements ResponseListener {

    private static final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

    private final AccessLogFormat format;
    private final WritableByteChannel channel;
    private final long start = System.currentTimeMillis();

    /**
     * Constructs a new object.
     *
     * @param format  the format
     * @param channel the output channel
     */
    protected AccessLogResponseListener(final AccessLogFormat format, final WritableByteChannel channel) {
        this.format = format;
        this.channel = channel;
    }

    @Override
    public void callback(final Request request, final Response response) {
        try {
            channel.write(ByteBuffer.wrap(getMessage(request, response).toString().getBytes()));
        } catch (IOException e) {
            Logger.error(e, "Error writing access log message: {0}", e.getMessage());
        }
    }

    private String getMessage(final Request request, final Response response) {
        String message = df.format(new Date()) + ": ";

        switch (format) {
            case DEVELOPER:
                message += getDeveloperMessage(request, response);
                break;
            case SMALL:
                message += getSmallMessage(request, response);
                break;
            case TINY:
                message += getTinyMessage(request);
        }
        return message;
    }

    private long getDuration() {
        return System.currentTimeMillis() - start;
    }

    private String getDeveloperMessage(final Request request, final Response response) {
        return request.method() + " " + request.path() + " " + request.ip() + " " + " " + response.status().value()
                + " " + response.status().getReasonPhrase() + " " + getDuration() + "ms\n";
    }

    private String getSmallMessage(final Request request, final Response response) {
        return request.method() + " " + request.path() + " " + request.ip() + " " + " " + response.status().value()
                + "\n";
    }

    private String getTinyMessage(final Request request) {
        return request.method() + " " + request.path() + "\n";
    }

}
