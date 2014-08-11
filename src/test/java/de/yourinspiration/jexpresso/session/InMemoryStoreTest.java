package de.yourinspiration.jexpresso.session;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link InMemoryStore}.
 *
 * @author Marcel Härle
 */
public class InMemoryStoreTest {

    private InMemoryStore store;

    @Before
    public void setUp() {
        store = new InMemoryStore();
    }

    @Test
    public void testSetAndGet() {
        store.set("key", "value", "4711");
        assertEquals("value", store.get("key", "4711", String.class));
    }

    @Test
    public void testSize() {
        store.set("key", "value", "4711");
        assertEquals(1, store.size());
    }

    @Test
    public void testClear() {
        store.set("key", "value", "4711");
        store.set("key", "value", "4712");
        assertEquals(2, store.size());
        store.clear();
        assertEquals(0, store.size());
    }

    @Test
    public void testClearForSessionId() {
        store.set("key", "value", "4711");
        store.set("key", "value", "4712");
        assertEquals(2, store.size());
        store.clear("4712");
        assertEquals(1, store.size());
    }

}
