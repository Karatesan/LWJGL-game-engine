package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.gameObjects.*;

public class CollisionManager {

  private final EnemyManager enemyManager;
  private final ProjectileManager projectileManager;

  public CollisionManager(EnemyManager enemyManager, ProjectileManager projectileManager) {
    this.enemyManager = enemyManager;
    this.projectileManager = projectileManager;
  }

  public void handleProjectileHits(Player player) {
    for (Projectile p : projectileManager.getProjectiles()) {
      if (p.isDestroyed()) continue;
      if (p.getTeam() == Team.PLAYER) {
        for (Enemy e : enemyManager.getEnemies()) {
          if (e.isAlive()) {
            if (entityHitCollision(p, e)) {
              enemyManager.takeHit(e, p.getDamage(), p.getDirection());
              p.destroyProjectile();
            }
          }
        }
      }
      if (p.getTeam() == Team.ENEMY) {
        if (entityHitCollision(p, player)) {
          player.takeDamage(p.getDamage());
          player.setLastHitDamage(p.getDamage());
          p.destroyProjectile();
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
