package staticresources;

/**
 * An instance of this class holds the data for a cached static file resource.
 *
 * @author Marcel Härle
 */
public class FileCacheEntry {

    private final boolean found;
    private final byte[] bytes;
    private final String path;
    private final long lastModified;
    private final boolean isDirectory;

    /**
     * Constructs a new instance.
     *
     * @param found        whether the file was found or not
     * @param bytes        the content of the file
     * @param path         the file path
     * @param lastModified the timestamp of the last modification
     * @param isDirectory  whether the resource is a directory
     */
    public FileCacheEntry(final boolean found, final byte[] bytes, final String path, final long lastModified,
                          final boolean isDirectory) {
        this.found = found;
        this.bytes = bytes;
        this.path = path;
        this.lastModified = lastModified;
        this.isDirectory = isDirectory;
    }

    @Override
    public String toString() {
        return "[found=" + found + ",path=" + path + ",length=" + bytes.length + "]";
    }

    /**
     * Whether the file was found.
     *
     * @return returns <code>true</code> if the file was found, otherwise
     * <code>false</code>
     */
    public boolean isFound() {
        return found;
    }

    /**
     * Get the content of the file.
     *
     * @return the content of the file
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * Get the file path.
     *
     * @return the file path
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the timestamp of the last modification.
     *
     * @return the timestamp of the last modification
     */
    public long lastModified() {
        return lastModified;
    }

    /**
     * Whether the resource is a directory.
     *
     * @return returns <code>true</code> if the resource is a directory,
     * otherwise <code>false</code>
     */
    public boolean isDirectory() {
        return isDirectory;
    }

}
