package pl.karatesan.engine.utils;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
  private long window;
  private int width;
  private int height;

  public Window(int windowWidth, int windowHeight, String title) {
    this.height = windowHeight;
    this.width = windowWidth;

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    window = glfwCreateWindow(windowWidth, windowHeight, title, NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Nie udało się utworzyć okna!");
    }

    glfwSetFramebufferSizeCallback(window, this::framebufferSizeCallback);

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1); // vsync (jak działa)
    glfwShowWindow(window);

    GL.createCapabilities();
  }

  private void framebufferSizeCallback(long window, int width, int height) {
    this.width = width;
    this.height = height;
    glViewport(0, 0, width, height); // update OpenGL viewport
    // Later: notify renderer to recalculate projection matrix
  }

  public void swapBuffers() {
    glfwSwapBuffers(window);
  }

  public void terminateWindow() {
    glfwDestroyWindow(window);
  }

  public double getTime() {
    return glfwGetTime();
  }

  public void pollEvents() {
    glfwPollEvents();
  }

  public boolean windowShouldClose() {
    return glfwWindowShouldClose(window);
  }

  public int getWindowHeight() {
    return height;
  }

  public int getWindowWidth() {
    return width;
  }

  public void setKeyCallback(GLFWKeyCallbackI callback) {
    glfwSetKeyCallback(window, callback);
  }
}
