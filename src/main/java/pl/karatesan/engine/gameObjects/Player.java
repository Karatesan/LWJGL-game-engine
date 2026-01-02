package pl.karatesan.engine.gameObjects;

import org.joml.Vector2f;
import pl.karatesan.engine.texture.Texture;

public class Player extends Entity {
  private RangedWeapon weapon; // coldown,damage,velocity
  private boolean wasHit;
  private int lastHitDamage;

  public Player(
      Vector2f position,
      float speed,
      Texture texture,
      Vector2f size,
      int health,
      Vector2f aimDirection) {
    super(position, speed, aimDirection, health, size, texture);
    this.wasHit = false;
  }

  @Override
  public void takeDamage(int damage) {
    health -= damage;
    wasHit = true;
    if (health <= 0) {
      isAlive = false;
    }
  }

  public boolean tryShoot() {
    return weapon.shot();
  }

  public void update(double deltaTime) {
    this.weapon.update(deltaTime);
    this.wasHit = false;
  }

  public void move(double deltaTime, Vector2f movementDirection) {
    if (movementDirection.x != 0 || movementDirection.y != 0) {
      position.add(movementDirection.mul((float) (speed * deltaTime)));
    }
  }

  public void aim(Vector2f mousePosition) {
    mousePosition.sub(position, aimDirection);
    aimDirection.normalize();
  }

  public void setWeapon(RangedWeapon weapon) {
    this.weapon = weapon;
  }

  public RangedWeapon getWeapon() {
    return weapon;
  }

  public boolean wasHit() {
    return wasHit;
  }

    public int getLastHitDamage() {
        return lastHitDamage;
    }

    public void setLastHitDamage(int lastHitDamage) {
        this.lastHitDamage = lastHitDamage;
    }
}
