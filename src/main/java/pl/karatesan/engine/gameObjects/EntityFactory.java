package pl.karatesan.engine.gameObjects;

import org.joml.Vector2f;
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

  public Player createPlayer(Vector2f playerPosition, float playerSpeed, int health, Vector2f size) {
    Texture playerTexture = textureManager.load("/player.png");
    return new Player(
        playerPosition, playerSpeed, playerTexture, size, health, new Vector2f(1, 0));
  }

  public Weapon createWeapon(
      String name, float cooldown, float velocity, int minDamage, int maxDamage, float range) {
    // weapon has no texture yet
    // weapon is created maneuall now, later ll introduce file with JSON data of all weapons
    return new Weapon(
        name, cooldown, velocity, minDamage, maxDamage, range, textureManager.load("/bullet.jpg"));
  }
}
