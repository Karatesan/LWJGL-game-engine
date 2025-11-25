package pl.karatesan.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.gameObjects.Player;
import pl.karatesan.engine.gameObjects.Projectile;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.renderer.Renderer;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Game {
  private Camera2D camera;
  private Player player;
  private ArrayList<Projectile> projectiles;

  public Game(Camera2D camera) {
    this.camera = camera;
    player = new Player(new Vector2f(0, 0), 0.5f);
    projectiles = new ArrayList<>();
  }

  public void handleInput(GenericInputHandler genericInputHandler) {
    player.handleInput(genericInputHandler, camera);
  }

  public void update(double deltaTime) {
    player.update(deltaTime);
    camera.setPosition(player.getPlayerPosition());

    Projectile projectile = player.tryShoot();
    if (projectile != null) {
      projectiles.add(projectile);
    }

    for (int i = projectiles.size() - 1; i >= 0; i--) {
      Projectile p = projectiles.get(i);
      p.update(deltaTime);
      if (p.shouldDestroy()) projectiles.remove(i);
    }
  }

  public void render(Renderer renderer) {
    renderer.begin();
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, renderer.getPlayerTexture());
    renderer.drawQuad(
        player.getPlayerPosition(),
        player.getAimDirection(),
        new Vector2f(0.3f, 0.3f),
        new Vector3f(0.0f, 1.0f, 0.0f));
    glBindTexture(GL_TEXTURE_2D, renderer.getBulletTexture());
    for (Projectile p : projectiles) {
      renderer.drawQuad(
          p.getPosition(),
          p.getDirection(),
          new Vector2f(0.1f, 0.1f),
          new Vector3f(1.0f, 0.0f, 0.0f));
    }
    renderer.end();
  }
}
