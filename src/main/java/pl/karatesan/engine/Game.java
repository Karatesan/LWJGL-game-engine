package pl.karatesan.engine;

import org.joml.Vector2f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.gameObjects.entity.Entity;
import pl.karatesan.engine.gameObjects.entity.Player;
import pl.karatesan.engine.gameObjects.weapons.AssaultRifle;
import pl.karatesan.engine.gameObjects.weapons.Shotgun;
import pl.karatesan.engine.gameObjects.weapons.WeaponUtil;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.managers.*;
import pl.karatesan.engine.projectiles.Projectile;
import pl.karatesan.engine.renderer.Renderer;
import pl.karatesan.engine.sound.SoundManager;
import pl.karatesan.engine.text.*;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;
import pl.karatesan.engine.utils.Utilities;

// TODO finite state machine for enemies
public class Game {
  private World world;
  private Camera2D camera;
  private EnemyWavesManager waveManager;
  private EntityFactory entityFactory;
  private WeaponFactory weaponFactory;
  private WeaponUtil weaponUtil;
  private CollisionManager collisionManager;
  private boolean isGameOver = false;
  private FontAtlas fontAtlas;
  private Text time;
  private Text killCounter;
  private Text fpsCounter;
  private Text playerHealth;
  private TimeoutText waveInfo;
  private Text enemyCount;
  private Text gameOver;
  private double fps;
  private int enemiesKilled;
  private HUD hud;

  private float distanceTravelled;
  private float timeElapsed = 0;

  public Game(
      Camera2D camera,
      TextureManager textureManager,
      RandomService randomService,
      SoundManager soundManager) {
    this.camera = camera;
    this.entityFactory = new EntityFactory(textureManager);
    this.weaponFactory = new WeaponFactory(textureManager, randomService);
    this.weaponUtil = new WeaponUtil(randomService);
    this.collisionManager = new CollisionManager();
    this.hud = new HUD(camera);
    Player player = entityFactory.createPlayer(new Vector2f(0, 0), 100, 100, new Vector2f(50, 50));
    // AssaultRifle weapon = weaponFactory.createAssaultRiffle(Team.PLAYER);
    // Rifle weapon = weaponFactory.createRifle(Team.PLAYER);
    Shotgun weapon = weaponFactory.createShotgun(Team.PLAYER);
    player.setWeapon(weapon);
    Ground ground = entityFactory.createGround();
    this.world = new World(player, ground, randomService, soundManager);
    SpawnManager spawnManager = new SpawnManager(textureManager, weaponFactory, randomService);
    this.waveManager =
        new EnemyWavesManager(spawnManager, randomService, 10, 10, 2, 5, camera.getViewWidth());
    waveManager.spawnInitialWave(world);

    fontAtlas = new FontAtlas();
    fontAtlas.init();
    this.time = new Text("Time: 00:00", UIAnchor.TOP_CENTER, 0, 0, fontAtlas, 0.5f, hud);
    this.killCounter = new Text("Killed: 0", UIAnchor.TOP_LEFT, 0, 0, fontAtlas, 1f, hud);
    this.fpsCounter = new Text("00", UIAnchor.TOP_RIGHT, 100, 0, fontAtlas, 0.3f, hud);
    this.enemyCount = new Text("00", UIAnchor.BOTTOM_CENTER, 0, 0, fontAtlas, 0.3f, hud);
    this.gameOver = new Text("GAME OVER", UIAnchor.CENTER, 0, 0, fontAtlas, 3f, hud);

    this.playerHealth =
        new Text(
            Integer.toString(world.getPlayer().getHealth()),
            UIAnchor.BOTTOM_RIGHT,
            50,
            50,
            fontAtlas,
            0.5f,
            hud);
    this.waveInfo =
        new TimeoutText(
            "WAVE 1",
            new Vector2f(
                player.getPosition().x + 250, player.getPosition().y - fontAtlas.getLineHeight()),
            fontAtlas,
            2f,
            3f);

    world.getSoundManager().playMusic(player.getPosition());
  }

