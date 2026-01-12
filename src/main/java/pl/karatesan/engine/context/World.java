package pl.karatesan.engine.context;

import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.gameObjects.entity.Entity;
import pl.karatesan.engine.gameObjects.entity.Player;
import pl.karatesan.engine.utils.RandomService;

import java.util.ArrayList;
import java.util.List;

public class World {
  List<Entity> entities;
  Player player;
  Ground ground;
  List<Projectile> projectiles;

  List<Entity> entitiesToAdd;
  List<Projectile> projectilesToAdd;

  private RandomService randomService;

  public World(Player player, Ground ground, RandomService randomService) {
    this.entities = new ArrayList<>();
    this.projectiles = new ArrayList<>();
    this.player = player;
    this.ground = ground;
    this.entitiesToAdd = new ArrayList<>();
    this.projectilesToAdd = new ArrayList<>();
    this.randomService = randomService;
  }

  public void flushChanges() {
    if (!entitiesToAdd.isEmpty()) {
      entities.addAll(entitiesToAdd);
      entitiesToAdd.clear();
    }

    if (!projectilesToAdd.isEmpty()) {
      projectiles.addAll(projectilesToAdd);
      projectilesToAdd.clear();
    }
    projectiles.removeIf(Projectile::isDestroyed);
    entities.removeIf(e -> !e.isAlive());
  }

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

  public RandomService getRandomService() {
    return randomService;
  }

  public int getKilledEnemiesCount() {
    return (int)
        entities.stream().filter(e -> !e.isAlive() && e.getTeam().equals(Team.ENEMY)).count();
  }
}
