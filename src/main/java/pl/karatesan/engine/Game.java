package pl.karatesan.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.renderer.Renderer;
import pl.karatesan.engine.texture.TextureManager;

public class Game {
  private Camera2D camera;
  private Player player;
  private Ground ground;
  private ProjectileManager projectileManager;
  private EntityFactory entityFactory;
  private Weapon shotgun;
  private Weapon assaultRifle;

  public Game(Camera2D camera, TextureManager textureManager) {
    this.camera = camera;
    this.projectileManager = new ProjectileManager();
    this.entityFactory = new EntityFactory(textureManager);
    shotgun = entityFactory.createWeapon("shotgun", 1, 500, 30, 50, 500);
    assaultRifle = entityFactory.createWeapon("Assault rifle", 0.1f, 500, 10, 15, 500);
    player = entityFactory.createPlayer(new Vector2f(0, 0), 50, 100, new Vector2f(50, 50));
    player.setWeapon(assaultRifle);
    ground = entityFactory.createGround();
  }

  // todo bedzie trzeba colission manager, interfejs damageable z metoda takeDamage, damageManager
  // colision manager tworzy eventy - co zostalo trafione i czym, damage handler to obsluguje
  public void update(double deltaTime, GenericInputHandler input) {
    player.update(deltaTime);
    player.move(
        deltaTime, input.getMovementInput(), camera.convertScreenToWorld(input.getMousePosition()));
    if (input.isMouseLeftDown() && player.tryShoot()) {
      projectileManager.createProjectile(
          player.getWeapon(), player.getAimDirection(), player.getPlayerPosition());
    }
    camera.setPosition(player.getPlayerPosition());
    projectileManager.update(deltaTime);
  }

  public void render(Renderer renderer) {
    renderer.begin();
    renderer.drawGround(ground.getPosition(), ground.getTexture(), ground.getSize());
    renderer.drawQuad(
        player.getPlayerPosition(),
        player.getAimDirection(),
        player.getSize(),
        null,
        player.getTexture());
    for (Projectile p : projectileManager.getProjectiles()) {
      renderer.drawQuad(
          p.getPosition(),
          p.getDirection(),
          p.getSize(),
          new Vector3f(1.0f, 0.0f, 0.0f),
          p.getTexture());
    }
    renderer.end();
  }
}
