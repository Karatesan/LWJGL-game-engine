package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.utils.RandomService;

public class EnemyWavesManager {

  private final SpawnManager spawnManager;
  private RandomService randomService;
  private float waveSpawnCooldown;
  private float enemySpawnTimer = 0;
  private int enemiesPerWave;
  private int waveCounter = 0;
  private int enemyIncreaseInterval;
  private float spawnRadius;
  private int enemyCountIncrease;
  private boolean waveChanged = false;
  private Vector2f positionBuffer;

  public EnemyWavesManager(
      SpawnManager spawnManager,
      RandomService randomService,
      float waveSpawnCooldown,
      int enemiesPerWave,
      int enemyCountIncrease,
      int enemyIncreaseInterval,
      float spawnRadius) {
    this.spawnManager = spawnManager;
    this.randomService = randomService;
    this.waveSpawnCooldown = waveSpawnCooldown;
    this.enemiesPerWave = enemiesPerWave;
    this.enemyCountIncrease = enemyCountIncrease;
    this.enemyIncreaseInterval = enemyIncreaseInterval;
    this.spawnRadius = spawnRadius;
    this.positionBuffer = new Vector2f();
    this.enemyIncreaseInterval = 5;
  }

  public void spawnInitialWave(World world) {
    this.spawnWave(enemiesPerWave, spawnRadius / 2, world);
  }

  public void update(float deltaTime, World world) {
    enemySpawnTimer += deltaTime;
    if (enemySpawnTimer >= waveSpawnCooldown) {
      enemySpawnTimer = 0;
      waveCounter++;
      waveChanged = true;
      this.spawnWave(enemiesPerWave, spawnRadius / 2, world);
      if (waveCounter % enemyIncreaseInterval == 0) {
        enemiesPerWave += enemyCountIncrease;
      }
    }
  }

  public void spawnWave(int enemyNumber, float radius, World world) {
    if (enemyNumber <= 0)
      throw new IllegalArgumentException("Number of spawned enemies must be greater than 0");
    Vector2f playerPosition = world.getPlayer().getPosition();
    for (int i = 0; i < enemyNumber; i++) {
      double maxAngle = Math.PI * 2;
      double angle = randomService.randomDoubleInRange(0, maxAngle);
      positionBuffer.x = (float) (playerPosition.x + Math.cos(angle) * radius);
      positionBuffer.y = (float) (playerPosition.y + Math.sin(angle) * radius);
      spawnManager.spawnRandomEnemy(positionBuffer, playerPosition, world);
    }
  }

  public boolean consumeWaveChangedFlag() {
    if (waveChanged) {
      waveChanged = false;
      return true;
    }
    return false;
  }

  public int getWaveCounter() {
    return waveCounter;
  }
}
