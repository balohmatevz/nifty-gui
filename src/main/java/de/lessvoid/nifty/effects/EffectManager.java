package de.lessvoid.nifty.effects;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.render.RenderStateType;
import de.lessvoid.nifty.tools.TimeProvider;

/**
 * manage all effects of an element.
 * @author void
 */
public class EffectManager {

  /**
   * all the effects.
   */
  private Map < EffectEventId, EffectProcessor > effectProcessor = new Hashtable < EffectEventId, EffectProcessor >();

  /**
   * alternateKey we should use.
   */
  private String alternateKey;

  /**
   * create a new effectManager with the given listener.
   */
  public EffectManager() {
    this.alternateKey = null;

    effectProcessor.put(EffectEventId.onStartScreen, new EffectProcessor(false, false));
    effectProcessor.put(EffectEventId.onEndScreen, new EffectProcessor(true, false));
    effectProcessor.put(EffectEventId.onFocus, new EffectProcessor(true, false));
    effectProcessor.put(EffectEventId.onClick, new EffectProcessor(false, false));
    effectProcessor.put(EffectEventId.onHover, new EffectProcessor(true, true));
    effectProcessor.put(EffectEventId.onActive, new EffectProcessor(true, false));
    effectProcessor.put(EffectEventId.onCustom, new EffectProcessor(false, false));
  }

  /**
   * register an effect.
   * @param id the id
   * @param e the effect
   */
  public final void registerEffect(final EffectEventId id, final Effect e) {
    effectProcessor.get(id).registerEffect(e);
  }

  /**
   * start all effects with the given id for the given element.
   * @param id the effect id to start
   * @param w the element
   * @param time TimeProvider
   * @param listener the {@link EndNotify} to use.
   */
  public final void startEffect(
      final EffectEventId id,
      final Element w,
      final TimeProvider time,
      final EndNotify listener) {
    effectProcessor.get(id).activate(listener, alternateKey);
  }

  /**
   * Stop effects with the given id.
   * @param effectId effect id to stop
   */
  public void stopEffect(final EffectEventId effectId) {
    effectProcessor.get(effectId).setActive(false);
  }

  /**
   * prepare rendering.
   * @param renderDevice RenderDevice
   */
  public void begin(final NiftyRenderEngine renderDevice) {
    Set < RenderStateType > renderStates = RenderStateType.allStates();

    for (EffectProcessor processor : effectProcessor.values()) {
      renderStates.removeAll(processor.getRenderStatesToSave());
    }

    renderDevice.saveState(renderStates);
  }

  /**
   * finish rendering.
   * @param renderDevice RenderDevice
   */
  public void end(final NiftyRenderEngine renderDevice) {
    renderDevice.restoreState();
  }

  /**
   * render all pre effects.
   * @param renderDevice the renderDevice we should use.
   */
  public void renderPre(final NiftyRenderEngine renderDevice) {
    effectProcessor.get(EffectEventId.onHover).renderPre(renderDevice);
    effectProcessor.get(EffectEventId.onStartScreen).renderPre(renderDevice);
    effectProcessor.get(EffectEventId.onEndScreen).renderPre(renderDevice);
    effectProcessor.get(EffectEventId.onActive).renderPre(renderDevice);
    effectProcessor.get(EffectEventId.onFocus).renderPre(renderDevice);
    effectProcessor.get(EffectEventId.onClick).renderPre(renderDevice);
    effectProcessor.get(EffectEventId.onCustom).renderPre(renderDevice);
  }

  /**
   * render all post effects.
   * @param renderDevice the renderDevice we should use.
   */
  public void renderPost(final NiftyRenderEngine renderDevice) {
    effectProcessor.get(EffectEventId.onHover).renderPost(renderDevice);
    effectProcessor.get(EffectEventId.onStartScreen).renderPost(renderDevice);
    effectProcessor.get(EffectEventId.onEndScreen).renderPost(renderDevice);
    effectProcessor.get(EffectEventId.onActive).renderPost(renderDevice);
    effectProcessor.get(EffectEventId.onFocus).renderPost(renderDevice);
    effectProcessor.get(EffectEventId.onClick).renderPost(renderDevice);
    effectProcessor.get(EffectEventId.onCustom).renderPost(renderDevice);
  }

  /**
   * handle mouse hover effects.
   * @param element the current element
   * @param x mouse x position
   * @param y mouse y position
   */
  public void handleHover(final Element element, final int x, final int y) {
    EffectProcessor processor = effectProcessor.get(EffectEventId.onHover);
    processor.processHover(element, x, y);
  }

  /**
   * checks if a certain effect is active.
   * @param effectEventId the effectEventId to check
   * @return true, if active, false otherwise
   */
  public final boolean isActive(final EffectEventId effectEventId) {
    return effectProcessor.get(effectEventId).isActive();
  }

  /**
   * reset all effects.
   */
  public final void reset() {
	// onHover should stay active and is not reset
	// onActive should stay active and is not reset
	// onFocus should stay active and is not reset
    effectProcessor.get(EffectEventId.onStartScreen).reset();
    effectProcessor.get(EffectEventId.onEndScreen).reset();
    effectProcessor.get(EffectEventId.onClick).reset();
    effectProcessor.get(EffectEventId.onCustom).reset();
  }

  /**
   * set the alternate key.
   * @param newAlternateKey alternate key
   */
  public void setAlternateKey(final String newAlternateKey) {
    this.alternateKey = newAlternateKey;
  }

  /**
   * get state string.
   * @param offset offset
   * @return String with state information
   */
  public String getStateString(final String offset) {
    StringBuffer data = new StringBuffer();

    int activeProcessors = 0;
    for (EffectEventId eventId : effectProcessor.keySet()) {
      EffectProcessor processor = effectProcessor.get(eventId);
      if (processor.isActive()) {
        activeProcessors++;

        data.append("\n" + offset);
        data.append("  {" + eventId.toString() + "} ");
        data.append(processor.getStateString());
      }
    }

    if (activeProcessors == 0) {
      return "";
    } else {
      return data.toString();
    }
  }
}
