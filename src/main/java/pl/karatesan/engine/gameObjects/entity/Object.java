package pl.karatesan.engine.gameObjects.entity;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.texture.Texture;

public abstract class Object {
  protected Vector2f position;
  protected Vector2f size;
  protected boolean isActive;
  protected Texture texture;

  public Object(Vector2f position, Vector2f size, Texture texture) {
    this.texture = texture;
    this.size = size;
    this.position = position;
    this.isActive = true;
  }

  public abstract void activate(Entity activator, World world);

  public Vector2f getPosition() {
    return position;
  }

  public Vector2f getSize() {
    return size;
  }

  public boolean isActive() {
    return isActive;
  }

  public Texture getTexture() {
    return texture;
  }

  @Override
  public String toString() {
    return "Object{"
        + "position="
        + position
        + ", size="
        + size
        + ", isActive="
        + isActive
        + ", texture="
        + texture
        + '}';
  }
}
