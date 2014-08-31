package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.transformer.ResponseTransformer;
import io.netty.handler.codec.http.HttpMethod;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link de.yourinspiration.jexpresso.core.JExpresso}.
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
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.get("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.GET, handler));
    }

    @Test
    public void testPost() {
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.post("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.POST, handler));
    }

    @Test
    public void testHead() {
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.head("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.HEAD, handler));
    }

    @Test
    public void testPut() {
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.put("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.PUT, handler));
    }

    @Test
    public void testOptions() {
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.options("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.OPTIONS, handler));
    }

    @Test
    public void testDelete() {
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.delete("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.DELETE, handler));
    }

    @Test
    public void testTrace() {
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.trace("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.TRACE, handler));
    }

    @Test
    public void testConnect() {
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.connect("/path", handler);

        Mockito.verify(base).addRoute(new Route("/path", HttpMethod.CONNECT, handler));
    }

    @Test
    public void testEngine() {
        final TemplateEngine templateEngine = (template, options) -> null;

        jexpresso.engine("hbs", templateEngine);

        Mockito.verify(base).addTemplateEngine("hbs", templateEngine);
    }

    @Test
    public void testException() {
        final RouteHandler handler = (request, response) -> {
        };

        jexpresso.exception(RuntimeException.class, handler);

        Mockito.verify(base).addExceptionHandler(RuntimeException.class, handler);
    }

    @Test
    public void testUse() {
        final MiddlewareHandler handler = (request, response, next) -> {

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

    @Test
    public void testSetTransformer() {
        final ResponseTransformer customResponseTransformer = new ResponseTransformer() {
            @Override
            public String render(Object model) {
                return ">>>" + model.toString() + "<<<";
            }

            @Override
            public ContentType contentType() {
                return ContentType.TEXT_PLAIN;
            }
        };

        jexpresso.setTransformer(customResponseTransformer);

        Mockito.verify(base).setTransformer(customResponseTransformer);
    }

}
