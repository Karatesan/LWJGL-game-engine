package pl.karatesan.engine.gameObjects;

import org.joml.Vector2d;
import org.joml.Vector2f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.texture.Texture;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.Utilities;

import java.text.DecimalFormat;

public class Player {
  private Vector2f playerPosition;
  private float playerSpeed = 0.5f;
  private Vector2f aimDirection;
  private int health;
  private Weapon weapon; // coldown,damage,velocity
  private Vector2f movement;
  private Vector2f size;
  private Texture texture;

  public Player(
      Vector2f playerPosition,
      float playerSpeed,
      Texture texture,
      Vector2f size,
      int health,
      Vector2f aimDirection) {
    this.playerPosition = playerPosition;
    this.playerSpeed = playerSpeed;
    this.texture = texture;
    this.size = size;
    this.health = health;
    this.aimDirection = aimDirection;
    this.movement = new Vector2f();
  }

  public Vector2f getSize() {
    return size;
  }

  public Vector2f getPlayerPosition() {
    return playerPosition;
  }

  public void setPlayerPosition(Vector2f playerPosition) {
    this.playerPosition = playerPosition;
  }

  public float getPlayerSpeed() {
    return playerSpeed;
  }

  public void setPlayerSpeed(float playerSpeed) {
    this.playerSpeed = playerSpeed;
  }

  public boolean tryShoot() {
    return weapon.shot();
  }

  public void update(double deltaTime) {
    this.weapon.update(deltaTime);
  }

  public void move(double deltaTime, Vector2f movementDirection, Vector2f mousePosition) {
    if (movementDirection.x != 0 || movementDirection.y != 0) {
      playerPosition.add(movementDirection.mul((float) (playerSpeed * deltaTime)));
    }
    Vector2f aim = new Vector2f();
    mousePosition.sub(playerPosition, aim);
    aimDirection.set(aim).normalize(); // TODO we modify here mousePosition from game
  }

  public void takeDamage(int damage) {
    health -= damage;
  }

  public Vector2f getAimDirection() {
    return aimDirection;
  }

  public Texture getTexture() {
    return texture;
  }

  public void setWeapon(Weapon weapon) {
    this.weapon = weapon;
  }

  public Weapon getWeapon() {
    return weapon;
  }
}
