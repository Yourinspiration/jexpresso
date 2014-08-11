package de.yourinspiration.jexpresso.staticresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;

/**
 * File Resource.
 * <p/>
 * Handle resources of implied or explicit file type. This class can check for
 * aliasing in the filesystem (eg case insensitivity).
 */
public class FileResource extends URLResource {

    private File _file;

    protected FileResource(final URL url) throws IOException, URISyntaxException {
        super(url, null);

        try {
            // Try standard API to convert URL to file.
            _file = new File(new URI(url.toString()));
        } catch (URISyntaxException e) {
            throw e;
        } catch (Exception e) {
            try {
                // Assume that File.toURL produced unencoded chars. So try
                // encoding them.
                String file_url = "file:" + URIUtil.encodePath(url.toString().substring(5));
                URI uri = new URI(file_url);
                if (uri.getAuthority() == null)
                    _file = new File(uri);
                else
                    _file = new File("//" + uri.getAuthority() + URIUtil.decodePath(url.getFile()));
            } catch (Exception e2) {

                // Still can't get the file. Doh! try good old hack!
                checkConnection();
                Permission perm = _connection.getPermission();
                _file = new File(perm == null ? url.getFile() : perm.getName());
            }
        }
        if (_file.isDirectory()) {
            if (!_urlString.endsWith("/"))
                _urlString = _urlString + "/";
        } else {
            if (_urlString.endsWith("/"))
                _urlString = _urlString.substring(0, _urlString.length() - 1);
        }

    }

    protected FileResource(URL url, URLConnection connection, File file) {
        super(url, connection);
        _file = file;
        if (_file.isDirectory() && !_urlString.endsWith("/"))
            _urlString = _urlString + "/";
    }

    @Override
    public boolean exists() {
        return _file.exists();
    }

    @Override
    public long lastModified() {
        return _file.lastModified();
    }

    @Override
    public boolean isDirectory() {
        return _file.isDirectory();
    }

    @Override
    public long length() {
        return _file.length();
    }

    @Override
    public String getName() {
        return _file.getAbsolutePath();
    }

    @Override
    public File getFile() {
        return _file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(_file);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof FileResource))
            return false;

        FileResource f = (FileResource) o;
        return f._file == _file || null != _file && _file.equals(f._file);
    }

    @Override
    public int hashCode() {
        return null == _file ? super.hashCode() : _file.hashCode();
    }

}
