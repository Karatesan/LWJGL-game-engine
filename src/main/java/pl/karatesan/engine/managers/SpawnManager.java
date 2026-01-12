package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.gameObjects.entity.Enemy;
import pl.karatesan.engine.gameObjects.entity.EnemyWithRangedWeapon;
import pl.karatesan.engine.gameObjects.entity.Entity;
import pl.karatesan.engine.gameObjects.weapons.Weapon;
import pl.karatesan.engine.texture.Texture;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;

import java.util.List;

public class SpawnManager {

  private final TextureManager textureManager;
  private final WeaponFactory weaponFactory;
  private RandomService randomService;
  private Vector2f positionBuffer;
  private Vector2f aimDirectionBuffer;
  private EnemyType[] enemyType;

  public SpawnManager(
      TextureManager textureManager, WeaponFactory weaponFactory, RandomService randomService) {
    this.textureManager = textureManager;
    this.weaponFactory = weaponFactory;
    this.randomService = randomService;
    this.positionBuffer = new Vector2f();
    this.aimDirectionBuffer = new Vector2f();
    this.enemyType = EnemyType.values();
  }

  public void spawnEnemy(
      EnemyType enemyType, Vector2f position, Vector2f playerPosition, World world) {
    playerPosition.sub(position, aimDirectionBuffer);
    aimDirectionBuffer.normalize();
    Enemy enemy = null;
    switch (enemyType) {
      case ENEMY_WITH_RIFFLE -> {
        enemy =
            createEnemyWithRangedWeapon(
                position, aimDirectionBuffer, weaponFactory.createAssaultRiffle(Team.ENEMY));
      }
      case ENEMY_WITH_SHOTGUN -> {
        enemy =
            createEnemyWithRangedWeapon(
                position, aimDirectionBuffer, weaponFactory.createShotgun(Team.ENEMY));
      }
    }
    world.addEntity(enemy);
  }

  public void spawnWave(int enemyNumber, float radius, Vector2f playerPosition, World world) {
    if (enemyNumber <= 0)
      throw new IllegalArgumentException("Number of spawned enemies must be greater than 0");
    for (int i = 0; i < enemyNumber; i++) {
      double maxAngle = Math.PI * 2;
      double angle = randomService.randomDoubleInRange(0, maxAngle);
      positionBuffer.x = (float) (playerPosition.x + Math.cos(angle) * radius);
      positionBuffer.y = (float) (playerPosition.y + Math.sin(angle) * radius);
      int randIndex = randomService.randIntInRange(0, enemyType.length - 1);
      this.spawnEnemy(enemyType[randIndex], positionBuffer, playerPosition, world);
    }
  }

  private EnemyWithRangedWeapon createEnemyWithRangedWeapon(
      Vector2f position, Vector2f aim, Weapon weapon) {
    Texture texture = textureManager.load("/sprite.png");
    return new EnemyWithRangedWeapon(position, 25, aim, 100, new Vector2f(50, 50), weapon, texture);
  }
}
