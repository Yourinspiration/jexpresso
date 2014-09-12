package de.yourinspiration.jexpresso.middleware.accesslog;

import de.yourinspiration.jexpresso.core.MiddlewareHandler;
import de.yourinspiration.jexpresso.core.Next;
import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;

import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * A middleware for JExpresso to print the access log.
 *
 * @author Marcel Härle
 */
public class AccessLog implements MiddlewareHandler {

    private final AccessLogFormat format;
    private final WritableByteChannel channel;

    /**
     * Constructs a new access log middleware using the provided log format and
     * output stream.
     *
     * @param format the access log format
     * @param out    the output stream
     */
    public AccessLog(final AccessLogFormat format, final OutputStream out) {
        this.format = format;
        this.channel = Channels.newChannel(out);
    }

    @Override
    public void handle(final Request request, final Response response, final Next next) {
        response.addListener(new AccessLogResponseListener(format, channel));
        next.next();
    }

}
