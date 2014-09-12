package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.UserDetails;
import de.yourinspiration.jexpresso.middleware.security.core.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test case for {@link MemoryUserDetailsService}.
 *
 * @author Marcel Härle
 */
public class MemoryUserDetailsServiceTest {

    private MemoryUserDetailsService userDetailsService;

    @Before
    public void setUp() {
        userDetailsService = new MemoryUserDetailsService();
    }

    @Test
    public void testAddAndLoadUser() throws UserNotFoundException {
        final String username = "max";
        final String password = "1234";
        final String authorities = "USER";

        userDetailsService.addUser(username, password, authorities);

        final UserDetails userDetails = userDetailsService.loadUserByUsername("max");

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
    }

    @Test(expected = UserNotFoundException.class)
    public void testLoadUserForNotExistingUser() throws UserNotFoundException {
        userDetailsService.loadUserByUsername("max");
    }

}
