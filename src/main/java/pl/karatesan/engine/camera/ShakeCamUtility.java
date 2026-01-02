package pl.karatesan.engine.camera;

import org.joml.Vector2f;
import pl.karatesan.engine.utils.RandomService;

public class ShakeCamUtility {

  private Vector2f currentShakeDirection = new Vector2f();
  private int currentOscilation;
  private float shakeMagnitude;
  private int oscilationNumber;
  private float shakeCamDuration;
  private float timePerOscilation;
  private RandomService randomService;
  private Vector2f offset;
  private float factor;
  private boolean inShake;

  public ShakeCamUtility(RandomService randomService) {
    this.randomService = randomService;
    this.offset = new Vector2f(0, 0);
    this.inShake = false;
    this.oscilationNumber = 4;
    this.shakeCamDuration = 0.3f;
    this.timePerOscilation = shakeCamDuration / oscilationNumber;
    this.shakeMagnitude = 2;
    this.currentOscilation = 0;
    this.offset.set(0, 0);
    this.factor = 0;
  }

  public Vector2f calculateShakeOffset(double deltaTime) {
    if (inShake) {
      factor += (float) (Math.PI * 2 / timePerOscilation * deltaTime);
      float t = (float) Math.sin(factor);
      if (factor >= Math.PI * 2) {
        if (currentOscilation == oscilationNumber) {
          inShake = false;
          reset();
        } else {
          factor = 0;
          currentOscilation++;
          shakeMagnitude *= 0.8f;
          double angle = randomService.randFloatInRange(0, (float) (Math.PI * 2));
          float x = (float) Math.cos(angle);
          float y = (float) Math.sin(angle);
          currentShakeDirection.set(x, y);
        }
      }
      offset.set(
          currentShakeDirection.x * shakeMagnitude * t,
          currentShakeDirection.y * shakeMagnitude * t);
    }
    return offset;
  }

  public void startShake(int shakeMagnitude) {
    this.shakeMagnitude = (float) shakeMagnitude / 5;
    if (inShake) {
      this.shakeMagnitude *= 1.5f;
    }
    inShake = true;
  }

  public void reset() {
    timePerOscilation = shakeCamDuration / oscilationNumber;
    shakeMagnitude = 2;
    currentOscilation = 0;
    offset.set(0, 0);
    factor = 0;
  }
}
