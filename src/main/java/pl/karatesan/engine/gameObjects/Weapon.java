package pl.karatesan.engine.gameObjects;

import org.joml.Vector2f;
import pl.karatesan.engine.texture.Texture;

public class Weapon {
  private String name;
  private int maxDamage;
  private int minDamage;
  private float projectileVelocity;
  private float weaponCooldown;
  private float range;
  private float currentCooldown;
  private boolean isOnCooldown;
  private Texture projectileTexture;

  public Weapon(
      String name,
      float weaponCooldown,
      float projectileVelocity,
      int minDamage,
      int maxDamage,
      float range,
      Texture projectileTexture) {
    this.name = name;
    this.weaponCooldown = weaponCooldown;
    this.projectileVelocity = projectileVelocity;
    this.minDamage = minDamage;
    this.maxDamage = maxDamage;
    this.range = range;
    this.currentCooldown = 0;
    this.isOnCooldown = false;
    this.projectileTexture = projectileTexture;
  }

  public String getName() {
    return name;
  }

  public float getProjectileVelocity() {
    return projectileVelocity;
  }

  public int calculateDamage() {
    return (int) (Math.random() * (maxDamage - minDamage + 1) + minDamage);
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
}
