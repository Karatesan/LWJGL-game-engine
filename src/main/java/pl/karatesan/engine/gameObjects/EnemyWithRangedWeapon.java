package pl.karatesan.engine.gameObjects;

import org.joml.Vector2f;
import pl.karatesan.engine.texture.Texture;

public class EnemyWithRangedWeapon extends Enemy {

  private Vector2f aimBuffer;

  public EnemyWithRangedWeapon(
      Vector2f position,
      float speed,
      Vector2f aimDirection,
      int health,
      Vector2f size,
      RangedWeapon weapon,
      Texture texture) {
    super(position, speed, aimDirection, health, size, weapon, texture);
    this.aimBuffer = new Vector2f();
  }

  public void update(double deltaTime, Vector2f playerPosition) {
    this.weapon.update(deltaTime);
    float range = Vector2f.distance(playerPosition.x, playerPosition.y, position.x, position.y);
    inRange = range < weapon.getRange();
    playerPosition.sub(position, aimBuffer);
    aimBuffer.normalize();
    aimDirection.set(aimBuffer);
  }

  public void move(double deltaTime) {
    if (!inRange) {
      aimBuffer.mul((float) (speed * deltaTime));
      position.add(aimBuffer);
    }
  }

  public boolean tryAttack() {
    if (inRange) return weapon.shot();
    return false;
  }

  public RangedWeapon getWeapon() {
    return weapon;
  }
}
