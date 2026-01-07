package pl.karatesan.engine.gameObjects;

import org.joml.Vector2f;
import pl.karatesan.engine.texture.Texture;

public abstract class Entity {
  protected Vector2f position;
  protected float speed = 0.5f;
  protected Vector2f aimDirection;
  protected int health;
  protected Vector2f size;
  protected boolean isAlive;
  protected Texture texture;
  protected Team team;

  public Entity(
      Vector2f position,
      float speed,
      Vector2f aimDirection,
      int health,
      Vector2f size,
      Texture texture) {
    this.position = new Vector2f(position);
    this.speed = speed;
    this.aimDirection = new Vector2f(aimDirection);
    this.health = health;
    this.size = size;
    this.isAlive = true;
    this.texture = texture;
  }

  public void takeDamage(int damage, Vector2f pushback) {
    health -= damage;
    position.add(pushback);
  }

  public Vector2f getPosition() {
    return position;
  }

  public void setPosition(Vector2f position) {
    this.position = position;
  }

  public float getSpeed() {
    return speed;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public Vector2f getAimDirection() {
    return aimDirection;
  }

  public void setAimDirection(Vector2f aimDirection) {
    this.aimDirection = aimDirection;
  }

  public int getHealth() {
    return health;
  }

  public Vector2f getSize() {
    return size;
  }

  public void setSize(Vector2f size) {
    this.size = size;
  }

  public Texture getTexture() {
    return texture;
  }

  public void setTexture(Texture texture) {
    this.texture = texture;
  }

  public boolean isAlive() {
    return isAlive;
  }

  public void setIsAlive(boolean alive) {
    this.isAlive = alive;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }
}
