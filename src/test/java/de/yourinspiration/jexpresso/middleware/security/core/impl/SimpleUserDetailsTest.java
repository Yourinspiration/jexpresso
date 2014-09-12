package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.GrantedAuthority;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link SimpleUserDetails}.
 *
 * @author Marcel Härle
 */
public class SimpleUserDetailsTest {

    private final String username = "username";
    private final String password = "password";
    private final Collection<GrantedAuthority> authorities = new ArrayList<>();
    private final boolean enabled = true;
    private SimpleUserDetails simpleUserDetails;

    @Before
    public void setUp() {
        simpleUserDetails = new SimpleUserDetails(username, password, authorities, enabled);
    }

    @Test
    public void testGetAuthorities() {
        assertEquals(authorities, simpleUserDetails.getAuthorities());
    }

    @Test
    public void testGetPassword() {
        assertEquals(password, simpleUserDetails.getPassword());
    }

    @Test
    public void testGetUsername() {
        assertEquals(username, simpleUserDetails.getUsername());
    }

    @Test
    public void testIsEnabled() {
        assertEquals(enabled, simpleUserDetails.isEnabled());
    }

}
