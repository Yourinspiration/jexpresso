package de.yourinspiration.jexpresso;

import io.netty.handler.codec.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Map;

/**
 * Test case for {@link JExpresso}.
 *
 * @author Marcel Härle
 */
public class JExpressoTest {

    private JExpresso jexpresso;

    @Mock
    private JExpressoBase base;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jexpresso = new JExpresso(base);
    }

    @Test
    public void testGet() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.get("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.GET, handler));
    }

    @Test
    public void testPost() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.post("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.POST, handler));
    }

    @Test
    public void testHead() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.head("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.HEAD, handler));
    }

    @Test
    public void testPut() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.put("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.PUT, handler));
    }

    @Test
    public void testOptions() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.options("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.OPTIONS, handler));
    }

    @Test
    public void testDelete() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.delete("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.DELETE, handler));
    }

    @Test
    public void testTrace() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.trace("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.TRACE, handler));
    }

    @Test
    public void testConnect() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.connect("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.CONNECT, handler));
    }

    @Test
    public void testEngine() {
        final TemplateEngine templateEngine = new TemplateEngine() {

            @Override
            public String render(String template, Map<String, Object> options) {
                return null;
            }
        };

        jexpresso.engine("hbs", templateEngine);

        Mockito.verify(base).addTemplateEngine("hbs", templateEngine);
    }

    @Test
    public void testException() {
        final RouteHandler handler = new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
            }
        };

        jexpresso.exception(RuntimeException.class, handler);

        Mockito.verify(base).addExceptionHandler(RuntimeException.class, handler);
    }

    @Test
    public void testUse() {
        final MiddlewareHandler handler = new MiddlewareHandler() {

            @Override
            public void handle(Request request, Response response, Next next) {

            }
        };

        jexpresso.use(handler);

        Mockito.verify(base).addMiddleware(handler);
    }

    @Test(expected = RuntimeException.class)
    public void testEnable() {
        jexpresso.enable("feature");
    }

    @Test
    public void testListenInt() throws InterruptedException {
        jexpresso.listen(8888);

        Mockito.verify(base).startNetty(8888);
    }

    @Test
    public void testListenIntStarterCallback() throws InterruptedException {
        final StarterCallback starterCallback = Mockito.mock(StarterCallback.class);

        jexpresso.listen(8888, starterCallback);

        Mockito.verify(base).startNetty(8888);
        Mockito.verify(starterCallback).handle();
    }

}
