package de.yourinspiration.jexpresso.baseauth;

import java.io.Serializable;

/**
 * A granted authority represents a certain role that the user has been granted
 * for the application.
 *
 * @author Marcel Härle
 */
public interface GrantedAuthority extends Serializable {

    /**
     * The string representation of the granted authority.
     *
     * @return the string representation of the granted authority
     */
    String getAuthority();

}
