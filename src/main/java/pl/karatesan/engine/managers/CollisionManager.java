package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.gameObjects.entity.Enemy;
import pl.karatesan.engine.gameObjects.entity.Entity;
import pl.karatesan.engine.gameObjects.entity.Player;
import pl.karatesan.engine.utils.RandomService;

import java.util.List;

public class CollisionManager {

  private RandomService randomService;
  private Vector2f pushbackBuffer;

  public CollisionManager(RandomService randomService) {
    this.randomService = randomService;
    this.pushbackBuffer = new Vector2f();
  }

  public void handleProjectileHits(
          List<Entity> entities, List<Projectile> projectiles, Player player) {
    for (Projectile p : projectiles) {
      if (p.isDestroyed()) continue;
      if (p.getTeam() != player.getTeam() && entityHitCollision(p, player)) {
        player.takeDamage(p.getDamage(), null);
        p.destroyProjectile();
      } else {
        for (Entity e : entities) {
          if (p.getTeam() == e.getTeam()) continue;
          if (e.isAlive()) {
            if (entityHitCollision(p, e)) {
              int weaponPower = p.getDamage() / 4;
              float x = randomService.randFloatInRange(-1.0f, 1.0f);
              float y = randomService.randFloatInRange(-1.0f, 1.0f);
              pushbackBuffer.set(p.getDirection().x + x, p.getDirection().y + y).mul(weaponPower);
              e.takeDamage(p.getDamage(), pushbackBuffer);
              p.destroyProjectile();
            }
          }
        }
      }
    }
  }

  private boolean circleCollision(float radius1, Vector2f pos1, float radius2, Vector2f pos2) {
    float dx = pos1.x - pos2.x;
    float dy = pos1.y - pos2.y;
    double distance = dx * dx + dy * dy;
    return distance <= (radius2 + radius1) * (radius1 + radius2);
  }

  private boolean entityHitCollision(Projectile p, Entity e) {
    float radius1 = p.getSize().x / 2;
    float radius2 = e.getSize().x / 2;
    return circleCollision(radius1, p.getPosition(), radius2, e.getPosition());
  }
}
