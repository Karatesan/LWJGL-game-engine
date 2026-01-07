package pl.karatesan.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.managers.*;
import pl.karatesan.engine.renderer.Renderer;
import pl.karatesan.engine.text.FontAtlas;
import pl.karatesan.engine.text.Text;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;

import java.util.ArrayList;
import java.util.List;

public class Game {
  private Camera2D camera;
  private Player player;
  private SpawnManager spawnManager;
  private Ground ground;
  private ProjectileManager projectileManager;
  private EntityFactory entityFactory;
  private WeaponFactory weaponFactory;
  private RandomService randomService;
  private CollisionManager collisionManager;
  private float newEnemySpawnCooldown = 10;
  private double timeElapsed = 0;
  private float enemySpawnTimer = 0;
  private int initialEnemyCount = 5;
  private int spawnedEnemyCount = 2;
  private int waveCounter = 0;
  private FontAtlas fontAtlas;
  private List<Enemy> enemies;

  public Game(Camera2D camera, TextureManager textureManager, RandomService randomService) {
    this.randomService = randomService;
    this.camera = camera;
    this.projectileManager = new ProjectileManager(randomService);
    this.entityFactory = new EntityFactory(textureManager);
    this.weaponFactory = new WeaponFactory(textureManager, randomService);
    this.enemies = new ArrayList<>();
    this.spawnManager =
        new SpawnManager(enemies, textureManager, weaponFactory, projectileManager, randomService);
    this.collisionManager = new CollisionManager(randomService);

    player = entityFactory.createPlayer(new Vector2f(0, 0), 80, 10000, new Vector2f(50, 50));
    RangedWeapon weapon = weaponFactory.createWeapon(WeaponType.ASSAULT_RIFLE, Team.PLAYER);
    player.setWeapon(weapon);
    ground = entityFactory.createGround();
    spawnManager.spawnWave(
        initialEnemyCount, (float) camera.getViewWidth() / 2, player.getPosition());
    fontAtlas = new FontAtlas();
    fontAtlas.init();
  }

  // todo bedzie trzeba colission manager, interfejs damageable z metoda takeDamage, damageManager
  // colision manager tworzy eventy - co zostalo trafione i czym, damage handler to obsluguje
  public void update(double deltaTime, GenericInputHandler input) {
    timeElapsed += deltaTime;
    enemySpawnTimer += (float) deltaTime;
    if (player.isAlive()) {
      player.update(deltaTime);
      player.move(deltaTime, input.getMovementInput());
      player.aim(camera.convertScreenToWorld(input.getMousePosition()));
    }
    spawnManager.update(deltaTime, player.getPosition());
    if (input.isMouseLeftDown() && player.tryShoot()) {
      projectileManager.createProjectile(
          player.getWeapon(), player.getAimDirection(), player.getPosition(), Team.PLAYER);
    }
    projectileManager.update(deltaTime);
    collisionManager.handleProjectileHits(enemies, projectileManager.getProjectiles(), player);
    if (player.wasHit()) {
      int damage = player.getLastHitDamage();
      camera.startShake(damage);
    }
    camera.update(deltaTime, player.getPosition());

    if (enemySpawnTimer >= newEnemySpawnCooldown) {
      enemySpawnTimer = 0;
      spawnManager.spawnWave(
          spawnedEnemyCount, (float) camera.getViewWidth() / 2, player.getPosition());
      if (waveCounter % 5 == 0) {
        spawnedEnemyCount++;
      }
    }
  }

  public void render(Renderer renderer) {
    renderer.begin();
    renderer.drawGround(ground.getPosition(), ground.getTexture(), ground.getSize());
    renderer.drawQuad(
        player.getPosition(),
        player.getAimDirection(),
        player.getSize(),
        null,
        player.getTexture());
    for (Projectile p : projectileManager.getProjectiles()) {
      renderer.drawQuad(
          p.getPosition(),
          p.getDirection(),
          p.getSize(),
          new Vector3f(1.0f, 0.0f, 0.0f),
          p.getTexture());
    }
    for (Enemy e : spawnManager.getEnemies()) {
      renderer.drawQuad(e.getPosition(), e.getAimDirection(), e.getSize(), null, e.getTexture());
    }
    Text text = new Text("12OMOnsdPSS4512345ss", fontAtlas);
    renderer.drawText(new Vector2f(0, 0), text, fontAtlas);
    renderer.end();
  }
}
