package de.yourinspiration.jexpresso;

import de.yourinspiration.jexpresso.exception.ExceptionHandlerEntry;
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
    protected boolean started = false;
    private List<MiddlewareHandler> middlewareHandlers = new ArrayList<>();

    protected JExpressoBase() {
    }

    protected void startNetty(final int port) throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Configure the server.
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(
                                    new HttpJExpressoServerInitializer(routes, exceptionHandlerEntries,
                                            middlewareHandlers, templateEngines));

                    Channel ch = b.bind(port).sync().channel();
                    ch.closeFuture().sync();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
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

}
