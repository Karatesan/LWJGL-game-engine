package pl.karatesan.engine.gameObjects;

import org.joml.Vector2d;
import org.joml.Vector2f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.input.GenericInputHandler;

public class Player {
  private Vector2f playerPosition;
  private float playerSpeed = 0.5f;
  private Vector2f aimDirection;
  private boolean triggerPulled;
  private int health;
  private Weapon weapon; // coldown,damage,velocity
  private Vector2f movement;

  public Player(Vector2f playerPosition, float playerSpeed) {
    this.health = 100;
    this.playerPosition = playerPosition;
    this.playerSpeed = playerSpeed;
    this.movement = new Vector2f();
    this.aimDirection = new Vector2f(1, 0);
    this.weapon = new Weapon("Shotgun", 1.0f, 0.5f, 20, 50, 1);
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

  public void update(double deltaTime) {
    move(deltaTime);
    weapon.update(deltaTime);
  }

  public Projectile tryShoot() {
    if (triggerPulled && weapon.canShot()) {
      return shoot();
    }
    return null;
  }

  private void move(double deltaTime) {
    if (movement.x != 0 || movement.y != 0) {
      playerPosition.add(movement.normalize().mul((float) (playerSpeed * deltaTime)));
    }
  }

  public void takeDamage(int damage) {
    health -= damage;
  }

  public Projectile shoot() {
    return weapon.shot(aimDirection, playerPosition);
  }

  public void handleInput(GenericInputHandler genericInputHandler, Camera2D camera) {
    movement.set(0, 0);
    if (genericInputHandler.isMoveUpPressed()) movement.y += 1;
    if (genericInputHandler.isMoveDownPressed()) movement.y -= 1;
    if (genericInputHandler.isMoveLeftPressed()) movement.x -= 1;
    if (genericInputHandler.isMoveRightPressed()) movement.x += 1;
    Vector2d mousePosition = genericInputHandler.getMousePosition();
    aimDirection.set(camera.convertScreenToWorld(mousePosition).sub(playerPosition)).normalize();
    triggerPulled = genericInputHandler.isMouseLeftJustClicked();
  }

    public Vector2f getAimDirection() {
      return aimDirection;
    }
}
