package pl.karatesan.engine.gameObjects.entity;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.gameObjects.weapons.Weapon;
import pl.karatesan.engine.texture.Texture;

public abstract class Enemy extends Entity {
  protected Weapon weapon;
  protected boolean inRange;

  public Enemy(
      Vector2f position,
      float speed,
      Vector2f aimDirection,
      int health,
      Vector2f size,
      Weapon weapon,
      Texture texture) {
    super(position, speed, aimDirection, health, size, Team.ENEMY, texture);
    this.weapon = weapon;
    this.team = Team.ENEMY;
  }

  public abstract void update(World world, double deltaTime);

  public abstract void move(double deltaTime);

  @Override
  public void takeDamage(int damage, Vector2f pushback) {
    super.takeDamage(damage, pushback);
    if (health <= 0) {
      isAlive = false;
    }
  }

  public Weapon getWeapon() {
    return weapon;
  }

  public void setWeapon(Weapon weapon) {
    this.weapon = weapon;
  }

  public boolean isInRange() {
    return inRange;
  }

  public void setInRange(boolean inRange) {
    this.inRange = inRange;
  }
}
