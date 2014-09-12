package de.yourinspiration.jexpresso.middleware.security.core.impl;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for {@link SimpleGrantedAuthority}.
 *
 * @author Marcel Härle
 */
public class SimpleGrantedAuthorityTest {

    private final String authority = "USER";
    private SimpleGrantedAuthority simpleGrantedAuthority;

    @Before
    public void setUp() {
        simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
    }

    @Test
    public void testGetAuthority() {
        assertEquals(authority, simpleGrantedAuthority.getAuthority());
    }

}
