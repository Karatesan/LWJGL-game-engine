package pl.karatesan.engine.gameObjects;

import pl.karatesan.engine.texture.Texture;
import pl.karatesan.engine.utils.RandomService;

public class RangedWeapon {
  private WeaponType type;
  private int maxDamage;
  private int minDamage;
  private float projectileVelocity;
  private float weaponCooldown;
  private float range;
  private float currentCooldown;
  private boolean isOnCooldown;
  private Texture projectileTexture;
  private RandomService randomService;

  public RangedWeapon(
      WeaponType type,
      float weaponCooldown,
      float projectileVelocity,
      int minDamage,
      int maxDamage,
      float range,
      Texture projectileTexture,
      RandomService randomService) {
    this.type = type;
    this.weaponCooldown = weaponCooldown;
    this.projectileVelocity = projectileVelocity;
    this.minDamage = minDamage;
    this.maxDamage = maxDamage;
    this.range = range;
    this.currentCooldown = 0;
    this.isOnCooldown = false;
    this.projectileTexture = projectileTexture;
    this.randomService = randomService;
  }

  public WeaponType getType() {
    return type;
  }

  public float getProjectileVelocity() {
    return projectileVelocity;
  }

  public int calculateDamage() {
    return randomService.randIntInRange(minDamage, maxDamage);
  }

  public float getRange() {
    return range;
  }

  public boolean canShot() {
    return !isOnCooldown;
  }

  public void update(double deltaTime) {
    if (isOnCooldown) {
      currentCooldown += (float) deltaTime;
      if (currentCooldown >= weaponCooldown) {
        isOnCooldown = false;
        currentCooldown = 0;
      }
    }
  }

  public boolean shot() {
    if (!isOnCooldown) {
      isOnCooldown = true;
      return true;
    }
    return false;
  }

  public Texture getProjectileTexture() {
    return projectileTexture;
  }

  public void scaleWeapon(int scalar) {
    this.weaponCooldown *= scalar * 2;
    this.projectileVelocity /= (scalar * 2);
    this.range /= scalar;
  }
}
