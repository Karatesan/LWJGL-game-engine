package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.gameObjects.Projectile;
import pl.karatesan.engine.gameObjects.RangedWeapon;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.gameObjects.WeaponType;
import pl.karatesan.engine.utils.RandomService;

import java.util.ArrayList;

public class ProjectileManager {

  private RandomService randomService;
  private ArrayList<Projectile> projectiles;
  private Vector2f offsetBuffer;
  private Vector2f projectileAngleBuffer;
  private final Vector2f tempDirectionBuffer = new Vector2f();
  private final float shotgunSpread = 70;
  private final float coneAngle = 0.4f;
  private final int pelletCount = 5;
  private final float rifleSpreadCone = .08f;

  public ProjectileManager(RandomService randomService) {
    this.projectiles = new ArrayList<>();
    this.offsetBuffer = new Vector2f();
    this.projectileAngleBuffer = new Vector2f();
    this.randomService = randomService;
  }

  public void createProjectile(
          RangedWeapon weapon, Vector2f aimDirection, Vector2f entityPosition, Team team) {
    Vector2f direction = new Vector2f(aimDirection);
    // direction.mul(100.0f, offsetBuffer); // TODO hardcoded half of player size
    Vector2f origin = new Vector2f(entityPosition).add(direction);
    if (weapon.getType().equals(WeaponType.SHOTGUN))
      createShotgunPellets(weapon, direction, origin, pelletCount, team);
    else if (weapon.getType().equals(WeaponType.ASSAULT_RIFLE)) {
      createAssaultRiffleProjectile(weapon, direction, origin, team);
    }
  }

  private void generateProjectile(
      RangedWeapon weapon, Vector2f direction, Vector2f origin, Team team) {
    Projectile projectile =
        new Projectile(
            weapon.calculateDamage(),
            direction,
            origin,
            weapon.getProjectileVelocity(),
            weapon.getRange(),
            new Vector2f(5, 5),
            weapon.getProjectileTexture(),
            team);
    projectiles.add(projectile);
  }

  /**
   * Generates a cone of projectiles (pellets) for a shotgun effect, combining angular spread and a
   * random positional offset to simulate realistic firing.
   *
   * <p>The angular spread is achieved by rotating the primary aim direction (D) using its
   * perpendicular vector (P), following the 2D vector rotation formula: D' = cos(theta) * D +
   * sin(theta) * P.
   *
   * <p>The positional offset is achieved by shifting the projectile's spawn point along its
   * calculated flight path (D') by a random distance, which visually spreads the starting points of
   * the pellets.
   *
   * @param weapon The weapon instance used to calculate damage and velocity.
   * @param direction The unit vector representing the player's straight aim direction (D).
   * @param origin The player's current position (the true center origin).
   * @param numberOfPellets The total number of projectiles to generate (e.g., 5).
   */
  private void createShotgunPellets(
      RangedWeapon weapon, Vector2f direction, Vector2f origin, int numberOfPellets, Team team) {
    if (numberOfPellets < 3)
      throw new IllegalArgumentException(
          "Number of pellets shot from shotgun must be higher than 2.");
    // spread from -.3 to .3
    float startAngle = -coneAngle / 2;
    float increment = -2 * startAngle / (numberOfPellets - 1);
    Vector2f perpendicularDir = new Vector2f(-direction.y, direction.x);
    for (int i = 0; i < numberOfPellets; i++) {
      Vector2f pelletDirection = new Vector2f();
      float cos = (float) Math.cos(startAngle + i * increment);
      float sin = (float) Math.sin(startAngle + i * increment);
      direction.mul(cos, pelletDirection);
      perpendicularDir.mul(sin, projectileAngleBuffer);
      pelletDirection.add(projectileAngleBuffer).normalize();

      float randPelletOrigin = randomService.randFloat() * shotgunSpread;

      pelletDirection.mul(randPelletOrigin, tempDirectionBuffer);

      Vector2f finalPelletOrigin = new Vector2f();
      origin.add(tempDirectionBuffer, finalPelletOrigin);

      generateProjectile(weapon, pelletDirection, finalPelletOrigin, team);
    }
  }

  private void createAssaultRiffleProjectile(
      RangedWeapon weapon, Vector2f direction, Vector2f origin, Team team) {
    //    Vector2f perpendicularDir = new Vector2f(-direction.y, direction.x);
    Vector2f bulletDirection = new Vector2f();
    //    float angle = randomService.randFloat() * rifleSpreadCone - 0.1f;
    //
    //    float cos = (float) Math.cos(angle);
    //    float sin = (float) Math.sin(angle);
    //    direction.mul(cos, bulletDirection);
    //    perpendicularDir.mul(sin, projectileAngleBuffer);
    //    bulletDirection.add(projectileAngleBuffer).normalize();
    //

    float x = (float) randomService.nextGaussian() * rifleSpreadCone;
    float y = (float) randomService.nextGaussian() * rifleSpreadCone;
    bulletDirection.x = direction.x + x;
    bulletDirection.y = direction.y + y;
    bulletDirection.normalize();
    Vector2f finalPelletOrigin = new Vector2f();
    origin.add(bulletDirection, finalPelletOrigin);
    generateProjectile(weapon, bulletDirection, finalPelletOrigin, team);
  }

  public void update(double deltaTime) {
    for (int i = projectiles.size() - 1; i >= 0; i--) {
      Projectile p = projectiles.get(i);
      p.update(deltaTime);
      if (p.shouldDestroy()) projectiles.remove(i);
    }
  }

  public ArrayList<Projectile> getProjectiles() {
    return projectiles;
  }
}
