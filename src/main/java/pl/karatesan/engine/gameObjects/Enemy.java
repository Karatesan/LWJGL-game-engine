package pl.karatesan.engine.gameObjects;

import org.joml.Vector2f;
import pl.karatesan.engine.texture.Texture;

public abstract class Enemy extends Entity {
  protected RangedWeapon weapon;
  protected boolean inRange;

  public Enemy(
      Vector2f position,
      float speed,
      Vector2f aimDirection,
      int health,
      Vector2f size,
      RangedWeapon weapon,
      Texture texture) {
    super(position, speed, aimDirection, health, size, texture);
    this.weapon = weapon;
    this.team = Team.ENEMY;
  }

  public abstract void update(double deltaTime, Vector2f playerPosition);

  public abstract void move(double deltaTime);

  public abstract boolean tryAttack();

  @Override
  public void takeDamage(int damage, Vector2f pushback) {
    super.takeDamage(damage, pushback);
    if (health <= 0) {
      isAlive = false;
    }
  }

  public RangedWeapon getWeapon() {
    return weapon;
  }

  public void setWeapon(RangedWeapon weapon) {
    this.weapon = weapon;
  }

  public boolean isInRange() {
    return inRange;
  }

  public void setInRange(boolean inRange) {
    this.inRange = inRange;
  }
}
