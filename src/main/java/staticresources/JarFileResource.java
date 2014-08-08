package staticresources;

import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * File resource inside a JAR.
 *
 * @author Marcel Härle
 */
class JarFileResource extends JarResource {

    private JarFile _jarFile;
    private File _file;
    private JarEntry _entry;
    private boolean _directory;
    private String _jarUrl;
    private String _path;
    private boolean _exists;

    protected JarFileResource(final URL url) {
        super(url);
    }

    @Override
    public synchronized void release() {
        _entry = null;
        _file = null;
        if (_jarFile != null) {
            try {
                _jarFile.close();
            } catch (IOException ioe) {
                Logger.warn("Error closing jar file");
            }
        }
        _jarFile = null;
        super.release();
    }

    @Override
    protected synchronized boolean checkConnection() {
        try {
            super.checkConnection();
        } finally {
            if (_jarConnection == null) {
                _entry = null;
                _file = null;
                _jarFile = null;
            }
        }
        return _jarFile != null;
    }

    @Override
    protected synchronized void newConnection() throws IOException {
        super.newConnection();

        _entry = null;
        _file = null;
        _jarFile = null;

        int sep = _urlString.indexOf("!/");
        _jarUrl = _urlString.substring(0, sep + 2);
        _path = _urlString.substring(sep + 2);
        if (_path.length() == 0)
            _path = null;
        _jarFile = _jarConnection.getJarFile();
        _file = new File(_jarFile.getName());
    }

    @Override
    public boolean exists() {
        if (_exists)
            return true;

        if (_urlString.endsWith("!/")) {

            String file_url = _urlString.substring(4, _urlString.length() - 2);
            try {
                return newResource(file_url).exists();
            } catch (Exception e) {
                return false;
            }
        }

        boolean check = checkConnection();

        // Is this a root URL?
        if (_jarUrl != null && _path == null) {
            // Then if it exists it is a directory
            _directory = check;
            return true;
        } else {
            // Can we find a file for it?
            JarFile jarFile = null;
            if (check)
                // Yes
                jarFile = _jarFile;
            else {
                // No - so lets look if the root entry exists.
                try {
                    JarURLConnection c = (JarURLConnection) (new URL(_jarUrl)).openConnection();
                    jarFile = c.getJarFile();
                } catch (Exception e) {
                    Logger.warn(e, "Error open connection");
                }
            }

            // Do we need to look more closely?
            if (jarFile != null && _entry == null && !_directory) {
                // OK - we have a JarFile, lets look at the entries for our path
                Enumeration<JarEntry> e = jarFile.entries();
                while (e.hasMoreElements()) {
                    JarEntry entry = e.nextElement();
                    String name = entry.getName().replace('\\', '/');

                    // Do we have a match
                    if (name.equals(_path)) {
                        _entry = entry;
                        // Is the match a directory
                        _directory = _path.endsWith("/");
                        break;
                    } else if (_path.endsWith("/")) {
                        if (name.startsWith(_path)) {
                            _directory = true;
                            break;
                        }
                    } else if (name.startsWith(_path) && name.length() > _path.length()
                            && name.charAt(_path.length()) == '/') {
                        _directory = true;
                        break;
                    }
                }

                if (_directory && !_urlString.endsWith("/")) {
                    _urlString += "/";
                    try {
                        _url = new URL(_urlString);
                    } catch (MalformedURLException ex) {
                        Logger.warn(ex, "Error creating URL");
                    }
                }
            }
        }

        _exists = _directory || _entry != null;
        return _exists;
    }

    @Override
    public boolean isDirectory() {
        return _urlString.endsWith("/") || exists() && _directory;
    }

    @Override
    public long lastModified() {
        if (checkConnection() && _file != null) {
            if (exists() && _entry != null)
                return _entry.getTime();
            return _file.lastModified();
        }
        return -1;
    }

    @Override
    public long length() {
        if (isDirectory())
            return -1;

        if (_entry != null)
            return _entry.getSize();

        return -1;
    }

}
