package de.yourinspiration.jexpresso;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link FileCacheEntry}.
 * 
 * @author Marcel Härle
 *
 */
public class FileCacheEntryTest {

    private FileCacheEntry fileCacheEntry;

    private final boolean found = true;
    private final byte[] bytes = new byte[0];
    private final String path = "/the/path";
    private final long lastModified = 1234567;
    private final boolean isDirectory = false;

    @Before
    public void setUp() {
        fileCacheEntry = new FileCacheEntry(found, bytes, path, lastModified, isDirectory);
    }

    @Test
    public void testIsFound() {
        assertEquals(found, fileCacheEntry.isFound());
    }

    @Test
    public void testGetBytes() {
        assertEquals(bytes, fileCacheEntry.getBytes());
    }

    @Test
    public void testGetPath() {
        assertEquals(path, fileCacheEntry.getPath());
    }

    @Test
    public void testLastModified() {
        assertEquals(lastModified, fileCacheEntry.lastModified());
    }

    @Test
    public void testIsDirectory() {
        assertEquals(isDirectory, fileCacheEntry.isDirectory());
    }

}
