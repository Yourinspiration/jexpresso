package staticresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;

/**
 * Abstract resource class.
 */
public class URLResource extends Resource {
    protected URL _url;
    protected String _urlString;

    protected URLConnection _connection;
    protected InputStream _in = null;

    protected URLResource(final URL url, final URLConnection connection) {
        _url = url;
        _urlString = _url.toString();
        _connection = connection;
    }

    protected synchronized boolean checkConnection() {
        if (_connection == null) {
            try {
                _connection = _url.openConnection();
            } catch (IOException e) {

            }
        }
        return _connection != null;
    }

    @Override
    public synchronized void release() {
        if (_in != null) {
            try {
                _in.close();
            } catch (IOException e) {

            }
            _in = null;
        }

        if (_connection != null)
            _connection = null;
    }

    @Override
    public boolean exists() {
        try {
            synchronized (this) {
                if (checkConnection() && _in == null)
                    _in = _connection.getInputStream();
            }
        } catch (IOException e) {

        }
        return _in != null;
    }

    @Override
    public boolean isDirectory() {
        return exists() && _url.toString().endsWith("/");
    }

    @Override
    public long lastModified() {
        if (checkConnection())
            return _connection.getLastModified();
        return -1;
    }

    @Override
    public long length() {
        if (checkConnection())
            return _connection.getContentLength();
        return -1;
    }

    @Override
    public URL getURL() {
        return _url;
    }

    @Override
    public File getFile() throws IOException {
        // Try the permission hack
        if (checkConnection()) {
            Permission perm = _connection.getPermission();
            if (perm instanceof java.io.FilePermission)
                return new File(perm.getName());
        }

        // Try the URL file arg
        try {
            return new File(_url.getFile());
        } catch (Exception e) {

        }

        // Don't know the file
        return null;
    }

    @Override
    public String getName() {
        return _url.toExternalForm();
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (!checkConnection())
            throw new IOException("Invalid resource");

        try {
            if (_in != null) {
                InputStream in = _in;
                _in = null;
                return in;
            }
            return _connection.getInputStream();
        } finally {
            _connection = null;
        }
    }

    @Override
    public String toString() {
        return _urlString;
    }

    @Override
    public int hashCode() {
        return _urlString.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof URLResource && _urlString.equals(((URLResource) o)._urlString);
    }

}
