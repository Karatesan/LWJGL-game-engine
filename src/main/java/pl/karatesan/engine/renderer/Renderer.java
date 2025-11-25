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

public class Renderer {

  private Mesh quadMesh;
  private Shader shader;
  private Window window;
  private Matrix4f model;
  private Camera2D camera;
  private Texture playerTexture;
  private Texture bulletTexture;

  public Renderer(Window window, Camera2D camera) {
    this.window = window;
    model = new Matrix4f();
    this.camera = camera;
    init();
  }

  private void init() throws RuntimeException {
    shader = new Shader("t2d/VertexShaderTexture.txt", "t2d/FragmentShaderTexture.txt");

    float[] vertices = {
      // x,    y,    z,     u,   v
      0.5f, 0.5f, 0.0f, 1.0f, 1.0f, // Top-right
      0.5f, -0.5f, 0.0f, 1.0f, 0.0f, // Bottom-right
      -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // Bottom-left
      -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, // Top-left
    };
    int[] indices = {
      0, 1, 2,
      0, 2, 3
    };
    quadMesh = new Mesh(new int[] {3, 2}, vertices, indices);

    this.playerTexture = new Texture("/player.png");
    this.bulletTexture = new Texture("/bullet.jpg");
    shader.use();
    shader.setUniform1i("textureSampler", 0);
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

  public void drawQuad(Vector2f position, Vector2f aimDirection, Vector2f size, Vector3f color) {
    float angle = (float) Math.atan2(aimDirection.y, aimDirection.x);
    model.identity().translate(position.x, position.y, 0).rotateZ(angle).scale(size.x, size.y, 1);
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

  public int getPlayerTexture() {
    return playerTexture.getTextureId();
  }

  public int getBulletTexture() {
    return bulletTexture.getTextureId();
  }
}
