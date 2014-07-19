package de.yourinspiration.jexpresso;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.resource.Resource;
import org.pmw.tinylog.Logger;

import com.google.common.cache.CacheLoader;

/**
 * Cache loader for static file resources.
 * 
 * @author Marcel Härle
 *
 */
public class FileCacheLoader extends CacheLoader<String, FileCacheEntry> {

    @Override
    public FileCacheEntry load(final String resource) throws Exception {
        // Lookup the classpath for the requested resource.
        final URL fileUrl = getClass().getResource("/" + resource);

        // null will be returned when there is no such resource on the
        // classpath.
        if (fileUrl == null) {
            Logger.debug("Resource {0} not found on classpath", resource);
            return new FileCacheEntry(false, null, resource, System.currentTimeMillis(), false);
        }

        final Resource fileResource = Resource.newResource(fileUrl);

        if (!fileResource.exists()) {
            Logger.debug("Resource {0} does not exist", resource);
            return new FileCacheEntry(false, null, resource, System.currentTimeMillis(), false);
        }

        if (fileResource.isDirectory()) {
            return new FileCacheEntry(true, null, resource, System.currentTimeMillis(), true);
        }

        final InputStream fileInputStream = fileUrl.openStream();

        byte[] bytes;

        // Somehow there is a NPE when accessing a directory when compressed to
        // a JAR.
        try {
            bytes = IOUtils.toByteArray(fileInputStream);
        } catch (NullPointerException npe) {
            return new FileCacheEntry(false, null, resource, System.currentTimeMillis(), false);
        } finally {
            fileInputStream.close();
        }

        // Cached files cannot be changed, so simulate a last modified timestamp
        // by the current timestamp.
        final FileCacheEntry fileCacheEntry = new FileCacheEntry(true, bytes, resource, System.currentTimeMillis(),
                false);

        Logger.debug("Resource {0} loaded in file cache", fileCacheEntry);

        return fileCacheEntry;
    }

}
