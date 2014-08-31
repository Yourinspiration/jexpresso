package de.yourinspiration.jexpresso.core;

import de.yourinspiration.jexpresso.exception.ExceptionHandlerEntry;
import de.yourinspiration.jexpresso.http.ContentType;
import de.yourinspiration.jexpresso.transformer.HtmlTransformer;
import de.yourinspiration.jexpresso.transformer.JsonTransformer;
import de.yourinspiration.jexpresso.transformer.PlainTextTransformer;
import de.yourinspiration.jexpresso.transformer.ResponseTransformer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.pmw.tinylog.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal base class for a JExpresso application. Does not provide any public
 * API.
 *
 * @author Marcel Härle
 */
public class JExpressoBase {

    private final List<Route> routes = new ArrayList<>();
    private final List<ExceptionHandlerEntry> exceptionHandlerEntries = new ArrayList<>();
    private final Map<String, TemplateEngine> templateEngines = new HashMap<>();
    private final List<MiddlewareHandler> middlewareHandlers = new ArrayList<>();
    private final Map<ContentType, ResponseTransformer> responseTransformerMap = new HashMap<>();

    protected JExpressoBase() {
        // Init default response transformers
        responseTransformerMap.put(ContentType.APPLICATION_JSON, new JsonTransformer());
        responseTransformerMap.put(ContentType.TEXT_HTML, new HtmlTransformer());
        responseTransformerMap.put(ContentType.TEXT_PLAIN, new PlainTextTransformer());
    }

    protected void startNetty(final int port) {
        new Thread(() -> {
            // Configure the server.
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(
                                new HttpJExpressoServerInitializer(routes, exceptionHandlerEntries,
                                        middlewareHandlers, templateEngines, responseTransformerMap));

                Channel ch = b.bind(port).sync().channel();
                ch.closeFuture().sync();
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }).start();
    }

    protected void addRoute(final Route route) {
        // issue #29: Replace a route for the same path.
        for (int i = 0, l = routes.size(); i < l; i++) {
            if (routes.get(i).equals(route)) {
                routes.remove(i);
                Logger.debug("Replaces route for path {0}", route.getPath());
                break;
            }
        }
        routes.add(route);
        Logger.debug("Added route {0}", route);
    }

    protected void addExceptionHandler(Class<? extends Exception> exceptionClass, final RouteHandler routeHandler) {
        exceptionHandlerEntries.add(new ExceptionHandlerEntry(exceptionClass, routeHandler));
    }

    protected void addMiddleware(final MiddlewareHandler handler) {
        middlewareHandlers.add(handler);
    }

    protected void addTemplateEngine(final String ext, final TemplateEngine templateEngine) {
        templateEngines.put(ext, templateEngine);
    }

    public void setTransformer(final ResponseTransformer responseTransformer) {
        responseTransformerMap.put(responseTransformer.contentType(), responseTransformer);
    }
}
