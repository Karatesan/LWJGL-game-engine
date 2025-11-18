package pl.karatesan.engine.utils;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class Renderer {

  private Mesh quadMesh;
  private Shader shader;
  private Window window;
  private Matrix4f model;
  private Matrix4f projection;

  public Renderer(Window window) {
    this.window = window;
    projection = new Matrix4f();
    model = new Matrix4f();

    init();
  }

  private void init() {
    try {
      shader = new Shader("t2d/VertexShader.txt", "t2d/FragmentShader.txt");
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
    }
    shader.use();
    updateProjection();
    shader.setUniformM4("projection", projection);

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

  public void begin(Camera2D camera) {
    glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    shader.setUniformM4("view", camera.getViewMatrix());
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

  public void updateProjection() {
    float aspect = (float) window.getWindowWidth() / window.getWindowHeight();
    projection.identity().ortho(-aspect, aspect, -1.0f, 1.0f, -1.0f, 1.0f);
  }
}
