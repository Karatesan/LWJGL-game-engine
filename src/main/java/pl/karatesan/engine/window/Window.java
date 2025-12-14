package pl.karatesan.engine.window;

import org.joml.Vector2d;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.opengl.GL;

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
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
  private long window;
  private int frameBufferWidth;
  private int frameBufferHeight;
  private int windowWidth;
  private int windowHight;
  private double[] xpos = new double[1];
  private double[] ypos = new double[1];
  private Vector2d mousePosition;
  private boolean isWindowResized;
  private final float originalAspect;

  public Window(int windowWidth, int windowHeight, String title) {
    this.isWindowResized = true; // initial flag state so renderer can set projection matrix
    this.windowHight = windowHeight;
    this.windowWidth = windowWidth;
    this.originalAspect = (float) windowWidth /windowHeight;
    this.mousePosition = new Vector2d();

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    window = glfwCreateWindow(windowWidth, windowHeight, title, NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Failed to create window: " + title);
    }

    glfwSetFramebufferSizeCallback(window, this::framebufferSizeCallback);
    glfwSetWindowSizeCallback(window, this::windowSizeCallback);

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1); // vsync (jak działa)
    glfwShowWindow(window);

    GL.createCapabilities();
  }

  private void framebufferSizeCallback(long window, int width, int height) {
    this.frameBufferWidth = width;
    this.frameBufferHeight = height;
//    float newAspect = (float) width / height;
//    int viewportW;
//    int viewportH;
//    int x = 0, y = 0;
//    if (newAspect >= originalAspect) {
//      viewportH = height;
//      viewportW = (int) (viewportH * originalAspect);
//      x = (width - viewportW) / 2;
//    } else {
//      viewportW = width;
//      viewportH = (int) (viewportW / originalAspect
//      );
//      y = (height - viewportH) / 2;
//    }
//    glViewport(x, y, viewportW, viewportH); // update OpenGL viewport
      glViewport(0,0,width,height);
    isWindowResized = true;
  }

  private void windowSizeCallback(long window, int width, int height) {
    this.windowWidth = width;
    this.windowHight = height;
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
    return windowHight;
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
}
