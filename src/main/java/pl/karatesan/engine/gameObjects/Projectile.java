package pl.karatesan.engine.gameObjects;

import org.joml.Vector2f;

import java.text.DecimalFormat;

public class Projectile {
  private int damage;
  private Vector2f direction;
  private Vector2f origin;
  private Vector2f position;
  private float velocity;
  private float range;
  private float distanceTraveled;
  private Vector2f directionBuffer;

  public Projectile(int damage, Vector2f direction, Vector2f origin, float velocity, float range) {
    this.damage = damage;
    this.direction = direction;
    this.origin = new Vector2f(origin);
    this.position = new Vector2f(origin);
    this.velocity = velocity;
    this.range = range;
    this.directionBuffer = new Vector2f();
  }

  public void update(double deltaTime) {
    float distance = velocity * (float) deltaTime;
    direction.mul(distance, directionBuffer);
    distanceTraveled += distance;
    position.add(directionBuffer);
  }

  public boolean shouldDestroy() {
    return distanceTraveled >= range;
  }

  public Vector2f getPosition() {
    return position;
  }

  @Override
  public String toString() {
    DecimalFormat df = new DecimalFormat("#.####");
    return "Projectile{"
        + "direction="
        + df.format(direction.x)
        + " "
        + df.format(direction.y)
        + ", position="
        + df.format(position.x)
        + " "
        + df.format(position.y)
        + '}';
  }
}
