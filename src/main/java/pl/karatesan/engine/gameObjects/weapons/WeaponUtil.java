package pl.karatesan.engine.gameObjects.weapons;

import org.joml.Vector2f;
import pl.karatesan.engine.utils.RandomService;

public class WeaponUtil {

  private Vector2f pushbackBuffer;
  private RandomService randomService;

  public WeaponUtil(RandomService randomService) {
    this.pushbackBuffer = new Vector2f();
    this.randomService = randomService;
  }

  public Vector2f calculatePushBack(int damage, Vector2f projectileDirection) {
    int weaponPower = damage / 4;
    float x = randomService.randFloatInRange(-1.0f, 1.0f);
    float y = randomService.randFloatInRange(-1.0f, 1.0f);
    pushbackBuffer.set(projectileDirection.x + x, projectileDirection.y + y).mul(weaponPower);
    return pushbackBuffer;
  }
}
