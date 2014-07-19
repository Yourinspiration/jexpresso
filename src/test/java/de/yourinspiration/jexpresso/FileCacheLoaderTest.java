package de.yourinspiration.jexpresso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link FileCacheLoader}.
 * 
 * @author Marcel Härle
 *
 */
public class FileCacheLoaderTest {

    private FileCacheLoader fileCacheLoader;

    @Before
    public void setUp() {
        fileCacheLoader = new FileCacheLoader();
    }

    @Test
    public void testLoadForNotExistingResource() throws Exception {
        final String resource = "does/not/exist";
        final FileCacheEntry fileCacheEntry = fileCacheLoader.load(resource);
        assertFalse("the file should not be found", fileCacheEntry.isFound());
        assertNull("there should be no bytes", fileCacheEntry.getBytes());
        assertEquals("should be the right resource", resource, fileCacheEntry.getPath());
    }

    @Test
    public void testLoadForExistinResource() throws Exception {
        final String resource = "assets/test.txt";
        final FileCacheEntry fileCacheEntry = fileCacheLoader.load(resource);
        assertTrue("the file should be found", fileCacheEntry.isFound());
        assertNotNull("there should be bytes", fileCacheEntry.getBytes());
        assertEquals("should be the right resource", resource, fileCacheEntry.getPath());
        assertFalse("file should not be a directory", fileCacheEntry.isDirectory());
    }

    @Test
    public void testLoadForDirectory() throws Exception {
        final String resource = "assets";
        final FileCacheEntry fileCacheEntry = fileCacheLoader.load(resource);
        assertTrue("the file should be found", fileCacheEntry.isFound());
        assertNull("there should be no bytes for a directory", fileCacheEntry.getBytes());
        assertEquals("should be the right resource", resource, fileCacheEntry.getPath());
        assertTrue("file should be a directory", fileCacheEntry.isDirectory());
    }

}
