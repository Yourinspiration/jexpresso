package de.yourinspiration.jexpresso.middleware.session;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import java.io.Serializable;

/**
 * An implementation for {@link SessionStore} that stores de.yourinspiration.jexpresso.middleware.session data in a
 * MongoDB database.
 *
 * @author Marcel Härle
 */
public class MongoStore implements SessionStore {

    private static final String DATA_FIELD = "data";
    private static final String NAME_FIELD = "name";
    private static final String SESSION_ID_FIELD = "sessionId";

    private final DBCollection collection;
    private final Gson gson = new Gson();

    /**
     * Constructs a new {@link MongoStore}.
     *
     * @param mongo      the mongo db connection
     * @param dbname     the name of the database
     * @param collection the name of the collection to be used for storing the de.yourinspiration.jexpresso.middleware.session
     *                   data
     */
    public MongoStore(final Mongo mongo, final String dbname, final String collection) {
        this.collection = mongo.getDB(dbname).getCollection(collection);
    }

    @Override
    public <T extends Serializable> T get(final String name, final String sessionId, final Class<T> clazz) {
        final DBObject dbObject = collection.findOne(new BasicDBObject(SESSION_ID_FIELD, sessionId).append(NAME_FIELD,
                name));

        if (dbObject != null && dbObject.containsField(DATA_FIELD)) {
            return gson.fromJson((String) dbObject.get(DATA_FIELD), clazz);
        }

        return null;
    }

    @Override
    public void set(final String name, final Serializable value, final String sessionId) {
        DBObject dbObject = collection.findOne(new BasicDBObject(SESSION_ID_FIELD, sessionId).append(NAME_FIELD, name));
        if (dbObject != null) {
            dbObject.put(DATA_FIELD, gson.toJson(value));
        } else {
            dbObject = new BasicDBObject(NAME_FIELD, name).append(DATA_FIELD, gson.toJson(value)).append(
                    SESSION_ID_FIELD, sessionId);
        }
        collection.save(dbObject);
    }

    @Override
    public long size() {
        return collection.distinct(SESSION_ID_FIELD).size();
    }

    @Override
    public void clear() {
        collection.remove(new BasicDBObject());
    }

    @Override
    public void clear(final String sessionId) {
        collection.remove(new BasicDBObject(SESSION_ID_FIELD, sessionId));
    }

}
