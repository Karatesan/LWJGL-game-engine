package pl.karatesan.engine.projectiles;

import org.joml.Vector2f;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.gameObjects.entity.Entity;
import pl.karatesan.engine.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class RifleProjectile extends Projectile {
  List<Entity> entitiesHit;

  public RifleProjectile(
      int damage,
      Vector2f direction,
      Vector2f origin,
      float velocity,
      float range,
      Vector2f size,
      Texture texture,
      Team team) {
    super(damage, direction, origin, velocity, range, size, texture, team);
    this.entitiesHit = new ArrayList<>();
  }

  @Override
  public boolean onCollision(Entity entity) {
    if (entitiesHit.contains(entity)) {
      return false;
    }
    entitiesHit.add(entity);
    return true;
  }
}
