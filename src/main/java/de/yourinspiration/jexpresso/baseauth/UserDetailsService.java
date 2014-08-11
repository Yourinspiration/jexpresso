package de.yourinspiration.jexpresso.baseauth;

/**
 * Service to retrieve user credentials for a given username.
 *
 * @author Marcel Härle
 */
public interface UserDetailsService {

    /**
     * Loads a user by his username.
     *
     * @param username the username
     * @return returns the credentials if the user could be found
     * @throws UserNotFoundException when the user could not be found
     */
    UserDetails loadUserByUsername(String username) throws UserNotFoundException;

}
