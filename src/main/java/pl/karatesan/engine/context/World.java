package pl.karatesan.engine.context;

import pl.karatesan.engine.gameObjects.*;

import java.util.List;

public class World {
  List<Entity> entities;
  Player player;
  Ground ground;
  List<Projectile> projectiles;

  public List<Entity> getEntities() {
    return entities;
  }

  public void addEntity(Entity e) {
    entities.add(e);
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public Ground getGround() {
    return ground;
  }

  public void setGround(Ground ground) {
    this.ground = ground;
  }

  public List<Projectile> getProjectiles() {
    return projectiles;
  }

  public void addProjectile(Projectile p) {
    projectiles.add(p);
  }
}
