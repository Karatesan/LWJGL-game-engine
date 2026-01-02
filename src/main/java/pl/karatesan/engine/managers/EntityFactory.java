package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.gameObjects.EnemyWithRangedWeapon;
import pl.karatesan.engine.gameObjects.Ground;
import pl.karatesan.engine.gameObjects.Player;
import pl.karatesan.engine.gameObjects.RangedWeapon;
import pl.karatesan.engine.texture.Texture;
import pl.karatesan.engine.texture.TextureManager;

public class EntityFactory {

  private final TextureManager textureManager;

  public EntityFactory(TextureManager textureManager) {
    this.textureManager = textureManager;
  }

  public Ground createGround() {
    Texture groundTexture = textureManager.loadGround("/grass.png");
    return new Ground(new Vector2f(0, 0), new Vector2f(5000, 5000), groundTexture);
  }

  public Player createPlayer(
      Vector2f playerPosition, float playerSpeed, int health, Vector2f size) {
    Texture playerTexture = textureManager.load("/player.png");
    return new Player(playerPosition, playerSpeed, playerTexture, size, health, new Vector2f(1, 0));
  }

  public EnemyWithRangedWeapon createEnemyWithRiffle(
      Vector2f position, Vector2f playerPosition, RangedWeapon weapon) {
    Texture texture = textureManager.load("/sprite.png");
    Vector2f aim = new Vector2f();
    playerPosition.sub(position, aim);
    aim.normalize();
    return new EnemyWithRangedWeapon(position, 25, aim, 100, new Vector2f(50, 50), weapon, texture);
  }
}
