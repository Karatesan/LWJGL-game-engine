package pl.karatesan.engine.managers;

import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.gameObjects.weapons.AssaultRifle;
import pl.karatesan.engine.gameObjects.weapons.Rifle;
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
        return new Shotgun(1f, textureManager.load("/bullet.jpg"), 30, 50, 1000, 450);
      }
      case ENEMY -> {
        return new Shotgun(1.5f, textureManager.load("/bullet.jpg"), 30, 50, 500, 350);
      }
      default -> {
        return null;
      }
    }
  }

  public AssaultRifle createAssaultRiffle(Team team) {
    switch (team) {
      case PLAYER -> {
        return new AssaultRifle(0.1f, textureManager.load("/bullet.jpg"), 10, 15, 1800, 700);
      }
      case ENEMY -> {
        return new AssaultRifle(1f, textureManager.load("/bullet.jpg"), 10, 15, 500, 400);
      }
      default -> {
        return null;
      }
    }
  }

  public Rifle createRifle(Team team) {
    switch (team) {
      case PLAYER -> {
        return new Rifle(1.5f, textureManager.load("/bullet.jpg"), 50, 100, 1500, 1500);
      }
      case ENEMY -> {
        return new Rifle(3f, textureManager.load("/bullet.jpg"), 50, 100, 600, 600);
      }
      default -> {
        return null;
      }
    }
  }
}
