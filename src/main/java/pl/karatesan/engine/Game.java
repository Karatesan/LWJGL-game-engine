package pl.karatesan.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import pl.karatesan.engine.utils.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
  private Window window;
  private Camera2D camera2D;
  private Vector2f playerPosition;
  private Vector2f movement;
  private float playerSpeed = 0.5f;
  private MouseHandler mouseHandler;
  private double lastTime;
  private Renderer renderer;
  private InputHandler inputHandler;

  public void run() {
    if (!glfwInit()) {
      throw new IllegalStateException("Nie udało się zainicjalizować GLFW!");
    }
    GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err).set();
    try {
      init();
      loop();
    } finally {
      cleanup(errorCallback);
    }
  }

  private void cleanup(GLFWErrorCallback errorCallback) {
    if (window != null) window.terminateWindow();
    glfwTerminate();
    errorCallback.free();
  }

  private void init() {
    playerPosition = new Vector2f(0, 0);
    movement = new Vector2f();
    window = new Window(800, 600, "2D Game");
    camera2D = new Camera2D(playerPosition.x, playerPosition.y);
    mouseHandler = new MouseHandler(window.getWindowWidth(), window.getWindowHeight());
    renderer = new Renderer(window);
    inputHandler = new InputHandler(window);
  }

  private void loop() {
    lastTime = window.getTime();

    while (!window.windowShouldClose()) {
      window.pollEvents();
      double deltaTime = calculateDeltaTime();

      handleInput();
      update(deltaTime);
      render();
    }
  }

  private void handleInput() {
    inputHandler.update();
    movement.set(0, 0);

    if (inputHandler.isKeyPressed(GLFW_KEY_W)) {
      movement.y += 1;
    }
    if (inputHandler.isKeyPressed(GLFW_KEY_S)) {
      movement.y -= 1;
    }
    if (inputHandler.isKeyPressed(GLFW_KEY_A)) {
      movement.x -= 1;
    }
    if (inputHandler.isKeyPressed(GLFW_KEY_D)) {
      movement.x += 1;
    }
  }

  private void update(double deltaTime) {
    if (movement.x != 0 || movement.y != 0) {
      playerPosition.add(movement.normalize().mul((float) (playerSpeed * deltaTime)));
    }
    camera2D.setPosition(playerPosition.x, playerPosition.y);
    System.out.println(playerPosition);
  }

  private void render() {
    renderer.begin(camera2D);
    renderer.updateProjection();
    renderer.drawQuad(playerPosition, new Vector2f(0.3f, 0.3f), new Vector3f(0.0f, 1.0f, 0.0f));
    renderer.end();
  }

  private double calculateDeltaTime() {
    double currentTime = window.getTime();
    double deltaTime = currentTime - lastTime;
    lastTime = currentTime;
    return deltaTime;
  }

  static void main(String[] args) {
    new Game().run();
  }
}
