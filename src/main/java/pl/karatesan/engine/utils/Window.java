package pl.karatesan.engine.utils;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
  private long window;
  private long windowWidth;
  private long windowHeight;

  public Window(int windowWidth, int windowHeight, String title) {
    this.windowHeight = windowHeight;
    this.windowWidth = windowWidth;

    GLFWErrorCallback.createPrint(System.err).set();

    if (!glfwInit()) {
      throw new IllegalStateException("Nie udało się zainicjalizować GLFW!");
    }

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    window = glfwCreateWindow(windowWidth, windowHeight, title, NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Nie udało się utworzyć okna!");
    }
    glfwMakeContextCurrent(window);
    glfwSwapInterval(1); // vsync (jak działa)
    glfwShowWindow(window);

    GL.createCapabilities();
  }

  public boolean isKeyPressed(int key) {
    return glfwGetKey(window, key) == GLFW_PRESS;
  }

  public void swapBuffers() {
    glfwSwapBuffers(window);
  }

  public void terminateWindow() {
    glfwDestroyWindow(window);
    glfwTerminate();
  }

  public double getTime() {
    return glfwGetTime();
  }

  public void poolEvents() {
    glfwPollEvents();
  }

  public boolean windowShouldClose() {
    return glfwWindowShouldClose(window);
  }

  public int getWindowHeight() {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer width = stack.mallocInt(1);
      IntBuffer height = stack.mallocInt(1);
      glfwGetWindowSize(window, width, height);
      return height.get();
    }
  }

  public int getWindowWidth() {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      IntBuffer width = stack.mallocInt(1);
      IntBuffer height = stack.mallocInt(1);
      glfwGetWindowSize(window, width, height);
      return width.get();
    }
  }
}
