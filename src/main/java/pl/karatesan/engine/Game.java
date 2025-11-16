package pl.karatesan.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.utils.Camera2D;
import pl.karatesan.engine.utils.MouseHandler;
import pl.karatesan.engine.utils.Renderer;
import pl.karatesan.engine.utils.Window;

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

  public void run() {
    init();
    loop();
    window.terminateWindow();
  }

  private void init() {
    playerPosition = new Vector2f(0, 0);
    movement = new Vector2f();
    window = new Window(800, 600, "2D Game");
    camera2D = new Camera2D(playerPosition.x, playerPosition.y);
    mouseHandler = new MouseHandler(window.getWindowWidth(), window.getWindowHeight());
    renderer = new Renderer(window, camera2D);
  }

  private void loop() {
    lastTime = window.getTime();

    while (!window.windowShouldClose()) {
      window.poolEvents();
      double deltaTime = calculateDeltaTime();

      handleInput();
      update(deltaTime);
      render();
    }
  }

  private void handleInput() {
    movement.set(0, 0);

    // handle mouse
    // Vector2d offset = mouseHandler.handleMouseRotation(window.getWindow());

    if (window.isKeyPressed(GLFW_KEY_W)) {
      movement.y += 1;
    }
    if (window.isKeyPressed(GLFW_KEY_S)) {
      movement.y -= 1;
    }
    if (window.isKeyPressed(GLFW_KEY_A)) {
      movement.x -= 1;
    }
    if (window.isKeyPressed(GLFW_KEY_D)) {
      movement.x += 1;
    }
  }

  private void update(double deltaTime) {
    if (movement.x != 0 || movement.y != 0) {
      playerPosition.add(movement.normalize().mul((float) (playerSpeed * deltaTime)));
    }
    camera2D.setPosition(playerPosition.x, playerPosition.y);
  }

  private void render() {
    renderer.begin();
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
