package pl.karatesan.engine.gameObjects.weapons;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.projectiles.Projectile;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.texture.Texture;

public class Shotgun extends Weapon {

  private final float shotgunSpread = 70;
  private final float coneAngle = 0.4f;
  private final int pelletCount = 5;
  private Vector2f projectileAngleBuffer = new Vector2f();
  private final Vector2f tempDirectionBuffer = new Vector2f();

  public Shotgun(
      float cooldown,
      Texture projectileTexture,
      int minDamage,
      int maxDamage,
      float projectileVelocity,
      float range) {
    super(
        cooldown,
        projectileTexture,
        minDamage,
        maxDamage,
        projectileVelocity,
        range,
        WeaponType.SHOTGUN);
  }

  public void createProjectiles(World world, Vector2f origin, Vector2f direction, Team team) {
    // spread from -.3 to .3
    float startAngle = -coneAngle / 2;
    float increment = -2 * startAngle / (pelletCount - 1);
    Vector2f perpendicularDir = new Vector2f(-direction.y, direction.x);
    for (int i = 0; i < pelletCount; i++) {
      Vector2f pelletDirection = new Vector2f();
      float cos = (float) Math.cos(startAngle + i * increment);
      float sin = (float) Math.sin(startAngle + i * increment);
      direction.mul(cos, pelletDirection);
      perpendicularDir.mul(sin, projectileAngleBuffer);
      pelletDirection.add(projectileAngleBuffer).normalize();

      float randPelletOrigin = world.getRandomService().randFloat() * shotgunSpread;

      pelletDirection.mul(randPelletOrigin, tempDirectionBuffer);

      Vector2f finalPelletOrigin = new Vector2f();
      origin.add(tempDirectionBuffer, finalPelletOrigin);

      Projectile projectile =
          new Projectile(
              this.calculateDamage(world),
              pelletDirection,
              finalPelletOrigin,
              getProjectileVelocity(),
              getRange(),
              new Vector2f(5, 5), // size
              getProjectileTexture(),
              team);
      world.addProjectile(projectile);
    }
  }
}
