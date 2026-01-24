package pl.karatesan.engine.camera;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector4d;
import pl.karatesan.engine.utils.RandomService;
import pl.karatesan.engine.utils.Utilities;
import pl.karatesan.engine.window.Window;

public class Camera2D {
  private static final float MIN_ZOOM = 0.1f;
  private static final float MAX_ZOOM = 10.0f;
  private final int PROJECTION_WIDTH;
  private final int PROJECTION_HEIGHT;

  private Vector2f position;
  private float zoom;
  private Matrix4f view;
  private Matrix4f projection;
  private Window window;
  private boolean positionChanged;
  private Matrix4f projectionViewMatrix;
  private Vector4d clipCoords;
  private Vector2f mouseWorldPosition;
  private ShakeCamUtility shakeCamUtility;
  private RandomService randomService;

  public Camera2D(float positionX, float positionY, Window window, RandomService randomService) {
    this.projectionViewMatrix = new Matrix4f();
    this.clipCoords = new Vector4d();
    this.positionChanged = false;
    this.window = window;
    this.mouseWorldPosition = new Vector2f();
    this.shakeCamUtility = new ShakeCamUtility(randomService);
    this.randomService = randomService;
    this.PROJECTION_WIDTH = window.getProjectionWidth();
    this.PROJECTION_HEIGHT = window.getProjectionHeight();
    position = new Vector2f(positionX, positionY);
    projection = new Matrix4f();
    zoom = 1.0f;
    view = new Matrix4f().translate(-position.x, -position.y, 0).scale(zoom);
  }

  public Matrix4f getViewMatrix() {
    if (positionChanged) {
      view.identity().scale(zoom).translate(-position.x, -position.y, 0);
      positionChanged = false;
    }
    return view;
  }

  public Matrix4f getUpdatedProjectionMatrix(Window window) {
    /*
    This changes the way we generate projection - it always matches
    window size and when we lose aspect then displayed things get skewed

       float width = window.getWindowWidth();
    float height = window.getWindowHeight();
    projection.identity().ortho(-width / 2, width / 2, -height / 2, height / 2, -1, 1);
    */

    /*
    we use constant dimensions for projection calculation
     */

    projection
        .identity()
        .ortho(
            (float) -PROJECTION_WIDTH / 2,
            (float) PROJECTION_WIDTH / 2,
            (float) -PROJECTION_HEIGHT / 2,
            (float) PROJECTION_HEIGHT / 2,
            -1,
            1);

    return projection;
  }

    public Vector2f convertScreenToWorld(Vector2d mouseCoords) {
        // 1. Get Viewport metrics from Window
        // You need to expose these getters in Window class
        int vpX = window.getViewportX();
        int vpY = window.getViewportY();
        int vpW = window.getViewportWidth();
        int vpH = window.getViewportHeight();

        // 2. Adjust Mouse Coords to be relative to the Viewport
        // Note: Mouse Y is usually Top-Down, OpenGL Y is Bottom-Up.
        // However, for NDC calculation, we handle the flip manually.

        // Convert Logical Mouse (Window) to Physical Mouse (Framebuffer)
        // GLFW reports mouse in "Screen Coordinates". Framebuffer might be 2x (Retina).
        // If your viewport is calculated based on Framebuffer size, you need to scale mouse input.
        // BUT: usually glViewport uses physical pixels.

        // Let's assume mouseCoords match the scale of viewportX/Y logic.
        // If you are on Retina, windowWidth != framebufferWidth.
        // You might need: mouseX *= (frameBufferWidth / windowWidth);

        double mouseX = mouseCoords.x;
        double mouseY = mouseCoords.y;

        // Handle Retina/HighDPI scaling if needed
        float contentScaleX = (float)window.getFrameBufferWidth() / window.getWindowWidth();
        float contentScaleY = (float)window.getFrameBufferHeight() / window.getWindowHeight();
        mouseX *= contentScaleX;
        mouseY *= contentScaleY;

        // 3. Calculate NDC based on VIEWPORT, not Window
        double xNDC = 2 * (mouseX - vpX) / (double) vpW - 1;
        double yNDC = -2 * (mouseY - vpY) / (double) vpH + 1; // Flip Y here

        clipCoords.set(xNDC, yNDC, 0, 1);

        // ... matrix multiplication ...
        Matrix4f currentView = getViewMatrix();
        projection.mul(currentView, projectionViewMatrix);
        projectionViewMatrix.invert();
        clipCoords.mul(projectionViewMatrix);

        mouseWorldPosition.set((float) clipCoords.x, (float) clipCoords.y);
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

  public void setPosition(float x, float y) {
    this.position.set(x, y);
    positionChanged = true;
  }

  public void update(double deltaTime, Vector2f position) {
    Vector2f offset = shakeCamUtility.calculateShakeOffset(deltaTime);
    setPosition(position.x + offset.x, position.y + offset.y);
  }

  public void startShake(int magnitude) {
    shakeCamUtility.startShake(magnitude);
  }

  public Vector2f getPosition() {
    return position;
  }

  public int getViewWidth() {
    return PROJECTION_WIDTH;
  }

  public int getViewHeigh() {
    return PROJECTION_HEIGHT;
  }

  public Window getWindow() {
    return window;
  }
}
