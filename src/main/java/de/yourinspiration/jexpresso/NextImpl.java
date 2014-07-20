package de.yourinspiration.jexpresso;

/**
 * Internal implementation for {@link Next}.
 * 
 * @author Marcel Härle
 *
 */
public class NextImpl implements Next {

    private final int currentIndex;
    private final MiddlewareChannelHandler middlewareChannelHandler;

    protected NextImpl(final int currentIndex, final MiddlewareChannelHandler middlewareChannelHandler) {
        this.currentIndex = currentIndex;
        this.middlewareChannelHandler = middlewareChannelHandler;
    }

    @Override
    public void next() {
        middlewareChannelHandler.next(currentIndex);
    }

    @Override
    public void cancel() {
        middlewareChannelHandler.cancel();
    }

}
