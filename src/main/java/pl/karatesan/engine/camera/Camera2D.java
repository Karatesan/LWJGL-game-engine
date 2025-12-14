package pl.karatesan.engine.camera;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import pl.karatesan.engine.utils.Utilities;
import pl.karatesan.engine.window.Window;

public class Camera2D {
  private static final float MIN_ZOOM = 0.1f;
  private static final float MAX_ZOOM = 10.0f;
  private static final float PROJECTION_WIDTH = 800;
  private static final float PROJECTION_HEIGHT = 600;

  private Vector2f position;
  private float zoom;
  private Matrix4f view;
  private Matrix4f projection;
  private Window window;
  private boolean positionChanged;
  private Matrix4f projectionViewMatrix;
  private Vector4d clipCoords;
  private Vector2f mouseWorldPosition;

  public Camera2D(float positionX, float positionY, Window window) {
    this.projectionViewMatrix = new Matrix4f();
    this.clipCoords = new Vector4d();
    this.positionChanged = false;
    this.window = window;
    this.mouseWorldPosition = new Vector2f();
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
    /*
    This changes the way we generate projection - it always matches
    window size and when we lose aspect then displayed things get skewed
    */

    float width = window.getWindowWidth();
    float height = window.getWindowHeight();
    projection.identity().ortho(-width / 2, width / 2, -height / 2, height / 2, -1, 1);

    /*
    we use constant dimensions for projection calculation
       projection
           .identity()
           .ortho(
               -PROJECTION_WIDTH / 2,
               PROJECTION_WIDTH / 2,
               -PROJECTION_HEIGHT / 2,
               PROJECTION_HEIGHT / 2,
               -1,
               1);
    */
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
    mouseWorldPosition.set((float) clipCoords.x, (float) clipCoords.y);
    Utilities.printVector2WithDelay(160, mouseWorldPosition, "Mouse World Position: ");
    Utilities.printVector2WithDelay(160, mouseCoords, "Mouse  Position: ");
    return new Vector2f(mouseWorldPosition);
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
