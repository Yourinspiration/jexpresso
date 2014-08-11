package de.yourinspiration.jexpresso.staticresources;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link FileCacheEntry}.
 *
 * @author Marcel Härle
 */
public class FileCacheEntryTest {

    private final boolean found = true;
    private final byte[] bytes = new byte[0];
    private final String path = "/my/path";
    private final long lastModified = 1234567899;
    private final boolean isDirectory = false;
    private FileCacheEntry entry;

    @Before
    public void setUp() {
        entry = new FileCacheEntry(found, bytes, path, lastModified, isDirectory);
    }

    @Test
    public void testToString() {
        assertEquals("[found=" + found + ",path=" + path + ",length=" + bytes.length + "]", entry.toString());
    }

    @Test
    public void testIsFound() {
        assertEquals(found, entry.isFound());
    }

    @Test
    public void testGetBytes() {
        assertArrayEquals(bytes, entry.getBytes());
    }

    @Test
    public void testGetPath() {
        assertEquals(path, entry.getPath());
    }

    @Test
    public void testLastModified() {
        assertEquals(lastModified, entry.lastModified());
    }

    @Test
    public void testIsDirectory() {
        assertEquals(isDirectory, entry.isDirectory());
    }

}
