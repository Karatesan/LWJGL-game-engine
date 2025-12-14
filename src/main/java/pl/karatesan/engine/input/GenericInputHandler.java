package pl.karatesan.engine.input;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import pl.karatesan.engine.window.Window;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class GenericInputHandler {
  private boolean[] currentKeys;
  private boolean[] previousKeys;
  private boolean currentLeftMouseDown;
  private boolean previousLeftMouseDown;
  private GLFWKeyCallbackI keyCallback;
  private GLFWMouseButtonCallbackI mouseCallback;
  private Vector2d mousePosition;
  private Vector2f movementInput;
  private Map<GameAction, Integer> keyMapping;

  public GenericInputHandler(Window w) {
    this.mousePosition = new Vector2d();
    this.currentKeys = new boolean[GLFW_KEY_LAST];
    this.previousKeys = new boolean[GLFW_KEY_LAST];
    this.keyMapping = new HashMap<>();
    this.movementInput = new Vector2f();

    keyCallback =
        (long window, int key, int scancode, int action, int mods) -> {
          if (action == GLFW_PRESS) setKeyDown(key);
          if (action == GLFW_RELEASE) setKeyUp(key);
        };
    w.setKeyCallback(keyCallback);

    mouseCallback =
        (win, button, action, mods) -> {
          if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
            currentLeftMouseDown = true;
          } else if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_RELEASE) {
            currentLeftMouseDown = false;
          }
        };
    w.setMouseCallback(mouseCallback);

    keyMapping.put(GameAction.MoveUp, GLFW_KEY_W);
    keyMapping.put(GameAction.MoveRight, GLFW_KEY_D);
    keyMapping.put(GameAction.MoveDown, GLFW_KEY_S);
    keyMapping.put(GameAction.MoveLeft, GLFW_KEY_A);
    keyMapping.put(GameAction.Shoot, GLFW_MOUSE_BUTTON_1);
  }

  public void update(Window window) {
    System.arraycopy(currentKeys, 0, previousKeys, 0, currentKeys.length);
    previousLeftMouseDown = currentLeftMouseDown;
    window.pollEvents();
    mousePosition.set(window.getMousePosition());
  }

  private void setKeyDown(int key) {
    if (key >= GLFW_KEY_SPACE && key < GLFW_KEY_LAST) currentKeys[key] = true;
  }

  private void setKeyUp(int key) {
    if (key >= GLFW_KEY_SPACE && key < GLFW_KEY_LAST) currentKeys[key] = false;
  }

  public boolean isKeyPressed(int key) {
    if (key > 0 && key < currentKeys.length) return currentKeys[key];
    return false;
  }

  public boolean isMoveUpPressed() {
    return isKeyPressed(keyMapping.get(GameAction.MoveUp));
  }

  public boolean isMoveDownPressed() {
    return isKeyPressed(keyMapping.get(GameAction.MoveDown));
  }

  public boolean isMoveLeftPressed() {
    return isKeyPressed(keyMapping.get(GameAction.MoveLeft));
  }

  public boolean isMoveRightPressed() {
    return isKeyPressed(keyMapping.get(GameAction.MoveRight));
  }

  public boolean isMouseLeftDown() {
    return this.currentLeftMouseDown;
  }

  public boolean isMouseLeftHeldContinuous() {
    return this.currentLeftMouseDown && previousLeftMouseDown;
  }

  public boolean isMouseLeftJustClicked() {
    return this.currentLeftMouseDown && !previousLeftMouseDown;
  }

  public boolean isKeyJustPressed(int key) {
    if (key > 0 && key < currentKeys.length) return currentKeys[key] && !previousKeys[key];
    return false;
  }

  public boolean isKeyReleased(int key) {
    if (key > 0 && key < currentKeys.length) return !currentKeys[key] && previousKeys[key];
    return false;
  }

  public Vector2d getMousePosition() {
    return mousePosition;
  }

  public boolean isCurrentLeftMouseDown() {
    return currentLeftMouseDown;
  }

  public boolean isPreviousLeftMouseDown() {
    return previousLeftMouseDown;
  }

  public Vector2f getMovementInput() {
    movementInput.set(0, 0);
    if (isMoveUpPressed()) movementInput.y += 1;
    if (isMoveDownPressed()) movementInput.y -= 1;
    if (isMoveLeftPressed()) movementInput.x -= 1;
    if (isMoveRightPressed()) movementInput.x += 1;
    if (movementInput.x != 0 || movementInput.y != 0) movementInput.normalize();
    return movementInput;
  }
}
