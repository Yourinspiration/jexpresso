package de.yourinspiration.jexpresso;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.yourinspiration.jexpresso.exception.ExceptionHandlerEntry;
import de.yourinspiration.jexpresso.exception.NotFoundException;

/**
 * Test case for {@link HttpJExpressoServerHandler}.
 * 
 * @author Marcel Härle
 *
 */
public class HttpJExpressoServerHandlerTest {

    private HttpJExpressoServerHandler handler;

    private final List<Route> routes = new ArrayList<>();
    private final List<ExceptionHandlerEntry> exceptionHandlerEntries = new ArrayList<>();
    private final Map<String, TemplateEngine> templateEngines = new HashMap<>();

    @Mock
    private ChannelHandlerContext ctx;
    @Mock
    private FullHttpRequest request;
    @Mock
    private Channel channel;
    @Mock
    private Attribute<RequestResponseContext> attribute;
    @Mock
    private RequestResponseContext reqResContext;

    private ResponseImpl responseImpl;

    @Mock
    private FullHttpResponse fullHttpResponse;
    @Mock
    private ChannelFuture channelFuture;
    @Mock
    private HttpHeaders httpHeaders;
    @Mock
    private HttpVersion httpVersion;
    @Mock
    private ByteBuf byteBuf;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        responseImpl = new ResponseImpl(fullHttpResponse);

        Mockito.when(ctx.channel()).thenReturn(channel);
        Mockito.when(channel.attr(RequestResponseContext.ATTR_KEY)).thenReturn(attribute);
        Mockito.when(attribute.get()).thenReturn(reqResContext);
        Mockito.when(reqResContext.getResponse()).thenReturn(responseImpl);
        Mockito.when(ctx.writeAndFlush(Matchers.any())).thenReturn(channelFuture);
        Mockito.when(request.headers()).thenReturn(httpHeaders);
        Mockito.when(request.getProtocolVersion()).thenReturn(httpVersion);
        Mockito.when(fullHttpResponse.headers()).thenReturn(httpHeaders);
        Mockito.when(fullHttpResponse.content()).thenReturn(byteBuf);

        handler = new HttpJExpressoServerHandler(routes, exceptionHandlerEntries, templateEngines);
    }

    @Test
    public void testForNoRoutes() throws Exception {
        handler.channelRead0(ctx, request);
    }

    @Test
    public void testForTextHtml() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/html");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                response.send("test");
            }
        }));

        handler.channelRead0(ctx, request);

        Mockito.verify(byteBuf).writeBytes("test".getBytes());
    }

    @Test
    public void testForTextPlain() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/plain");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                response.send("test");
            }
        }));

        handler.channelRead0(ctx, request);

        Mockito.verify(byteBuf).writeBytes("test".getBytes());
    }

    @Test
    public void testForUnkownContentType() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/unkown");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                response.send("test");
            }
        }));

        handler.channelRead0(ctx, request);

        Mockito.verify(byteBuf).writeBytes("test".getBytes());
    }

    @Test
    public void testForJson() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("application/json");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                final Customer c = new Customer();
                c.firstName = "Max";
                c.lastName = "Mustermann";
                response.json(c);
            }
        }));

        handler.channelRead0(ctx, request);

        Mockito.verify(byteBuf).writeBytes("{\"firstName\":\"Max\",\"lastName\":\"Mustermann\"}".getBytes());
    }

    @Test
    public void testThrowHttpStatusException() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/html");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                throw new NotFoundException();
            }
        }));

        handler.channelRead0(ctx, request);

        Mockito.verify(byteBuf).writeBytes(NotFoundException.DEFAULT_MSG.getBytes());
    }

    @Test
    public void testThrowCustomException() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/html");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        exceptionHandlerEntries.add(new ExceptionHandlerEntry(RuntimeException.class, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                response.send("EXCEPTION");
            }
        }));

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                throw new RuntimeException();
            }
        }));

        handler.channelRead0(ctx, request);

        Mockito.verify(byteBuf).writeBytes("EXCEPTION".getBytes());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowCustomExceptionWithoutExceptionHandler() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/html");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                throw new RuntimeException();
            }
        }));

        handler.channelRead0(ctx, request);
    }

    @Test
    public void testExceptionCaught() {
        final Throwable cause = new RuntimeException();
        handler.exceptionCaught(ctx, cause);
        Mockito.verify(ctx).close();
    }

    @Test
    public void testChannelReadComplete() {
        handler.channelReadComplete(ctx);
        Mockito.verify(ctx).flush();
    }

    @Test
    public void testRenderTemplate() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/html");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        templateEngines.put("hbs", new TemplateEngine() {

            @Override
            public String render(String template, Map<String, Object> options) {
                return "test";
            }
        });

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                response.render("test.hbs", new Options());
            }
        }));

        handler.channelRead0(ctx, request);

        Mockito.verify(byteBuf).writeBytes("test".getBytes());
    }

    @Test(expected = RuntimeException.class)
    public void testRenderTemplateForMissingFileExtention() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/html");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        templateEngines.put("hbs", new TemplateEngine() {

            @Override
            public String render(String template, Map<String, Object> options) {
                return "test";
            }
        });

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                response.render("test", new Options());
            }
        }));

        handler.channelRead0(ctx, request);
    }

    @Test(expected = RuntimeException.class)
    public void testRenderTemplateForMissingTemplateEngine() throws Exception {
        Mockito.when(httpHeaders.get(CONTENT_TYPE)).thenReturn("text/html");
        Mockito.when(request.getUri()).thenReturn("/test");
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.GET);

        templateEngines.put("xyz", new TemplateEngine() {

            @Override
            public String render(String template, Map<String, Object> options) {
                return "test";
            }
        });

        routes.add(new Route("/test", HttpMethod.GET, new RouteHandler() {

            @Override
            public void handle(Request request, Response response) {
                response.render("test.hbs", new Options());
            }
        }));

        handler.channelRead0(ctx, request);
    }

    class Customer {
        String firstName;
        String lastName;
    }

}
