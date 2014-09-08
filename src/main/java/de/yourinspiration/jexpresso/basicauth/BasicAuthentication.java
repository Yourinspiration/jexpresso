package de.yourinspiration.jexpresso.basicauth;

import de.yourinspiration.jexpresso.baseauth.*;
import de.yourinspiration.jexpresso.core.MiddlewareHandler;
import de.yourinspiration.jexpresso.core.Next;
import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;
import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.http.HttpStatus;
import io.netty.handler.codec.http.HttpMethod;
import org.pmw.tinylog.Logger;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * A middleware for JExpresso providing HTTP Basic Authentication.
 *
 * @author Marcel Härle
 */
public class BasicAuthentication implements MiddlewareHandler {

    public static final String USER_DETAILS_ATTR = "userDetails";

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final List<SecurityRoute> securityRoutes = new ArrayList<>();

    /**
     * Constructs a new HTTP Basic Authentiation middleware.
     *
     * @param userDetailsService the user details service to retrieve user credentials
     * @param passwordEncoder    the password encoder to check the passwords
     */
    public BasicAuthentication(final UserDetailsService userDetailsService, final PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Provide HTTP Basic Authentication for the given path and methods.
     *
     * @param path        the path to be secured
     * @param authorities the granted authorities
     * @param methods     the methods to be secured
     */
    public void securePath(final String path, final String authorities, final HttpMethod... methods) {
        securityRoutes.add(new SecurityRoute(path, authorities, methods));
    }

    @Override
    public void handle(Request request, Response response, Next next) {
        if (checkSecurityProviders(request, response)) {
            next.next();
        } else {
            next.cancel();
        }
    }

    private boolean checkSecurityProviders(final Request request, final Response response) {
        final String path = getUri(request);
        final HttpMethod method = request.method();

        for (SecurityRoute route : securityRoutes) {
            if (route.matchesPathAndMethod(path, method)) {
                final boolean authenticated = checkAuthentication(request, userDetailsService, passwordEncoder);
                if (!authenticated) {
                    handleUnauthenticated(response);
                    return false;
                } else {
                    return true;
                }
            }
        }

        return true;
    }

    private String getUri(final Request request) {
        if (request.path().contains("?")) {
            return request.path().substring(0, request.path().indexOf("?"));
        } else {
            return request.path();
        }
    }

    private void handleUnauthenticated(final Response response) {
        response.status(HttpStatus.UNAUTHORIZED);
        response.set("WWW-Authenticate", "Basic realm=\"sparkle realm\"");
        response.type(ContentType.TEXT_PLAIN);
        response.send("");
    }

    private boolean checkAuthentication(Request request, final UserDetailsService userDetailsService,
                                        final PasswordEncoder passwordEncoder) {
        boolean authenticated = false;

        if (request.get("Authorization").isPresent()) {
            String authorization = request.get("Authorization").get();
            if (authorization.startsWith("Basic")) {
                // Authorization: Basic base64credentials
                final String base64Credentials = authorization.substring("Basic".length()).trim();
                final String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                        Charset.forName("UTF-8"));

                // credentials = username:password
                final String[] values = credentials.split(":", 2);

                try {
                    final UserDetails userDetails = userDetailsService.loadUserByUsername(values[0]);

                    if (userDetails != null && passwordEncoder.checkpw(values[1], userDetails.getPassword())) {
                        final String authorities = getAuthoritiesForRoute(request.path(), request.method());

                        if (hasGrantedAuthoriy(userDetails, authorities)) {
                            request.attribute(USER_DETAILS_ATTR, userDetails);
                            authenticated = true;
                        }
                    }
                } catch (UserNotFoundException e) {
                    Logger.debug("User not found", e);
                }
            }
        };

        return authenticated;
    }

    private String getAuthoritiesForRoute(final String path, final HttpMethod method) {
        for (SecurityRoute securityRoute : securityRoutes) {
            if (securityRoute.matchesPathAndMethod(path, method)) {
                return securityRoute.getAuthorities();
            }
        }
        return "";
    }

    private boolean hasGrantedAuthoriy(final UserDetails userDetails, final String authorities) {
        if ("".equals(authorities.trim())) {
            return true;
        }

        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            for (String authority : authorities.split(",")) {
                if (grantedAuthority.getAuthority().equalsIgnoreCase(authority)) {
                    return true;
                }
            }
        }

        return false;
    }

}
