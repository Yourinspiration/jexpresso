package de.yourinspiration.jexpresso;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link NextImpl}.
 *
 * @author Marcel Härle
 */
public class NextImplTest {

    @Mock
    MiddlewareChannelHandler middlewareChannelHandler;
    private NextImpl nextImpl;
    private int currentIndex = 1;

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
