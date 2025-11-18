package pl.karatesan.engine.utils;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
  private boolean[] currentKeys;
  private boolean[] previousKeys;
  private GLFWKeyCallbackI keyCallback;

  public InputHandler(Window w) {
    this.currentKeys = new boolean[GLFW_KEY_LAST];
    this.previousKeys = new boolean[GLFW_KEY_LAST];

    keyCallback =
        (long window, int key, int scancode, int action, int mods) -> {
          if (action == GLFW_PRESS) setKeyDown(key);
          if (action == GLFW_RELEASE) setKeyUp(key);
        };
    w.setKeyCallback(keyCallback);
  }

  public void update() {
    previousKeys = Arrays.copyOf(currentKeys, GLFW_KEY_LAST);
  }

  private void setKeyDown(int key) {
    if (key >= GLFW_KEY_SPACE && key <= GLFW_KEY_LAST) currentKeys[key] = true;
  }

  private void setKeyUp(int key) {
    if (key >= GLFW_KEY_SPACE && key <= GLFW_KEY_LAST) currentKeys[key] = false;
  }

  public boolean isKeyPressed(int key) {
    return currentKeys[key];
  }

  public boolean isKeyJustPressed(int key) {
    return currentKeys[key] && !previousKeys[key];
  }

  public boolean isKeyReleased(int key){
      return !currentKeys[key] && previousKeys[key];
  }
}
