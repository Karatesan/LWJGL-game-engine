package pl.karatesan.engine;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.managers.CollisionManager;
import pl.karatesan.engine.renderer.Renderer;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;
import pl.karatesan.engine.window.Window;

import static org.lwjgl.glfw.GLFW.*;

public class Engine {

  private static final int PROJECTION_WIDTH = 1600;
  private static final int PROJECTION_HEIGHT = 900;

  private Window window;
  private Renderer renderer;
  private GenericInputHandler genericInputHandler;
  private GLFWErrorCallback errorCallback;
  private Game game;
  private double lastTime;
  private TextureManager textureManager;
  private RandomService randomService;

  public void init(int windowWidth, int windowHeight) {
    if (!glfwInit()) {
      throw new IllegalStateException("GLFW failed to initiate!");
    }
    errorCallback = GLFWErrorCallback.createPrint(System.err).set();
    randomService = new RandomService(System.currentTimeMillis());
    window = new Window(windowWidth, windowHeight, "2D Game", PROJECTION_WIDTH, PROJECTION_HEIGHT);
    Camera2D camera = new Camera2D(0, 0, window, randomService);
    renderer = new Renderer(window, camera);
    genericInputHandler = new GenericInputHandler(window);
    textureManager = new TextureManager();
    game = new Game(camera, textureManager, randomService);
  }

  public void gameLoop() {
    lastTime = getTime();
    while (!window.windowShouldClose()) {
      double deltaTime = calculateDeltaTime();
      genericInputHandler.update(window);
      game.update(deltaTime, genericInputHandler);
      game.render(renderer);
    }
  }

  public double getTime() {
    return glfwGetTime();
  }

  private double calculateDeltaTime() {
    double currentTime = getTime();
    double deltaTime = currentTime - lastTime;
    lastTime = currentTime;
    return deltaTime;
  }

  public void run() {
    try {
      init(800, 600);
      gameLoop();
    } catch (RuntimeException ex) {
      System.err.println("Engine failed to initiate: " + ex.getMessage());
    } finally {
      cleanup();
    }
  }

  public void cleanup() {
    if (renderer != null) renderer.cleanup(); // Free OpenGL resources FIRST
    if (window != null) window.terminateWindow();
    if (textureManager != null) textureManager.cleanup();
    glfwTerminate();
    errorCallback.free(); // Free callbacks last
  }

  public static void main(String[] args) {
    new Engine().run();
  }
}
