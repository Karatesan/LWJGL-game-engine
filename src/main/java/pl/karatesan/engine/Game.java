package pl.karatesan.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.gameObjects.entity.Entity;
import pl.karatesan.engine.gameObjects.entity.Player;
import pl.karatesan.engine.gameObjects.weapons.AssaultRifle;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.managers.*;
import pl.karatesan.engine.renderer.Renderer;
import pl.karatesan.engine.text.FontAtlas;
import pl.karatesan.engine.text.HUD;
import pl.karatesan.engine.text.Text;
import pl.karatesan.engine.text.UIAnchor;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;
import pl.karatesan.engine.utils.Utilities;

public class Game {
  private World world;
  private Camera2D camera;
  private SpawnManager spawnManager;
  private EntityFactory entityFactory;
  private WeaponFactory weaponFactory;
  private CollisionManager collisionManager;
  private float newEnemySpawnCooldown = 10;
  private double timeElapsed = 0;
  private float enemySpawnTimer = 0;
  private int initialEnemyCount = 5;
  private int spawnedEnemyCount = 2;
  private int waveCounter = 0;
  private FontAtlas fontAtlas;
  private Text time;
  private Text killCounter;
  private HUD hud;

  public Game(Camera2D camera, TextureManager textureManager, RandomService randomService) {
    this.camera = camera;
    this.entityFactory = new EntityFactory(textureManager);
    this.weaponFactory = new WeaponFactory(textureManager, randomService);
    this.collisionManager = new CollisionManager(randomService);
    this.hud = new HUD(camera.getWindow());
    Player player = entityFactory.createPlayer(new Vector2f(0, 0), 80, 10000, new Vector2f(50, 50));
    AssaultRifle weapon = weaponFactory.createAssaultRiffle(Team.PLAYER);
    // Shotgun weapon = weaponFactory.createShotgun(Team.PLAYER);
    player.setWeapon(weapon);
    Ground ground = entityFactory.createGround();
    this.world = new World(player, ground, randomService);
    this.spawnManager = new SpawnManager(textureManager, weaponFactory, randomService);
    spawnManager.spawnWave(
        initialEnemyCount, (float) camera.getViewWidth() / 2, player.getPosition(), world);
    fontAtlas = new FontAtlas();
    fontAtlas.init();
    this.time = new Text("Time: 00:00", UIAnchor.TOP_CENTER, 0, 0, fontAtlas, 0.5f, hud);
    this.killCounter = new Text("Killed: 0", UIAnchor.TOP_LEFT, 0, 0, fontAtlas, 1f, hud);
  }

  // todo bedzie trzeba colission manager, interfejs damageable z metoda takeDamage, damageManager
  // colision manager tworzy eventy - co zostalo trafione i czym, damage handler to obsluguje
  public void update(double deltaTime, GenericInputHandler input) {
    timeElapsed += deltaTime;
    enemySpawnTimer += (float) deltaTime;

    // Player
    Player player = world.getPlayer();
    if (player.isAlive()) {
      player.update(world, deltaTime);
      player.move(deltaTime, input.getMovementInput());
      player.aim(camera.convertScreenToWorld(input.getMousePosition()));
    }
    if (input.isMouseLeftDown()) {
      player.tryShoot(world);
    }

    // enemies
    for (Entity e : world.getEntities()) {
      if (e.isAlive()) {
        e.update(world, deltaTime);
      }
    }

    // projectiles
    for (Projectile p : world.getProjectiles()) {
      p.update(deltaTime);
    }

    collisionManager.handleProjectileHits(world.getEntities(), world.getProjectiles(), player);
    int enemiesKilled = world.getKilledEnemiesCount();
    if (player.wasHit()) {
      int damage = player.getLastHitDamage();
      camera.startShake(damage);
    }

    // Game update
    camera.update(deltaTime, player.getPosition());

    if (enemySpawnTimer >= newEnemySpawnCooldown) {
      enemySpawnTimer = 0;
      spawnManager.spawnWave(
          spawnedEnemyCount, (float) camera.getViewWidth() / 2, player.getPosition(), world);
      if (waveCounter % 5 == 0) {
        spawnedEnemyCount++;
      }
    }

    // UI
    time.update("Time: " + Utilities.trunctate(timeElapsed), fontAtlas);
    if (enemiesKilled > 0) killCounter.update("Killed: " + enemiesKilled, fontAtlas);

    world.flushChanges();
  }

  public void render(Renderer renderer) {
    renderer.begin();
    Ground ground = world.getGround();
    Player player = world.getPlayer();
    renderer.drawGround(ground.getPosition(), ground.getTexture(), ground.getSize());
    renderer.drawQuad(
        player.getPosition(),
        player.getAimDirection(),
        player.getSize(),
        null,
        player.getTexture());
    for (Projectile p : world.getProjectiles()) {
      renderer.drawQuad(
          p.getPosition(),
          p.getDirection(),
          p.getSize(),
          new Vector3f(1.0f, 0.0f, 0.0f),
          p.getTexture());
    }
    for (Entity e : world.getEntities()) {
      renderer.drawQuad(e.getPosition(), e.getAimDirection(), e.getSize(), null, e.getTexture());
    }
    renderer.beginRenderStaticUI();
    renderer.drawText(time, fontAtlas);
    renderer.drawText(killCounter,fontAtlas);
    renderer.end();
  }
}
