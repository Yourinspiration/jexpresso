package de.yourinspiration.jexpresso.middleware.security.core.impl;

import com.mongodb.*;
import de.yourinspiration.jexpresso.middleware.security.core.GrantedAuthority;
import de.yourinspiration.jexpresso.middleware.security.core.UserDetails;
import de.yourinspiration.jexpresso.middleware.security.core.UserDetailsService;
import de.yourinspiration.jexpresso.middleware.security.core.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation to retrieve user details by a MongoDB connection.
 *
 * @author Marcel Härle
 */
public class MongoDbUserDetailsService implements UserDetailsService {

    private final Mongo mongo;
    private final String database;
    private final String userCollection;
    private final String usernameField;
    private final String passwordField;
    private final String enabledField;
    private final String authoritiesField;

    /**
     * Constructs a new object.
     *
     * @param mongo            the mongo db connection
     * @param database         the database name
     * @param userCollection   the name of the user collection
     * @param usernameField    the name of the field for the username
     * @param passwordField    the name of the field for the password
     * @param enabledField     the name of the field for the enabled flag
     * @param authoritiesField the name of the field for the authority list
     */
    public MongoDbUserDetailsService(final Mongo mongo, final String database, final String userCollection,
                                     final String usernameField, final String passwordField, final String enabledField,
                                     final String authoritiesField) {
        this.mongo = mongo;
        this.database = database;
        this.userCollection = userCollection;
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.enabledField = enabledField;
        this.authoritiesField = authoritiesField;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UserNotFoundException {
        final DB db = mongo.getDB(database);
        final DBCollection userDbCollection = db.getCollection(userCollection);

        final DBObject userQuery = new BasicDBObject().append(usernameField, username);
        final DBObject userResult = userDbCollection.findOne(userQuery);

        if (userResult == null) {
            throw new UserNotFoundException("user for username " + username + " not found");
        }

        final String password = (String) userResult.get(passwordField);
        final boolean enabled = Boolean.parseBoolean((String) userResult.get(enabledField));

        final Collection<GrantedAuthority> authorities = new ArrayList<>();
        final BasicDBList authorityDBList = (BasicDBList) userResult.get(authoritiesField);
        for (Object authorityObject : authorityDBList) {
            final String role = (String) authorityObject;
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return new SimpleUserDetails(username, password, authorities, enabled);
    }

}
