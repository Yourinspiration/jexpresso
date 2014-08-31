package de.yourinspiration.jexpresso.staticresources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Bad Resource. A Resource that is returned for a bade URL. Acts as a resource that does not
 * exist and throws appropriate exceptions.
 */
class BadResource extends URLResource {
    private String _message = null;

    protected BadResource(final URL url, final String message) {
        super(url, null);
        _message = message;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public long lastModified() {
        return -1;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long length() {
        return -1;
    }

    @Override
    public File getFile() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new FileNotFoundException(_message);
    }

    @Override
    public String toString() {
        return super.toString() + "; BadResource=" + _message;
    }

}
