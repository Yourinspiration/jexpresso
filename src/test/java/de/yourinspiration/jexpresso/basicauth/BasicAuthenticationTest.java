package de.yourinspiration.jexpresso.basicauth;

import de.yourinspiration.jexpresso.baseauth.PasswordEncoder;
import de.yourinspiration.jexpresso.baseauth.UserDetails;
import de.yourinspiration.jexpresso.baseauth.UserDetailsService;
import de.yourinspiration.jexpresso.baseauth.UserNotFoundException;
import de.yourinspiration.jexpresso.core.Next;
import de.yourinspiration.jexpresso.core.Request;
import de.yourinspiration.jexpresso.core.Response;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

/**
 * Test case for {@link BasicAuthentication}.
 *
 * @author Marcel Härle
 */
public class BasicAuthenticationTest {

    private BasicAuthentication basicAuthentication;

    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private Request request;
    @Mock
    private Response response;
    @Mock
    private Next next;
    @Mock
    private UserDetails userDetails;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        basicAuthentication = new BasicAuthentication(userDetailsService, passwordEncoder);
    }

    @Test
    public void testSuccessfulAuthentication() throws UserNotFoundException {
        final String path = "/test/path";
        final String authorities = "";
        final HttpMethod[] methods = new HttpMethod[]{HttpMethod.GET};

        basicAuthentication.securePath(path, authorities, methods);

        Mockito.when(request.path()).thenReturn(path);
        Mockito.when(request.method()).thenReturn(HttpMethod.GET);
        Mockito.when(request.get("Authorization")).thenReturn("Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
        Mockito.when(userDetailsService.loadUserByUsername("Aladdin")).thenReturn(userDetails);

        Mockito.when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        Mockito.when(userDetails.getUsername()).thenReturn("Aladdin");
        Mockito.when(userDetails.getPassword()).thenReturn("open sesame");

        Mockito.when(passwordEncoder.encode("open sesame")).thenReturn("open sesame");
        Mockito.when(passwordEncoder.checkpw("open sesame", "open sesame")).thenReturn(true);

        basicAuthentication.handle(request, response, next);

        Mockito.verify(request).attribute("userDetails", userDetails);
        Mockito.verify(next).next();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUserNotFound() throws UserNotFoundException {
        final String path = "/test/path";
        final String authorities = "";
        final HttpMethod[] methods = new HttpMethod[]{HttpMethod.GET};

        basicAuthentication.securePath(path, authorities, methods);

        Mockito.when(request.path()).thenReturn(path);
        Mockito.when(request.method()).thenReturn(HttpMethod.GET);
        Mockito.when(request.get("Authorization")).thenReturn("Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
        Mockito.when(userDetailsService.loadUserByUsername("Aladdin")).thenThrow(UserNotFoundException.class);

        basicAuthentication.handle(request, response, next);

        Mockito.verify(next).cancel();
    }

    @Test
    public void testForFalsePassword() throws UserNotFoundException {
        final String path = "/test/path";
        final String authorities = "";
        final HttpMethod[] methods = new HttpMethod[]{HttpMethod.GET};

        basicAuthentication.securePath(path, authorities, methods);

        Mockito.when(request.path()).thenReturn(path);
        Mockito.when(request.method()).thenReturn(HttpMethod.GET);
        Mockito.when(request.get("Authorization")).thenReturn("Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
        Mockito.when(userDetailsService.loadUserByUsername("Aladdin")).thenReturn(userDetails);

        Mockito.when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());
        Mockito.when(userDetails.getUsername()).thenReturn("Aladdin");
        Mockito.when(userDetails.getPassword()).thenReturn("open another sesame");

        Mockito.when(passwordEncoder.encode("open another sesame")).thenReturn("open another sesame");
        Mockito.when(passwordEncoder.checkpw("open sesame", "open another sesame")).thenReturn(false);

        basicAuthentication.handle(request, response, next);

        Mockito.verify(next).cancel();
    }

}