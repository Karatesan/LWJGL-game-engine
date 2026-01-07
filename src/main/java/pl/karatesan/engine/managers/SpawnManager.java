package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.texture.Texture;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;

import java.util.List;

public class SpawnManager {

  private List<Enemy> enemies;
  private final TextureManager textureManager;
  private final WeaponFactory weaponFactory;
  private final ProjectileManager projectileManager;
  private Vector2f hitPositionBuffer;
  private RandomService randomService;
  private Vector2f positionBuffer;
  private Vector2f aimDirectionBuffer;
  private EnemyType[] enemyType;

  public SpawnManager(
      List<Enemy> enemies,
      TextureManager textureManager,
      WeaponFactory weaponFactory,
      ProjectileManager projectileManager,
      RandomService randomService) {
    this.textureManager = textureManager;
    this.weaponFactory = weaponFactory;
    this.projectileManager = projectileManager;
    this.randomService = randomService;
    this.enemies = enemies;
    this.hitPositionBuffer = new Vector2f();
    this.positionBuffer = new Vector2f();
    this.aimDirectionBuffer = new Vector2f();
    this.enemyType = EnemyType.values();
  }

  public void update(double deltaTime, Vector2f playerPosition) {
    // enemies.removeIf()
    for (Enemy e : enemies) {
      if (e.isAlive()) {
        e.update(deltaTime, playerPosition);
        e.move(deltaTime);
        if (e.tryAttack()) {
          float random = (float) Math.random() * 0.4f - 0.2f;
          Vector2f aimDirection = e.getAimDirection();
          Vector2f perpendicularDir =
              new Vector2f(-e.getAimDirection().y, e.getAimDirection().x).mul(random);
          e.getAimDirection().add(perpendicularDir);
          projectileManager.createProjectile(
              e.getWeapon(), aimDirection, e.getPosition(), Team.ENEMY);
        }
      }
    }
  }

  public void spawnEnemy(EnemyType enemyType, Vector2f position, Vector2f playerPosition) {
    playerPosition.sub(position, aimDirectionBuffer);
    aimDirectionBuffer.normalize();
    Enemy enemy = null;
    switch (enemyType) {
      case ENEMY_WITH_RIFFLE -> {
        enemy = createEnemyWithRangedWeapon(position, aimDirectionBuffer, WeaponType.ASSAULT_RIFLE);
      }
      case ENEMY_WITH_SHOTGUN -> {
        enemy = createEnemyWithRangedWeapon(position, aimDirectionBuffer, WeaponType.SHOTGUN);
      }
    }
    enemies.add(enemy);
  }

  public void spawnWave(int enemyNumber, float radius, Vector2f playerPosition) {
    if (enemyNumber <= 0)
      throw new IllegalArgumentException("Number of spawned enemies must be greater than 0");
    for (int i = 0; i < enemyNumber; i++) {
      double maxAngle = Math.PI * 2;
      double angle = randomService.randomDoubleInRange(0, maxAngle);
      positionBuffer.x = (float) (playerPosition.x + Math.cos(angle) * radius);
      positionBuffer.y = (float) (playerPosition.y + Math.sin(angle) * radius);
      int randIndex = randomService.randIntInRange(0, enemyType.length - 1);
      this.spawnEnemy(enemyType[randIndex], positionBuffer, playerPosition);
    }
  }

  private EnemyWithRangedWeapon createEnemyWithRangedWeapon(
      Vector2f position, Vector2f aim, WeaponType weaponType) {
    Texture texture = textureManager.load("/sprite.png");
    RangedWeapon weapon = weaponFactory.createWeapon(weaponType, Team.ENEMY);
    return new EnemyWithRangedWeapon(position, 25, aim, 100, new Vector2f(50, 50), weapon, texture);
  }

  public List<Enemy> getEnemies() {
    return enemies;
  }
}
