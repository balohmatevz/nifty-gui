package de.lessvoid.nifty.screen;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyMouse;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.screen.Screen.StartScreenEndNotify;
import de.lessvoid.nifty.spi.time.TimeProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScreenTest {
  private Nifty niftyMock;
  private ScreenController screenControllerMock;
  private TimeProvider timeProviderMock;
  private Screen screen;
  private StartScreenEndNotify startScreenEndNotify;

  @Before
  public void before() {
    NiftyRenderEngine niftyRenderEngineMock = createMock(NiftyRenderEngine.class);
    expect(niftyRenderEngineMock.convertFromNativeX(anyInt())).andStubReturn(0);
    expect(niftyRenderEngineMock.convertFromNativeY(anyInt())).andStubReturn(0);
    replay(niftyRenderEngineMock);

    NiftyMouse niftyMouseMock = createMock(NiftyMouse.class);
    expect(niftyMouseMock.getX()).andStubReturn(0);
    expect(niftyMouseMock.getY()).andStubReturn(0);
    replay(niftyMouseMock);

    screenControllerMock = createMock(ScreenController.class);
    niftyMock = createMock(Nifty.class);
    niftyMock.subscribeAnnotations(screenControllerMock);
    expect(niftyMock.getRenderEngine()).andStubReturn(niftyRenderEngineMock);
    expect(niftyMock.getNiftyMouse()).andStubReturn(niftyMouseMock);
    replay(niftyMock);

    screenControllerMock.onStartScreen();
    replay(screenControllerMock);

    timeProviderMock = createMock(TimeProvider.class);
    expect(timeProviderMock.getMsTime()).andStubReturn(0L);
    replay(timeProviderMock);

    screen = new Screen(niftyMock, "id", screenControllerMock, timeProviderMock);
  }

  @After
  public void after() {
    verify(niftyMock);
    verify(screenControllerMock);
    verify(timeProviderMock);
  }

  @Test
  public void testOnStartScreenHasEnded() {
    assertFalse(screen.isRunning());
    screen.onStartScreenHasEnded();
    assertTrue(screen.isRunning());
  }

  @Test
  public void testStartScreenEndNotifyWithAdditionalEndNotify() {
    EndNotify endNotifyMock = createMock(EndNotify.class);
    endNotifyMock.perform();
    replay(endNotifyMock);

    startScreenEndNotify = screen.createScreenStartEndNotify(endNotifyMock);
    startScreenEndNotify.perform();

    verify(endNotifyMock);
  }

  @Test
  public void testStartScreenEndNotifyWithoutAdditionalEndNotify() {
    startScreenEndNotify = screen.createScreenStartEndNotify(null);
    startScreenEndNotify.perform();
  }
}
