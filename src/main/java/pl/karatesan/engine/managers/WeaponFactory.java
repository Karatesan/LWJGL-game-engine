package pl.karatesan.engine.managers;

import pl.karatesan.engine.gameObjects.RangedWeapon;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.gameObjects.WeaponType;
import pl.karatesan.engine.texture.Texture;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;

public class WeaponFactory {
  private final TextureManager textureManager;
  private final RandomService randomService;

  public WeaponFactory(TextureManager textureManager, RandomService randomService) {
    this.textureManager = textureManager;
    this.randomService = randomService;
  }

  public RangedWeapon createWeapon(WeaponType type, Team team) {
    // weapon has no texture yet
    // weapon is created maneuall now, later ll introduce file with JSON data of all weapons
    switch (type) {
      case SHOTGUN -> {
        return createShotgun(team);
      }
      case ASSAULT_RIFLE -> {
        return createAssaultRiffle(team);
      }
      default -> {
        return null;
      }
    }
  }

  private RangedWeapon createShotgun(Team team) {
    switch (team) {
      case PLAYER -> {
        return createRangedWeapon(
            WeaponType.SHOTGUN, 1, 250, 30, 50, 250, textureManager.load("/bullet.jpg"));
      }
      case ENEMY -> {
        return createRangedWeapon(
            WeaponType.SHOTGUN, 2, 200, 30, 50, 250, textureManager.load("/bullet.jpg"));
      }
      default -> {
        return null;
      }
    }
  }

  private RangedWeapon createAssaultRiffle(Team team) {
    switch (team) {
      case PLAYER -> {
        return createRangedWeapon(
            WeaponType.ASSAULT_RIFLE, 0.1f, 600, 10, 15, 500, textureManager.load("/bullet.jpg"));
      }
      case ENEMY -> {
        return createRangedWeapon(
            WeaponType.ASSAULT_RIFLE, 1.0f, 200, 10, 15, 300, textureManager.load("/bullet.jpg"));
      }
      default -> {
        return null;
      }
    }
  }

  private RangedWeapon createRangedWeapon(
      WeaponType type,
      float cooldown,
      float velocity,
      int minDamage,
      int maxDamage,
      float range,
      Texture bulletTexture) {
    return new RangedWeapon(
        type, cooldown, velocity, minDamage, maxDamage, range, bulletTexture, randomService);
  }
}
