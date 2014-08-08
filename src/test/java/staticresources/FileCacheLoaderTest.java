package staticresources;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for {@link FileCacheLoader}.
 *
 * @author Marcel Härle
 */
public class FileCacheLoaderTest {

    private FileCacheLoader loader;

    @Before
    public void setUp() {
        loader = new FileCacheLoader();
    }

    @Test
    public void testLoadFile() throws Exception {
        final String path = "assets/test.txt";
        final FileCacheEntry entry = loader.load(path);
        assertEquals(path, entry.getPath());
        assertTrue(entry.isFound());
        assertFalse(entry.isDirectory());
        assertArrayEquals("TEST".getBytes(), entry.getBytes());
    }

    @Test
    public void testLoadDirectory() throws Exception {
        final String path = "assets";
        final FileCacheEntry entry = loader.load(path);
        assertEquals(path, entry.getPath());
        assertTrue(entry.isFound());
        assertTrue(entry.isDirectory());
    }

    @Test
    public void testLoadNotExistingFile() throws Exception {
        final String path = "assets/does/not/exist";
        final FileCacheEntry entry = loader.load(path);
        assertEquals(path, entry.getPath());
        assertFalse(entry.isFound());
    }

}
