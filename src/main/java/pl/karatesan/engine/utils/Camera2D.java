package pl.karatesan.engine.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Camera2D {
  private static final float MIN_ZOOM = 0.1f;
  private static final float MAX_ZOOM = 10.0f;
  private Vector2f position;
  private float zoom;
  private Matrix4f view;

  public Camera2D(float positionX, float positionY) {
    position = new Vector2f(positionX, positionY);
    view = new Matrix4f();
    zoom = 1.0f;
  }

  public void move(float xOffset, float yOffset) {
    position.x += xOffset;
    position.y += yOffset;
  }

  public Matrix4f getViewMatrix() {
    return view.identity().translate(-position.x, -position.y, 0).scale(zoom);
  }

  public void zoom(float zoomOffset) {
    zoom += zoomOffset;
    if (zoom < MIN_ZOOM) zoom = MIN_ZOOM;
    if (zoom > MAX_ZOOM) zoom = MAX_ZOOM;
  }

  public float getZoom() {
    return zoom;
  }

  public Vector2f getPosition() {
    return position;
  }

  public void setPosition(Vector2f position) {
    this.position = position;
  }

  public void setPosition(float x, float y) {
    this.position.x = x;
    this.position.y = y;
  }
}
