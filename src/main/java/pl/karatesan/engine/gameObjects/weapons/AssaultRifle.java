package pl.karatesan.engine.gameObjects.weapons;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.Projectile;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.texture.Texture;

public class AssaultRifle extends Weapon {

  private final float rifleSpreadCone = .08f;

  public AssaultRifle(
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
        range);
  }

  @Override
  public void createProjectiles(World world, Vector2f origin, Vector2f direction, Team team) {
    Vector2f bulletDirection = new Vector2f();
    float x = (float) (world.getRandomService().nextGaussian() * rifleSpreadCone);
    float y = (float) (world.getRandomService().nextGaussian() * rifleSpreadCone);
    bulletDirection.x = direction.x + x;
    bulletDirection.y = direction.y + y;
    bulletDirection.normalize();
    Projectile projectile =
        new Projectile(
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
