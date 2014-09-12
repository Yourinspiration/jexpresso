package de.yourinspiration.jexpresso.middleware.security.core;

import java.io.Serializable;
import java.util.Collection;

/**
 * Holds the credentials for an authentication request.
 *
 * @author Marcel Härle
 */
public interface UserDetails extends Serializable {

    /**
     * The user's granted authorities.
     *
     * @return returns an empty collection if the user has no granted
     * authorities
     */
    Collection<? extends GrantedAuthority> getAuthorities();

    /**
     * The user's password.
     *
     * @return the user's password
     */
    String getPassword();

    /**
     * The user's username.
     *
     * @return the user's username
     */
    String getUsername();

    /**
     * Whether the user is enabled.
     *
     * @return returns <code>true</code> if the user is enabled, otherwise
     * <code>false</code>
     */
    boolean isEnabled();

}
