package de.yourinspiration.jexpresso.middleware.session;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test case for {@link MongoStore}.
 *
 * @author Marcel Härle
 */
public class MongoStoreTest {

    private final String dbname = "testdb";
    private final String collection = "sessions";
    private MongoStore mongoStore;
    @Mock
    private Mongo mongo;
    @Mock
    private DB db;
    @Mock
    private DBCollection dbCollection;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Mockito.when(mongo.getDB(dbname)).thenReturn(db);
        Mockito.when(db.getCollection(collection)).thenReturn(dbCollection);

        mongoStore = new MongoStore(mongo, dbname, collection);
    }

    @Test
    public void testGet() {
        final String sessionId = "sdlnlekgnl3ekgnslekg";
        final String name = "test";

        Mockito.when(dbCollection.findOne(new BasicDBObject("sessionId", sessionId).append("name", name))).thenReturn(
                new BasicDBObject("data", "\"test value\""));

        assertEquals("test value", mongoStore.get(name, sessionId, String.class));
    }

    @Test
    public void testGetForNotExistingName() {
        final String sessionId = "sdlnlekgnl3ekgnslekg";
        final String name = "test";

        Mockito.when(dbCollection.findOne(new BasicDBObject("sessionId", sessionId).append("name", name))).thenReturn(
                null);

        assertNull(mongoStore.get(name, sessionId, String.class));
    }

    @Test
    public void testGetForMissingDataField() {
        final String sessionId = "sdlnlekgnl3ekgnslekg";
        final String name = "test";

        Mockito.when(dbCollection.findOne(new BasicDBObject("sessionId", sessionId).append("name", name))).thenReturn(
                new BasicDBObject("falseField", "\"test value\""));

        assertNull(mongoStore.get(name, sessionId, String.class));
    }

    @Test
    public void testSet() {
        final String sessionId = "sdlnlekgnl3ekgnslekg";
        final String name = "test";
        final String value = "test value";

        Mockito.when(dbCollection.findOne(new BasicDBObject("sessionId", sessionId).append("name", name))).thenReturn(
                null);

        mongoStore.set(name, value, sessionId);

        Mockito.verify(dbCollection).save(
                new BasicDBObject("name", name).append("data", "\"test value\"").append("sessionId", sessionId));
    }

    @Test
    public void testSetForExistingName() {
        final String sessionId = "sdlnlekgnl3ekgnslekg";
        final String name = "test";
        final String value = "test value";

        Mockito.when(dbCollection.findOne(new BasicDBObject("sessionId", sessionId).append("name", name))).thenReturn(
                new BasicDBObject("data", "\"some other value\"").append("sessionId", sessionId).append("name", name));

        mongoStore.set(name, value, sessionId);

        Mockito.verify(dbCollection).save(
                new BasicDBObject("name", name).append("data", "\"test value\"").append("sessionId", sessionId));
    }

    @Test
    public void testSize() {
        final List<String> value = new ArrayList<>();
        value.add("1");
        value.add("2");

        Mockito.when(dbCollection.distinct("sessionId")).thenReturn(value);

        assertEquals(2, mongoStore.size());
    }

    @Test
    public void testClear() {
        mongoStore.clear();

        Mockito.verify(dbCollection).remove(new BasicDBObject());
    }

    @Test
    public void testClearForSessionId() {
        final String sessionId = "sdlkfnsdlfknelkfn";

        mongoStore.clear(sessionId);

        Mockito.verify(dbCollection).remove(new BasicDBObject("sessionId", sessionId));
    }

}
