package pl.karatesan.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.shaders.Mesh;
import pl.karatesan.engine.shaders.Shader;
import pl.karatesan.engine.texture.Texture;
import pl.karatesan.engine.window.Window;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL13.*;

public class Renderer {

  private Mesh quadMesh;
  private Mesh quadTiledMesh;
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
    shader = new Shader("t2d/VertexShaderTexture.txt", "t2d/FragmentShaderTexture.txt");

    float[] quadVertices = {
      // x,    y,    z,     u,   v
      0.5f, 0.5f, 0.0f, 1.0f, 1.0f, // Top-right
      0.5f, -0.5f, 0.0f, 1.0f, 0.0f, // Bottom-right
      -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // Bottom-left
      -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, // Top-left
    };
    float[] quadTiledVertices = {
      // x,    y,    z,     u,   v
      0.5f, 0.5f, 0.0f, 50.125f, 50.125f, // Top-right
      0.5f, -0.5f, 0.0f, 50.125f, 0.0f, // Bottom-right
      -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // Bottom-left
      -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // Bottom-left
      -0.5f, 0.5f, 0.0f, 0.0f, 50.125f, // Top-left
      0.5f, 0.5f, 0.0f, 50.125f, 50.125f // Top-right
    };
    int[] indices = {
      0, 1, 2,
      0, 2, 3
    };
    quadMesh = new Mesh(new int[] {3, 2}, quadVertices, indices);
    quadTiledMesh = new Mesh(new int[] {3, 2}, quadTiledVertices, null);
    shader.use();
    shader.setUniform1i("textureSampler", 0);
  }

  public void begin() {
    shader.use();
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    shader.setUniformM4("view", camera.getViewMatrix());
    if (window.consumeResizeFlag()) {
      shader.setUniformM4("projection", camera.getUpdatedProjectionMatrix(window));
    }
  }

  public void drawQuad(
      Vector2f position, Vector2f aimDirection, Vector2f size, Vector3f color, Texture texture) {
    float angle = (float) Math.atan2(aimDirection.y, aimDirection.x);
    model.identity().translate(position.x, position.y, 0).rotateZ(angle).scale(size.x, size.y, 1);
    shader.setUniformM4("model", model);
    if (color != null) shader.setUniform3f("color", color.x, color.y, color.z);
    if (texture != null) {
      glActiveTexture(GL_TEXTURE0);
      texture.bindTexture();
    }
    quadMesh.draw();
  }

  public void drawGround(Vector2f position, Texture texture,Vector2f size) {
      model.identity().translate(position.x, position.y, 0).scale(size.x, size.y, 1);
      shader.setUniformM4("model", model);
      if (texture != null) {
          glActiveTexture(GL_TEXTURE0);
          texture.bindTexture();
      }
      quadTiledMesh.draw();
  }

  public void end() {
    window.swapBuffers();
  }

  public void cleanup() {
    if (shader != null) shader.delete();
    if (quadMesh != null) quadMesh.cleanup();
  }
}