  // todo bedzie trzeba colission manager, interfejs damageable z metoda takeDamage, damageManager
  // colision manager tworzy eventy - co zostalo trafione i czym, damage handler to obsluguje
  public void update(double deltaTime, GenericInputHandler input) {
    timeElapsed += deltaTime;
    fps = 1f / deltaTime;

    // Player
    Player player = world.getPlayer();
    if (player.isAlive()) {
      player.update(world, deltaTime);
      player.move(deltaTime, input.getMovementInput());
      if (input.getMovementInput().x != 0 || input.getMovementInput().y != 0) {
        distanceTravelled += (float) (deltaTime * player.getSpeed());
      }
      int pixelToMeter = 25;
      if (distanceTravelled >= pixelToMeter) {
        world.getSoundManager().playFootstepSound(player.getPosition());
        distanceTravelled = 0;
      }
      Vector2f aim = camera.convertScreenToWorld(input.getMousePosition());
      player.aim(aim);

      // Game update
      camera.update(deltaTime, player.getPosition());
      Vector2f listenerPos = player.getPosition(); // Or camera.getPosition()
      world.getSoundManager().setListenerData(listenerPos.x, listenerPos.y);

      if (input.isMouseLeftDown()) {
        player.tryShoot(world);
      }

      // enemies
      for (Entity e : world.getEntities()) {
        if (e.isAlive()) {
          e.update(world, deltaTime);
        }
      }

      waveManager.update((float) deltaTime, world);

      // projectiles
      for (Projectile p : world.getProjectiles()) {
        p.update(deltaTime);
      }

      // collisions
      collisionManager.handleProjectileHits(world, weaponUtil);

      if (player.wasHit()) {
        int damage = player.getLastHitDamage();
        camera.startShake(damage);
      }

      // UI
      if (waveManager.consumeWaveChangedFlag()) {
        waveInfo.update("Wave " + (waveManager.getWaveCounter() + 1), fontAtlas);
        waveInfo.setPosition(player.getPosition(), 200f, 0);
        waveInfo.setShouldRender(true);
      }
      waveInfo.updateTimeout((float) deltaTime);
      time.update("Time: " + Utilities.trunctate(timeElapsed), fontAtlas);
      enemiesKilled += world.getKilledEnemiesCount();
      enemyCount.update(String.valueOf(world.getEntities().size()), fontAtlas);
      fpsCounter.update(String.valueOf(fps), fontAtlas);
      if (enemiesKilled > 0) killCounter.update("Killed: " + enemiesKilled, fontAtlas);
      if (player.wasHit()) playerHealth.update(Integer.toString(player.getHealth()), fontAtlas);
      world.flushChanges();
    } else {
      isGameOver = true;
    }
  }

  public void render(Renderer renderer) {
    renderer.begin();
    Ground ground = world.getGround();
    Player player = world.getPlayer();
    renderer.beginRenderDynamicObjects();
    renderer.drawGround(ground.getPosition(), ground.getTexture(), ground.getSize());
    if (waveInfo.shouldRender()) renderer.drawText(waveInfo, fontAtlas);
    renderer.drawQuad(
        player.getPosition(), player.getAimDirection(), player.getSize(), player.getTexture());
    for (Projectile p : world.getProjectiles()) {
      renderer.drawQuad(p.getPosition(), p.getDirection(), p.getSize(), p.getTexture());
    }
    for (Entity e : world.getEntities()) {
      renderer.drawQuad(e.getPosition(), e.getAimDirection(), e.getSize(), e.getTexture());
    }

    renderer.beginRenderStaticUI();
    renderer.drawText(time, fontAtlas);
    renderer.drawText(killCounter, fontAtlas);
    renderer.drawText(fpsCounter, fontAtlas);
    renderer.drawText(playerHealth, fontAtlas);
    renderer.drawText(enemyCount, fontAtlas);
    if (isGameOver) renderer.drawText(gameOver, fontAtlas);
    renderer.end();
  }
}
