package de.yourinspiration.jexpresso.staticresources;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;

/**
 * A resource inside a JAR archive.
 *
 * @author Marcel Härle
 */
public class JarResource extends URLResource {

    protected JarURLConnection _jarConnection;

    protected JarResource(final URL url) {
        super(url, null);
    }

    @Override
    public synchronized void release() {
        _jarConnection = null;
        super.release();
    }

    @Override
    protected synchronized boolean checkConnection() {
        super.checkConnection();
        try {
            if (_jarConnection != _connection)
                newConnection();
        } catch (IOException e) {
            _jarConnection = null;
        }

        return _jarConnection != null;
    }

    /**
     * @throws java.io.IOException Sub-classes of <code>JarResource</code> may throw an
     *                             IOException (or subclass)
     */
    protected void newConnection() throws IOException {
        _jarConnection = (JarURLConnection) _connection;
    }

    @Override
    public boolean exists() {
        if (_urlString.endsWith("!/"))
            return checkConnection();
        else
            return super.exists();
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        checkConnection();
        if (!_urlString.endsWith("!/"))
            return new FilterInputStream(super.getInputStream()) {
                @Override
                public void close() throws IOException {
                    this.in = new ClosedIS();
                }
            };

        URL url = new URL(_urlString.substring(4, _urlString.length() - 2));
        return url.openStream();
    }

    private static class ClosedIS extends InputStream {
        @Override
        public int read() throws IOException {
            return -1;
        }
    }

}
