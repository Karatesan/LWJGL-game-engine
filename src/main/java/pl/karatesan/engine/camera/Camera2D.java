package pl.karatesan.engine.camera;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import pl.karatesan.engine.window.Window;

public class Camera2D {
  private static final float MIN_ZOOM = 0.1f;
  private static final float MAX_ZOOM = 10.0f;
  private Vector2f position;
  private float zoom;
  private Matrix4f view;
  private Matrix4f projection;
  private Window window;
  private boolean positionChanged;
  private Matrix4f projectionViewMatrix = new Matrix4f();
  private Vector4d clipCoords = new Vector4d();

  public Camera2D(float positionX, float positionY, Window window) {
    this.projectionViewMatrix = new Matrix4f();
    this.clipCoords = new Vector4d();
    this.positionChanged = false;
    this.window = window;
    position = new Vector2f(positionX, positionY);
    projection = new Matrix4f();
    zoom = 1.0f;
    view = new Matrix4f().translate(-position.x, -position.y, 0).scale(zoom);
  }

  public Matrix4f getViewMatrix() {
    if (positionChanged) {
      view.identity().translate(-position.x, -position.y, 0).scale(zoom);
      positionChanged = false;
    }
    return view;
  }

  public Matrix4f getUpdatedProjectionMatrix(Window window) {
    float aspect = (float) window.getWindowWidth() / window.getWindowHeight();
    projection.identity().ortho(-aspect, aspect, -1.0f, 1.0f, -1.0f, 1.0f);
    return projection;
  }

  public Vector2f convertScreenToWorld(Vector2d mouseCoords) {
    int width = window.getWindowWidth();
    int height = window.getWindowHeight();

    double xNDC = 2 * mouseCoords.x / (double) width - 1;
    double yNDC = -2 * mouseCoords.y / (double) height + 1;
    clipCoords.set(xNDC, yNDC, 0, 1);
    projection.mul(view, projectionViewMatrix);
    projectionViewMatrix.invert();
    clipCoords.mul(projectionViewMatrix);

    return new Vector2f((float) clipCoords.x, (float) clipCoords.y);
  }

  public void zoom(float zoomOffset) {
    zoom += zoomOffset;
    if (zoom < MIN_ZOOM) zoom = MIN_ZOOM;
    if (zoom > MAX_ZOOM) zoom = MAX_ZOOM;
    positionChanged = true;
  }

  public void setPosition(Vector2f position) {
    this.position.set(position);
    positionChanged = true;
  }
}
