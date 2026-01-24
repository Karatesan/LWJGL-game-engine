package pl.karatesan.engine;

import org.lwjgl.glfw.GLFWErrorCallback;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.renderer.Renderer;
import pl.karatesan.engine.sound.*;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;
import pl.karatesan.engine.window.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private AudioEngine audioEngine;

  public void init() {
    if (!glfwInit()) {
      throw new IllegalStateException("GLFW failed to initiate!");
    }
    errorCallback = GLFWErrorCallback.createPrint(System.err).set();
    randomService = new RandomService(System.currentTimeMillis());
    window = new Window("2D Game", PROJECTION_WIDTH, PROJECTION_HEIGHT);
    Camera2D camera = new Camera2D(0, 0, window, randomService);
    renderer = new Renderer(window, camera);
    genericInputHandler = new GenericInputHandler(window);
    textureManager = new TextureManager();
    this.audioEngine = new AudioEngine();
    audioEngine.init();
    Map<String, SoundEffect> stringSoundEffectMap =
        SoundConfigLoader.load(audioEngine, randomService);
    game =
        new Game(
            camera,
            textureManager,
            randomService,
            new SoundManager(audioEngine, stringSoundEffectMap));
  }

  private Map<String, SoundEffect> loadSoundEffects() {
    Map<String, SoundEffect> soundEffects = new HashMap<>();
    String shotgunShot = "shotgunShot1";
    String rifleShot = "rifleShot1";
    String assaultRifleShot = "assaultRifleShot1";
    String footstep = "footstep1";
    String themeMusic = "theme1";
    String hitGrunt1 = "hitGrunt1";
    String hitGrunt2 = "hitGrunt2";
    String hitGrunt3 = "hitGrunt3";
    String hitGrunt4 = "hitGrunt4";
    String hitGrunt5 = "hitGrunt5";
    String caseFall1 = "caseFall1";
    String caseFall2 = "caseFall2";
    String caseFall3 = "caseFall3";
    String caseFall4 = "caseFall4";
    String bulletHitFlesh1 = "bulletHitFlesh1";
    String bulletHitFlesh2 = "bulletHitFlesh2";
    String bulletHitFlesh3 = "bulletHitFlesh3";

    String die1 = "die1";

    audioEngine.loadSound(shotgunShot, "/sounds/shot/shotgunShot1.ogg");
    soundEffects.put(
        "shotgunShot",
        new SoundEffect(List.of(shotgunShot), 0.6f, VariationType.LOW, randomService));

    audioEngine.loadSound(rifleShot, "/sounds/shot/rifleShot1.ogg");
    soundEffects.put(
        "rifleShot", new SoundEffect(List.of(rifleShot), 0.6f, VariationType.LOW, randomService));

    audioEngine.loadSound(assaultRifleShot, "/sounds/shot/assaultRifleShot1.ogg");
    soundEffects.put(
        "assaultRifleShot",
        new SoundEffect(List.of(assaultRifleShot), 0.6f, VariationType.MEDIUM, randomService));

    audioEngine.loadSound(footstep, "/sounds/footsteps.ogg");
    soundEffects.put(
        "footstep", new SoundEffect(List.of(footstep), 0.5f, VariationType.HIGH, randomService));

    audioEngine.loadSound(themeMusic, "/sounds/theme2.ogg");
    soundEffects.put(
        "theme", new SoundEffect(List.of(themeMusic), 0.3f, VariationType.NONE, randomService));

    audioEngine.loadSound(hitGrunt1, "/sounds/hit/hitGrunt1.ogg");
    audioEngine.loadSound(hitGrunt2, "/sounds/hit/hitGrunt2.ogg");
    audioEngine.loadSound(hitGrunt3, "/sounds/hit/hitGrunt3.ogg");
    audioEngine.loadSound(hitGrunt4, "/sounds/hit/hitGrunt4.ogg");
    audioEngine.loadSound(hitGrunt5, "/sounds/hit/hitGrunt5.ogg");
    soundEffects.put(
        "hitGrunt",
        new SoundEffect(
            List.of(hitGrunt1, hitGrunt2, hitGrunt3, hitGrunt4, hitGrunt5),
            0.8f,
            VariationType.LOW,
            randomService));

    audioEngine.loadSound(bulletHitFlesh1, "/sounds/Hit/bulletHitFlesh1.ogg");
    audioEngine.loadSound(bulletHitFlesh2, "/sounds/Hit/bulletHitFlesh2.ogg");
    audioEngine.loadSound(bulletHitFlesh3, "/sounds/Hit/bulletHitFlesh3.ogg");
    soundEffects.put(
        "bulletHitFlesh",
        new SoundEffect(
            List.of(bulletHitFlesh1, bulletHitFlesh2, bulletHitFlesh3),
            0.4f,
            VariationType.HIGH,
            randomService));

    audioEngine.loadSound("die1", "/sounds/hit/die1.ogg");
    soundEffects.put(
        "die", new SoundEffect(List.of(die1), 0.4f, VariationType.MEDIUM, randomService));

    audioEngine.loadSound(caseFall1, "/sounds/shot/caseFall/caseFall1.ogg");
    audioEngine.loadSound(caseFall2, "/sounds/shot/caseFall/caseFall2.ogg");
    audioEngine.loadSound(caseFall3, "/sounds/shot/caseFall/caseFall3.ogg");
    audioEngine.loadSound(caseFall4, "/sounds/shot/caseFall/caseFall4.ogg");
    soundEffects.put(
        "caseFall",
        new SoundEffect(
            List.of(caseFall1, caseFall2, caseFall3, caseFall4),
            0.8f,
            VariationType.MEDIUM,
            randomService));

    return soundEffects;
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
      init();
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
