package pl.karatesan.engine.managers;

import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.gameObjects.weapons.AssaultRifle;
import pl.karatesan.engine.gameObjects.weapons.Shotgun;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;

public class WeaponFactory {
  private final TextureManager textureManager;
  private final RandomService randomService;

  public WeaponFactory(TextureManager textureManager, RandomService randomService) {
    this.textureManager = textureManager;
    this.randomService = randomService;
  }

  public Shotgun createShotgun(Team team) {
    switch (team) {
      case PLAYER -> {
        return new Shotgun(1f, textureManager.load("/bullet.jpg"), 30, 50, 250, 250);
      }
      case ENEMY -> {
        return new Shotgun(1f, textureManager.load("/bullet.jpg"), 30, 50, 200, 250);
      }
      default -> {
        return null;
      }
    }
  }

  public AssaultRifle createAssaultRiffle(Team team) {
    switch (team) {
      case PLAYER -> {
        return new AssaultRifle(
            0.1f, textureManager.load("/bullet.jpg"), 10, 15, 600, 500);
      }
      case ENEMY -> {
        return new AssaultRifle(
            1f, textureManager.load("/bullet.jpg"), 10, 15, 200, 300);
      }
      default -> {
        return null;
      }
    }
  }
}
