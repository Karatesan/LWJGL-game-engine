package pl.karatesan.engine.text;

import org.joml.Vector2f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.utils.Utilities;
import pl.karatesan.engine.window.Window;

public class HUD {
  private Camera2D camera;

  public HUD(Camera2D camera) {
    this.camera = camera;
  }

  public Vector2f getPosition(
      UIAnchor anchor, float paddingX, float paddingY, float textWidth, float textHeight) {
    float screenW = camera.getWindow().getViewportWidth();
    float screenH = camera.getWindow().getViewportHeight();
      float x = 0;
      float y = 0;

      switch (anchor) {
          // --- TOP ROW ---
          case TOP_LEFT:
              x = paddingX;
              y = screenH - paddingY - textHeight;
              // Y is top (screenH) minus padding minus text height (to get bottom-left of text)
              break;
          case TOP_CENTER:
              x = (screenW / 2) - (textWidth / 2); // Centered X
              y = screenH - paddingY - textHeight;
              break;
          case TOP_RIGHT:
              x = screenW - paddingX - textWidth; // Right edge minus padding minus text width
              y = screenH - paddingY - textHeight;
              break;

          // --- CENTER ROW ---
          case CENTER_LEFT:
              x = paddingX;
              y = (screenH / 2) - (textHeight / 2);
              break;
          case CENTER:

              x = (screenW / 2) - (textWidth / 2);
              y = (screenH / 2) - (textHeight / 2);
              System.out.println(screenH+ " "+ screenW + " "+ x +" "+ y + " width=" + textWidth);
              break;
          case CENTER_RIGHT:
              x = screenW - paddingX - textWidth;
              y = (screenH / 2) - (textHeight / 2);
              break;

          // --- BOTTOM ROW ---
          case BOTTOM_LEFT:
              x = paddingX;
              y = paddingY; // Bottom is 0, so just padding
              break;
          case BOTTOM_CENTER:
              x = (screenW / 2) - (textWidth / 2);
              y = paddingY;
              break;
          case BOTTOM_RIGHT:
              x = screenW - paddingX - textWidth;
              y = paddingY;
              break;
      }

    return new Vector2f(x, y);
  }
}
