package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.GrantedAuthority;
import de.yourinspiration.jexpresso.middleware.security.core.UserDetails;
import de.yourinspiration.jexpresso.middleware.security.core.UserDetailsService;
import de.yourinspiration.jexpresso.middleware.security.core.UserNotFoundException;
import org.pmw.tinylog.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementation to retrieve user details by a JDBC SQL connection.
 *
 * @author Marcel Härle
 */
public class JdbcUserDetailsService implements UserDetailsService {

    private final Connection connection;
    private final String userByUsernameQuery;
    private final String authoritiesByUsernameQuery;

    /**
     * Constructs a new object. The first result entry of the
     * userByUsernameQuery must be the password, the second one the enabled
     * flag. The first and only result entry of the authoritiesByUsernameQuery
     * must be the granted authority.
     *
     * @param connection                 the sql connection
     * @param userByUsernameQuery        the query to retrieve the user credentials by username, e.g.
     *                                   'select password, enabled from users where username = ?'
     * @param authoritiesByUsernameQuery the query to retrieve the user's granted authorities, e.g.
     *                                   'select role from userroles where username = ?'
     */
    public JdbcUserDetailsService(final Connection connection, final String userByUsernameQuery,
                                  final String authoritiesByUsernameQuery) {
        this.connection = connection;
        this.userByUsernameQuery = userByUsernameQuery;
        this.authoritiesByUsernameQuery = authoritiesByUsernameQuery;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UserNotFoundException {
        final String userQuery = userByUsernameQuery.replace("?", "'" + username + "'");

        try {
            final ResultSet userResultSet = connection.createStatement().executeQuery(userQuery);
            if (userResultSet.first()) {
                final String password = userResultSet.getString(1);
                final boolean enabled = userResultSet.getBoolean(2);

                final Collection<GrantedAuthority> authorities = getGrantedAuthorities(username);

                return new SimpleUserDetails(username, password, authorities, enabled);
            } else {
                throw new UserNotFoundException("No user found for username " + username);
            }

        } catch (SQLException sqle) {
            Logger.error("Error querying user by username: {0}", sqle);
            return null;
        }
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(final String username) {
        final String authorityQuery = authoritiesByUsernameQuery.replace("?", "'" + username + "'");

        final Collection<GrantedAuthority> authorities = new ArrayList<>();

        try {
            final ResultSet authoriyResultSet = connection.createStatement().executeQuery(authorityQuery);

            while (authoriyResultSet.next()) {
                final String role = authoriyResultSet.getString(1);
                final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role);
                authorities.add(grantedAuthority);
            }
        } catch (SQLException sqle) {
            Logger.error("Error querying granted authorities: {0}", sqle);
        }

        return authorities;
    }

}
