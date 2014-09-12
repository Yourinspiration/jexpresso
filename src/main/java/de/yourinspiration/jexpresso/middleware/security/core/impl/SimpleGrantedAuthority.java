package de.yourinspiration.jexpresso.middleware.security.core.impl;

import de.yourinspiration.jexpresso.middleware.security.core.GrantedAuthority;

/**
 * Simple implementation for {@link GrantedAuthority}.
 *
 * @author Marcel Härle
 */
public class SimpleGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = -5139260284447450801L;

    private final String authority;

    /**
     * Constructs a new granted authority object.
     *
     * @param authority the granted authority
     */
    public SimpleGrantedAuthority(final String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}
