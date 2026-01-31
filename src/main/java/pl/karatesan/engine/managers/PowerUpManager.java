package pl.karatesan.engine.managers;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.PowerUpType;
import pl.karatesan.engine.utils.RandomService;

public class PowerUpManager {

  private RandomService randomService;
  private float timer;
  private float spawnObjectInterval;
  private float spawnRadius;
  private SpawnManager spawnManager;

  public PowerUpManager(
      RandomService randomService, float spawnObjectInterval, SpawnManager spawnManager) {
    this.randomService = randomService;
    this.spawnObjectInterval = spawnObjectInterval;
    this.spawnManager = spawnManager;
    this.timer = 0;
    this.spawnRadius = 500;
  }

  public void spawnPowerUp(World world) {
    double maxAngle = Math.PI * 2;
    double angle = randomService.randomDoubleInRange(0, maxAngle);
    Vector2f position = new Vector2f();
    float x = (float) (world.getPlayer().getPosition().x + Math.cos(angle) * spawnRadius);
    float y = (float) (world.getPlayer().getPosition().y + Math.sin(angle) * spawnRadius);
    position.set(x, y);
    spawnManager.spawnPowerUp(PowerUpType.HEALTH_PACK, position, world);
  }

  public void update(double deltaTime, World world) {
    timer += (float) deltaTime;
    if (timer >= spawnObjectInterval) {
      timer = 0;
      spawnPowerUp(world);
    }
  }
}
