package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.entity.Entity;
import pl.karatesan.engine.gameObjects.entity.Object;
import pl.karatesan.engine.gameObjects.entity.Player;
import pl.karatesan.engine.gameObjects.weapons.ArmorType;
import pl.karatesan.engine.gameObjects.weapons.WeaponUtil;
import pl.karatesan.engine.projectiles.Projectile;
import pl.karatesan.engine.utils.RandomService;

public class CollisionManager {

  public void handleProjectileHits(World world, WeaponUtil weaponUtil) {
    Player player = world.getPlayer();
    for (Projectile projectile : world.getProjectiles()) {
      if (!projectile.isDestroyed()) {
        if (projectile.getTeam() != player.getTeam() && entityHitCollision(projectile, player)) {
          if (projectile.onCollision(player)) player.takeDamage(projectile.getDamage(), null);
        } else {
          for (Entity enemy : world.getEntities()) {
            if (projectile.getTeam() != enemy.getTeam()) {
              if (enemy.isAlive() && entityHitCollision(projectile, enemy)) {
                if (projectile.onCollision(enemy)) {
                  enemy.takeDamage(
                      projectile.getDamage(),
                      weaponUtil.calculatePushBack(
                          projectile.getDamage(), projectile.getDirection()));
                  world
                      .getSoundManager()
                      //                      .playGruntAfterHitSound(enemy.getPosition())
                      .playBulletHitSound(ArmorType.FLESH, enemy.getPosition());
                }
              }
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

  public void handlePowerUpsPick(World world) {
    Player player = world.getPlayer();
    float playerRadius = player.getSize().x / 2;
    for (Object o : world.getPowerUps()) {
      float objectRadius = o.getSize().x / 2;
      if (circleCollision(playerRadius, player.getPosition(), objectRadius, o.getPosition())) {
        o.activate(player, world);
      }
    }
  }
}
