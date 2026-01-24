package pl.karatesan.engine.gameObjects.weapons;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.texture.Texture;

public abstract class Weapon {
  protected float cooldown;
  protected Texture projectileTexture;
  private int maxDamage;
  private int minDamage;
  private float projectileVelocity;
  private float range;
  private float currentCooldown;
  private boolean isOnCooldown;
  private WeaponType weaponType;

  public Weapon(
      float cooldown,
      Texture projectileTexture,
      int minDamage,
      int maxDamage,
      float projectileVelocity,
      float range,
      WeaponType weaponType) {
    this.cooldown = cooldown;
    this.projectileTexture = projectileTexture;
    this.maxDamage = maxDamage;
    this.minDamage = minDamage;
    this.projectileVelocity = projectileVelocity;
    this.range = range;
    this.weaponType = weaponType;
    this.currentCooldown = 0;
    this.isOnCooldown = false;
  }

  public boolean tryShoot(World world, Vector2f origin, Vector2f direction, Team team) {
    if (!isOnCooldown) {
      createProjectiles(world, origin, direction, team);
      world.getSoundManager().playShotSound(weaponType, origin);
      currentCooldown = 0;
      isOnCooldown = true;
      return true;
    }
    return false;
  }

  public void update(double deltaTime) {
    if (isOnCooldown) {
      currentCooldown += (float) deltaTime;
      if (currentCooldown >= cooldown) {
        isOnCooldown = false;
        currentCooldown = 0;
      }
    }
  }

  // Abstract method: Subclasses define the pattern
  public abstract void createProjectiles(
      World world, Vector2f origin, Vector2f direction, Team team);

  protected int calculateDamage(World world) {
    return world.getRandomService().randIntInRange(minDamage, maxDamage);
  }

  public float getCooldown() {
    return cooldown;
  }

  public void setCooldown(float cooldown) {
    this.cooldown = cooldown;
  }

  public Texture getProjectileTexture() {
    return projectileTexture;
  }

  public void setProjectileTexture(Texture projectileTexture) {
    this.projectileTexture = projectileTexture;
  }

  public int getMaxDamage() {
    return maxDamage;
  }

  public void setMaxDamage(int maxDamage) {
    this.maxDamage = maxDamage;
  }

  public int getMinDamage() {
    return minDamage;
  }

  public void setMinDamage(int minDamage) {
    this.minDamage = minDamage;
  }

  public float getProjectileVelocity() {
    return projectileVelocity;
  }

  public void setProjectileVelocity(float projectileVelocity) {
    this.projectileVelocity = projectileVelocity;
  }

  public float getRange() {
    return range;
  }

  public void setRange(float range) {
    this.range = range;
  }

  public float getCurrentCooldown() {
    return currentCooldown;
  }

  public void setCurrentCooldown(float currentCooldown) {
    this.currentCooldown = currentCooldown;
  }

  public boolean isOnCooldown() {
    return isOnCooldown;
  }

  public void setOnCooldown(boolean onCooldown) {
    isOnCooldown = onCooldown;
  }
}
