package de.yourinspiration.jexpresso.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link de.yourinspiration.jexpresso.core.NextImpl}.
 *
 * @author Marcel Härle
 */
public class NextImplTest {

    private final int currentIndex = 1;
    @Mock
    MiddlewareChannelHandler middlewareChannelHandler;
    private NextImpl nextImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        nextImpl = new NextImpl(currentIndex, middlewareChannelHandler);
    }

    @Test
    public void testNext() {
        nextImpl.next();
        Mockito.verify(middlewareChannelHandler).next(currentIndex);
    }

    @Test
    public void testCancel() {
        nextImpl.cancel();
        Mockito.verify(middlewareChannelHandler).cancel();
    }

}
