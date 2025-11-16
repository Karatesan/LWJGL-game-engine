package pl.karatesan.t3d;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import pl.karatesan.engine.utils.Shader;
import pl.karatesan.dump.Texture;
import pl.karatesan.engine.utils.Camera3D;
import pl.karatesan.engine.utils.MouseHandler;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Go3d {
  private final int screenWidth = 800;
  private final int screenHeight = 600;
  private long window;
  private int[] VAOs = new int[2];
  private int[] VBOs = new int[2];
  private Texture[] textures = new Texture[2];
  private Shader shader;
  private float movementSpeed = 1.5f;
  private Camera3D camera3D;
  private MouseHandler mouseHandler;

  double[] xpos = new double[1];
  double[] ypos = new double[1];

  double fov = 45;

  // player
  Vector3f playerPosition;

  public void run() {
    init();
    loop();
    glfwDestroyWindow(window);
    glfwTerminate();
  }

  private void init() {
    // Obsługa błędów GLFW
    GLFWErrorCallback.createPrint(System.err).set();

    if (!glfwInit()) {
      throw new IllegalStateException("Nie udało się zainicjalizować GLFW!");
    }

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

    window = glfwCreateWindow(screenWidth, screenHeight, "VAO/VBO Example", NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Nie udało się utworzyć okna!");
    }

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1); // vsync (jak działa)
    glfwShowWindow(window);

    GL.createCapabilities();

    // =======================================================================

    try {
      shader = new Shader("t3d/VertexShader.txt", "t3d/FragmentShader.txt");
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
    }
    // --- Wierzchołki kwadratu ---
    float vertices[] = {
      // positions          // colors           // texture coords (note that we changed them to
      // 'zoom in' on our texture image)
      0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
      0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
      -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom left
      -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f // top left
    };
    float vertices3d[] = {
      -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, 0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.5f, 0.5f, -0.5f, 1.0f,
      1.0f, 0.5f, 0.5f, -0.5f, 1.0f, 1.0f, -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, -0.5f, -0.5f, -0.5f,
      0.0f, 0.0f, -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.5f, 0.5f, 0.5f,
      1.0f, 1.0f, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f, -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, -0.5f, -0.5f, 0.5f,
      0.0f, 0.0f, -0.5f, 0.5f, 0.5f, 1.0f, 0.0f, -0.5f, 0.5f, -0.5f, 1.0f, 1.0f, -0.5f, -0.5f,
      -0.5f, 0.0f, 1.0f, -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, -0.5f,
      0.5f, 0.5f, 1.0f, 0.0f, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 0.5f,
      -0.5f, -0.5f, 0.0f, 1.0f, 0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.5f,
      0.5f, 0.5f, 1.0f, 0.0f, -0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.5f, -0.5f, -0.5f, 1.0f, 1.0f, 0.5f,
      -0.5f, 0.5f, 1.0f, 0.0f, 0.5f, -0.5f, 0.5f, 1.0f, 0.0f, -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, -0.5f,
      -0.5f, -0.5f, 0.0f, 1.0f, -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.5f, 0.5f, -0.5f, 1.0f, 1.0f, 0.5f,
      0.5f, 0.5f, 1.0f, 0.0f, 0.5f, 0.5f, 0.5f, 1.0f, 0.0f, -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, -0.5f,
      0.5f, -0.5f, 0.0f, 1.0f
    };
    System.out.println(vertices3d.length/5);
    int[] indices = {0, 1, 2, 0, 2, 3};

    // Textures

    textures[0] = new Texture(GL_TEXTURE_2D, "/container.jpg", GL_CLAMP_TO_EDGE, GL_LINEAR);
    textures[1] = new Texture(GL_TEXTURE_2D, "/awesomeface.png", GL_REPEAT, GL_LINEAR);

    glEnable(GL_DEPTH_TEST);
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

    shader.use();
    shader.setUniform1i("ourTexture1", 0);
    shader.setUniform1i("ourTexture2", 1);

    // transforms

    VAOs[0] = glGenVertexArrays();
    glBindVertexArray(VAOs[0]);

    VBOs[0] = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, VBOs[0]);
    GL15.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 32, 24);

    int ebo = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    VAOs[1] = glGenVertexArrays();
    glBindVertexArray(VAOs[1]);

    VBOs[1] = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, VBOs[1]);
    GL15.glBufferData(GL_ARRAY_BUFFER, vertices3d, GL_STATIC_DRAW);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);

    // camera
    camera3D =
        new Camera3D(new Vector3f(0, 0, -3), new Vector3f(0, 1, 0), new Vector3f(0, 0, -1));
    mouseHandler = new MouseHandler(screenWidth, screenHeight);
  }

  private void loop() {
    double lastTime = glfwGetTime();

    Vector3f[] cubePositions = {
      new Vector3f(0.0f, 0.0f, 0.0f),
      new Vector3f(2.0f, 5.0f, -15.0f),
      new Vector3f(-1.5f, -2.2f, -2.5f),
      new Vector3f(-3.8f, -2.0f, -12.3f),
      new Vector3f(2.4f, -0.4f, -3.5f),
      new Vector3f(-1.7f, 3.0f, -7.5f),
      new Vector3f(1.3f, -2.0f, -2.5f),
      new Vector3f(1.5f, 2.0f, -2.5f),
      new Vector3f(1.5f, 0.2f, -1.5f),
      new Vector3f(-1.3f, 1.0f, -1.5f)
    };
    playerPosition = new Vector3f(0.0f, 0.0f, 3.0f);
    Matrix4f model = new Matrix4f();
    glfwSetScrollCallback(
        window,
        new GLFWScrollCallback() {
          @Override
          public void invoke(long window, double xoffset, double yoffset) {
            fov -= (float) yoffset;
            if (fov < 1.0f) fov = 1.0f;
            if (fov > 90.0f) fov = 90.0f;
          }
        });
    Matrix4f projection = new Matrix4f();

    while (!glfwWindowShouldClose(window)) {
      double currentTime = glfwGetTime();
      double deltaTime = currentTime - lastTime;
      lastTime = currentTime;
      glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      // set texture units
      glActiveTexture(GL_TEXTURE0);
      glBindTexture(GL_TEXTURE_2D, textures[0].getTextureId());
      glActiveTexture(GL_TEXTURE1);
      glBindTexture(GL_TEXTURE_2D, textures[1].getTextureId());

      projection
          .identity()
          .perspective((float) Math.toRadians(fov), (float) 800 / 600, 0.1f, 100.0f);
      shader.setUniformM4("projection", projection);
      handleInput((float) (deltaTime * movementSpeed), camera3D);
      camera3D.moveCamera(playerPosition);

      shader.setUniformM4("view", camera3D.getViewMatrix());

      shader.use();
      glBindVertexArray(VAOs[1]);

      for (int i = 0; i < cubePositions.length; ++i) {
        model.identity().translate(cubePositions[i]);
        float angle = 20 * i;
        if (i % 3 == 0) {
          model.rotate(
              (float) Math.toRadians(angle * glfwGetTime()),
              new Vector3f(1.0f, 1.0f, 0.0f).normalize());
        }
        shader.setUniformM4("model", model);
        glDrawArrays(GL_TRIANGLES, 0, 36);
      }

      glBindVertexArray(0);
      glfwSwapBuffers(window);
      glfwPollEvents();
    }
  }

  private void handleInput(float speed, Camera3D camera3D) {
    Vector3f rightVector = new Vector3f();
    Vector3f movement = new Vector3f();

    // handle view
    Vector2d offset = mouseHandler.handleMouseRotation(window);
    camera3D.handleLook(offset.x, offset.y);
    Vector3f dir = camera3D.getDirection();
    Vector3f up = camera3D.getUp();

    if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
      movement.add(dir);
    }
    if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
      movement.sub(dir);
    }
    if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
      dir.cross(up, rightVector);
      rightVector.normalize();
      movement.sub(rightVector);
    }
    if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
      dir.cross(up, rightVector);
      rightVector.normalize();
      movement.add(rightVector);
    }
    if (movement.x != 0 || movement.y != 0 || movement.z != 0) {
      movement.y = 0;
      playerPosition.add(movement.normalize().mul(speed));
    }
  }

  static void main(String[] args) {
    new Go3d().run();
  }
}
