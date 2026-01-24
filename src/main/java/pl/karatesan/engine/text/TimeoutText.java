package pl.karatesan.engine.text;

import org.joml.Vector2f;

public class TimeoutText extends Text {
  private float timeout;
  private float currentTimeoutLeft;

  public TimeoutText(
      String text,
      UIAnchor anchor,
      float xOffset,
      float yOffset,
      FontAtlas atlas,
      float scale,
      HUD hud,
      float timeout) {
    super(text, anchor, xOffset, yOffset, atlas, scale, hud);
    this.timeout = timeout;
    this.currentTimeoutLeft = timeout;
  }

    public TimeoutText(
            String text,
            Vector2f position,
            FontAtlas atlas,
            float scale,
            float timeout) {
        super(text, position, atlas, scale);
        this.timeout = timeout;
        this.currentTimeoutLeft = timeout;
    }

  public void updateTimeout(float deltaTime) {
    if (shouldRender()) {
      currentTimeoutLeft -= deltaTime;
      if (currentTimeoutLeft <= 0) {
        setShouldRender(false);
        currentTimeoutLeft = timeout;
      }
    }
  }
}
