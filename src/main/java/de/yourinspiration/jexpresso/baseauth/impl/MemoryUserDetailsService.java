package de.yourinspiration.jexpresso.baseauth.impl;

import de.yourinspiration.jexpresso.baseauth.GrantedAuthority;
import de.yourinspiration.jexpresso.baseauth.UserDetails;
import de.yourinspiration.jexpresso.baseauth.UserDetailsService;
import de.yourinspiration.jexpresso.baseauth.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple in memory user details service.
 *
 * @author Marcel Härle
 */
public class MemoryUserDetailsService implements UserDetailsService {

    private final Map<String, Credentials> users = new HashMap<>();

    /**
     * Adds a user to the memory store.
     *
     * @param username    the username
     * @param password    the password
     * @param authorities the authorities, separated by comma
     */
    public void addUser(final String username, final String password, final String authorities) {
        final Credentials credentials = new Credentials();
        credentials.authorities = authorities;
        credentials.enabled = true;
        credentials.password = password;
        credentials.username = username;
        users.put(username, credentials);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UserNotFoundException {
        if (users.containsKey(username)) {
            final List<GrantedAuthority> authorities = new ArrayList<>();
            for (String authority : users.get(username).authorities.split(",")) {
                authorities.add(new SimpleGrantedAuthority(authority));
            }
            final UserDetails userDetails = new SimpleUserDetails(users.get(username).username,
                    users.get(username).password, authorities, users.get(username).enabled);
            return userDetails;
        } else {
            throw new UserNotFoundException("user for username " + username + " not found");
        }
    }

    private class Credentials {
        String username;
        String password;
        String authorities;
        boolean enabled;
    }

}
