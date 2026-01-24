package pl.karatesan.engine.gameObjects.weapons;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.projectiles.Projectile;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.projectiles.RifleProjectile;
import pl.karatesan.engine.texture.Texture;

public class Rifle extends Weapon {

  public Rifle(
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
        WeaponType.RIFLE);
  }

  @Override
  public void createProjectiles(World world, Vector2f origin, Vector2f direction, Team team) {
    Vector2f bulletDirection = new Vector2f(direction);
    RifleProjectile projectile =
        new RifleProjectile(
            calculateDamage(world),
            bulletDirection,
            origin,
            getProjectileVelocity(),
            getRange(),
            new Vector2f(5, 5),
            getProjectileTexture(),
            team);
    world.addProjectile(projectile);
  }
}
