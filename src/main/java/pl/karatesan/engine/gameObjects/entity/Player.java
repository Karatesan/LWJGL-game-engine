package pl.karatesan.engine.gameObjects.entity;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.gameObjects.weapons.Weapon;
import pl.karatesan.engine.texture.Texture;

public class Player extends Entity {
  private Weapon weapon; // coldown,damage,velocity
  private boolean wasHit;
  private int lastHitDamage;
  private Vector2f moveBuffer;

  public Player(
      Vector2f position,
      float speed,
      Texture texture,
      Vector2f size,
      int health,
      Vector2f aimDirection) {
    super(position, speed, aimDirection, health, size, Team.PLAYER, texture);
    this.wasHit = false;
    this.moveBuffer = new Vector2f();
    this.team = Team.PLAYER;
  }

  @Override
  public void takeDamage(int damage, Vector2f pushback) {
    health -= damage;
    wasHit = true;
    this.lastHitDamage = damage;
    if (health <= 0) {
      isAlive = false;
    }
  }

  public void tryShoot(World world) {
     weapon.tryShoot(world,position,aimDirection,Team.PLAYER);
  }

  public void update(World world, double deltaTime) {
    this.weapon.update(deltaTime);
    this.wasHit = false;
  }

  public void move(double deltaTime, Vector2f movementDirection) {
    if (movementDirection.x != 0 || movementDirection.y != 0) {
      position.add(movementDirection.mul((float) (speed * deltaTime), moveBuffer));
    }
  }

  public void aim(Vector2f mousePosition) {
    mousePosition.sub(position, aimDirection);
    aimDirection.normalize();
  }

  public void setWeapon(Weapon weapon) {
    this.weapon = weapon;
  }

  public Weapon getWeapon() {
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
