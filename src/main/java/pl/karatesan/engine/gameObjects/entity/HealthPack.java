package pl.karatesan.engine.gameObjects.entity;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.texture.Texture;

public class HealthPack extends Object {

  int healValue;

  public HealthPack(Vector2f position, Vector2f size, int healValue, Texture texture) {
    super(position, size, texture);
    this.healValue = healValue;
  }

  @Override
  public void activate(Entity activator, World world) {
    if (isActive) {
      activator.heal(healValue);
      isActive = false;
      world.getSoundManager().playHealthPackSound(activator.getPosition());
    }
  }
}
