package pl.karatesan.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.shaders.Mesh;
import pl.karatesan.engine.shaders.Shader;
import pl.karatesan.engine.text.FontAtlas;
import pl.karatesan.engine.text.FontGlyph;
import pl.karatesan.engine.text.Text;
import pl.karatesan.engine.texture.Texture;
import pl.karatesan.engine.window.Window;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL13.*;

public class Renderer {

  private Mesh quadMesh;
  private Mesh quadTiledMesh;
  private Mesh fontMesh;
  private Shader shader;
  private Window window;
  private Matrix4f model;
  private Camera2D camera;
  private Matrix4f viewForUI;
  private Matrix4f projectionForUI;

  public Renderer(Window window, Camera2D camera) {
    this.window = window;
    model = new Matrix4f();
    this.camera = camera;
    this.viewForUI = new Matrix4f();
    this.projectionForUI =
        new Matrix4f()
            .ortho(
                0f,
                (float) window.getViewportWidth(),
                0,
                (float) window.getViewportHeight(),
                -1,
                1);
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
    quadMesh = new Mesh(new int[] {3, 2}, quadVertices, indices, false);
    fontMesh = new Mesh(new int[] {3, 2}, true);
    quadTiledMesh = new Mesh(new int[] {3, 2}, quadTiledVertices, null, false);
    shader.use();
    shader.setUniform1i("textureSampler", 0);
    shader.setUniformM4("projection", camera.getUpdatedProjectionMatrix(window));
  }

  public void begin() {
    shader.use();
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    shader.setUniformM4("view", camera.getViewMatrix());
    shader.setUniformM4("projection", camera.getUpdatedProjectionMatrix(window));

    // not needed since we use fixed projection resolution, does not change when resizing window
    //    if (window.consumeResizeFlag()) {
    //      shader.setUniformM4("projection", camera.getUpdatedProjectionMatrix(window));
    //    }
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

  public void beginRenderStaticUI() {
    shader.setUniformM4("view", viewForUI);
    shader.setUniformM4("projection", projectionForUI);
  }

  public void drawText(Text text, FontAtlas atlas) {
    model
        .identity()
        .translate(text.getPosition().x, text.getPosition().y, 0)
        .scale(text.getScale(), text.getScale(), 1);
    shader.setUniformM4("model", model);
    if (atlas.getFontTexture() != null) {
      glActiveTexture(GL_TEXTURE0);
      atlas.getFontTexture().bindTexture();
    }
    fontMesh.updateVBO(
        text.getPosition(),
        text.getGlyphs(),
        atlas.getScaleW(),
        atlas.getScaleH(),
        atlas.getBase());
    text.flushUpdate();

    fontMesh.draw();
  }

  public void drawGround(Vector2f position, Texture texture, Vector2f size) {
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
