package pl.karatesan.engine.window;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

  private long window;
  private int frameBufferWidth;
  private int frameBufferHeight;
  private int windowWidth;
  private int windowHeight;
  int viewportWidth;
  int viewportHeight;
  int viewportX = 0;
  int viewportY = 0;
  private double[] xpos = new double[1];
  private double[] ypos = new double[1];
  private Vector2d mousePosition;
  private boolean isWindowResized;
  private final float originalAspect;
  private final int PROJECTION_WIDTH;
  private final int PROJECTION_HEIGHT;

  public Window(String title, int projectionWidth, int projectionHeight) {
    this.isWindowResized = true; // initial flag state so renderer can set projection matrix
    this.originalAspect = (float) projectionWidth / projectionHeight;
    this.mousePosition = new Vector2d();
    this.PROJECTION_HEIGHT = projectionHeight;
    this.PROJECTION_WIDTH = projectionWidth;

    // 1. Get the monitor and its current video mode (resolution/refresh rate)
    long monitor = glfwGetPrimaryMonitor();
    GLFWVidMode vidMode = glfwGetVideoMode(monitor);

    // 2. Configure Hints BEFORE creation
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_DECORATED, GLFW_FALSE); // Remove borders/title bar
    glfwWindowHint(GLFW_RED_BITS, vidMode.redBits());
    glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits());
    glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits());
    glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate());

    // 3. Create Window using the Monitor's resolution
    // window = glfwCreateWindow(vidMode.width(), vidMode.height(), title, NULL, NULL);

    // 4. (Optional) If you want Exclusive Fullscreen instead:
    window = glfwCreateWindow(vidMode.width(), vidMode.height(), title, monitor, NULL);

    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1);
      IntBuffer pHeight = stack.mallocInt(1);

      glfwGetFramebufferSize(window, pWidth, pHeight);
      this.frameBufferWidth = pWidth.get(0);
      this.frameBufferHeight = pHeight.get(0);
      glfwGetWindowSize(window, pWidth, pHeight);
      this.windowWidth = pWidth.get(0);
      this.windowHeight = pHeight.get(0);
    }
    if (window == NULL) {
      throw new RuntimeException("Failed to create window: " + title);
    }

    glfwSetFramebufferSizeCallback(window, this::framebufferSizeCallback);
    glfwSetWindowSizeCallback(window, this::windowSizeCallback);

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1); // vsync (jak działa)

    glfwShowWindow(window);
    glfwFocusWindow(window);
    GL.createCapabilities();
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    calculateViewport(frameBufferWidth, frameBufferHeight);
    glfwPollEvents(); // Flush startup events
    glfwFocusWindow(window);
  }

  // framebuffer is physicall pixels of display
  // current implementation keeps static projection width and height
  // meaning that each window resize we have to recalculate viewport to keep correct aspect ratio
  private void framebufferSizeCallback(long window, int width, int height) {
    this.frameBufferWidth = width;
    this.frameBufferHeight = height;
    calculateViewport(width, height);
  }

  private void calculateViewport(int frameBufferWidth, int frameBufferHeight) {
    float newAspect = (float) frameBufferWidth / frameBufferHeight;
    if (newAspect >= originalAspect) {
      viewportHeight = frameBufferHeight;
      viewportWidth = (int) (viewportHeight * originalAspect);
      viewportX = (frameBufferWidth - viewportWidth) / 2;
    } else {
      viewportWidth = frameBufferWidth;
      viewportHeight = (int) (viewportWidth / originalAspect);
      viewportY = (frameBufferHeight - viewportHeight) / 2;
    }
    glViewport(viewportX, viewportY, viewportWidth, viewportHeight); // update OpenGL viewport

    // when we resize framebuffer and projection to window size.
    // compared to current implementation with resize of window field of view gets bigger
    // (currently its static we kinda scale everything up/down)
    //    glViewport(0, 0, width, height);
    // isWindowResized = true;
  }

  private void windowSizeCallback(long window, int width, int height) {
    this.windowWidth = width;
    this.windowHeight = height;
  }

  public void swapBuffers() {
    glfwSwapBuffers(window);
  }

  public void terminateWindow() {
    glfwDestroyWindow(window);
  }

  public void pollEvents() {
    glfwPollEvents();
  }

  public boolean windowShouldClose() {
    return glfwWindowShouldClose(window);
  }

  public int getWindowHeight() {
    return windowHeight;
  }

  public int getWindowWidth() {
    return windowWidth;
  }

  public void setKeyCallback(GLFWKeyCallbackI callback) {
    glfwSetKeyCallback(window, callback);
  }

  public void setMouseCallback(GLFWMouseButtonCallbackI callback) {
    glfwSetMouseButtonCallback(window, callback);
  }

  public Vector2d getMousePosition() {
    glfwGetCursorPos(window, xpos, ypos);
    mousePosition.set(xpos[0], ypos[0]);
    return mousePosition;
  }

  public boolean consumeResizeFlag() {
    boolean wasResized = isWindowResized;
    isWindowResized = false;
    return wasResized;
  }

  public int getProjectionWidth() {
    return PROJECTION_WIDTH;
  }

  public int getProjectionHeight() {
    return PROJECTION_HEIGHT;
  }

  public int getViewportWidth() {
    return viewportWidth;
  }

  public int getViewportHeight() {
    return viewportHeight;
  }

  public int getViewportX() {
    return viewportX;
  }

  public int getViewportY() {
    return viewportY;
  }

  public int getFrameBufferWidth() {
    return frameBufferWidth;
  }

  public int getFrameBufferHeight() {
    return frameBufferHeight;
  }
}
