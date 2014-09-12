package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.GrantedAuthority;
import de.yourinspiration.jexpresso.middleware.security.core.UserDetails;

import java.util.Collection;

/**
 * Simple implementation for {@link UserDetails}.
 *
 * @author Marcel Härle
 */
public class SimpleUserDetails implements UserDetails {

    private static final long serialVersionUID = 7578851062269846126L;

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    /**
     * Constructs a new user details object.
     *
     * @param username    the username
     * @param password    the password
     * @param authorities the granted authorities
     * @param enabled     whether the user is enabled
     */
    public SimpleUserDetails(final String username, final String password,
                             final Collection<? extends GrantedAuthority> authorities, final boolean enabled) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        String authoritiesString = "";
        for (GrantedAuthority grantedAuthority : authorities) {
            authoritiesString += "|" + grantedAuthority.getAuthority();
        }
        if (authoritiesString.length() > 0) {
            // Remove the first pipe character.
            authoritiesString = authoritiesString.substring(1);
        }
        return "[username=" + username + ",password=" + password + ",authorities=" + authoritiesString + ",enabled="
                + enabled + "]";
    }

}
