package pl.karatesan.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.shaders.Mesh;
import pl.karatesan.engine.shaders.Shader;
import pl.karatesan.engine.window.Window;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class Renderer {

  private Mesh quadMesh;
  private Shader shader;
  private Window window;
  private Matrix4f model;
  private Camera2D camera;

  public Renderer(Window window, Camera2D camera) {
    this.window = window;
    model = new Matrix4f();
    this.camera = camera;
    init();
  }

  private void init() throws RuntimeException {
    shader = new Shader("t2d/VertexShader.txt", "t2d/FragmentShader.txt");

    float[] vertices = {
      0.5f, 0.5f, 0.0f,
      0.5f, -0.5f, 0.0f,
      -0.5f, -0.5f, 0.0f,
      -0.5f, 0.5f, 0.0f,
    };
    int[] indices = {
      0, 1, 2,
      0, 2, 3
    };
    quadMesh = new Mesh(new int[] {3}, vertices, indices);
  }

  public void begin() {
    shader.use();
    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    shader.setUniformM4("view", camera.getViewMatrix());
    if (window.consumeResizeFlag()) {
      shader.setUniformM4("projection", camera.getUpdatedProjectionMatrix(window));
    }
  }

  public void drawQuad(Vector2f position, Vector2f size, Vector3f color) {
    model.identity().translate(position.x, position.y, 0).scale(size.x, size.y, 1);
    shader.setUniformM4("model", model);
    shader.setUniform3f("color", color.x, color.y, color.z);
    quadMesh.draw();
  }

  public void end() {
    window.swapBuffers();
  }

  public void cleanup() {
    if (shader != null) shader.delete();
    if (quadMesh != null) quadMesh.cleanup();
  }
}
