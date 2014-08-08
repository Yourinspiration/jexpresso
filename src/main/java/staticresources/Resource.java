package staticresources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Abstract resource class.
 */
public abstract class Resource {

    private static final String JAR_RESOURCE_PREFIX = "jar:";
    private static final String JAR_FILE_RESOURCE_PREFIX = "jar:file:";
    private static final String FILE_RESOURCE_PREFIX = "file:";

    /**
     * Construct a resource from a url.
     *
     * @param url A URL.
     * @return A Resource object.
     * @throws java.io.IOException Problem accessing URL
     */
    public static Resource newResource(final URL url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("url must not be null");
        }

        final String url_string = url.toExternalForm();

        if (url_string.startsWith(FILE_RESOURCE_PREFIX)) {
            try {
                FileResource fileResource = new FileResource(url);
                return fileResource;
            } catch (Exception e) {
                return new BadResource(url, e.toString());
            }
        } else if (url_string.startsWith(JAR_FILE_RESOURCE_PREFIX)) {
            return new JarFileResource(url);
        } else if (url_string.startsWith(JAR_RESOURCE_PREFIX)) {
            return new JarResource(url);
        }

        return new URLResource(url, null);
    }

    /**
     * Construct a resource from a string.
     *
     * @param resource A URL or filename.
     * @return A Resource object.
     * @throws java.io.IOException Problem accessing URL
     */
    public static Resource newResource(String resource) throws IOException {
        try {
            return newResource(new URL(resource));
        } catch (MalformedURLException urlException) {
            if (resource.startsWith(FILE_RESOURCE_PREFIX)) {
                try {
                    // It's a file.
                    if (resource.startsWith("./"))
                        resource = resource.substring(2);

                    final File file = new File(resource).getCanonicalFile();
                    final URL url = file.toURI().toURL();

                    URLConnection connection = url.openConnection();
                    return new FileResource(url, connection, file);
                } catch (IOException ioException) {
                    return new BadResource(new URL(resource), ioException.toString());
                }
            } else {
                throw urlException;
            }
        }
    }

    @Override
    protected void finalize() {
        release();
    }

    /**
     * Release any temporary resources held by the resource.
     */
    public abstract void release();

    /**
     * Whether the represented resource exists.
     *
     * @return returns <code>true</code> if the represented resource exists,
     * otherwise <code>false</code>
     */
    public abstract boolean exists();

    /**
     * Whether the represented resource is a container/directory.
     *
     * @return returns <code>true</code> if the represented resource is a
     * container/directory, otherwise <code>false</code>
     */
    public abstract boolean isDirectory();

    /**
     * Returns the last modified time.
     *
     * @return the last modified time
     */
    public abstract long lastModified();

    /**
     * Return the length of the resource.
     *
     * @return the length of the resource
     */
    public abstract long length();

    /**
     * Returns an URL representing the given resource.
     *
     * @return URL representing the given resource
     */
    public abstract URL getURL();

    /**
     * Returns an File representing the given resource or NULL if this is not
     * possible.
     *
     * @return File representing the given resource or NULL if this is not
     * possible
     * @throws java.io.IOException if an exception occurs
     */
    public abstract File getFile() throws IOException;

    /**
     * Returns the name of the resource
     *
     * @return the name of the resource
     */
    public abstract String getName();

    /**
     * Returns an input stream to the resource
     *
     * @return input stream to the resource
     * @throws java.io.IOException if an exception occurs
     */
    public abstract InputStream getInputStream() throws IOException;

}
