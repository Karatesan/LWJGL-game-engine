package pl.karatesan.engine;

import org.joml.Vector2f;
import org.joml.Vector3f;
import pl.karatesan.engine.camera.Camera2D;
import pl.karatesan.engine.gameObjects.*;
import pl.karatesan.engine.input.GenericInputHandler;
import pl.karatesan.engine.managers.*;
import pl.karatesan.engine.renderer.Renderer;
import pl.karatesan.engine.texture.TextureManager;
import pl.karatesan.engine.utils.RandomService;

public class Game {
  private Camera2D camera;
  private Player player;
  private EnemyManager enemyManager;
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

  public Game(Camera2D camera, TextureManager textureManager, RandomService randomService) {
    this.randomService = randomService;
    this.camera = camera;
    this.projectileManager = new ProjectileManager(randomService);
    this.entityFactory = new EntityFactory(textureManager);
    this.weaponFactory = new WeaponFactory(textureManager, randomService);
    this.enemyManager =
        new EnemyManager(textureManager, weaponFactory, projectileManager, randomService);
    this.collisionManager = new CollisionManager(enemyManager, projectileManager);
    player = entityFactory.createPlayer(new Vector2f(0, 0), 80, 10000, new Vector2f(50, 50));
    RangedWeapon weapon = weaponFactory.createWeapon(WeaponType.ASSAULT_RIFLE, Team.PLAYER);
    player.setWeapon(weapon);
    ground = entityFactory.createGround();

    //    enemyManager.spawnEnemy(EnemyType.ENEMY_WITH_RIFFLE, position, player.getPosition());
    //    float new_x = (float) (position.x * Math.cos(-Math.PI/2) - position.y *
    // Math.sin(-Math.PI/2));
    //    float new_y = (float) (position.y * Math.cos(-Math.PI/2) + position.x +
    // Math.sin(-Math.PI/2));
    //
    //      Vector2f pos2 = new Vector2f(new_x,new_y);
    //    enemyManager.spawnEnemy(EnemyType.ENEMY_WITH_RIFFLE, pos2, player.getPosition());

    //    Vector2f pos1 = new Vector2f();
    //    player.getAimDirection().mul(100, pos1);
    //    Vector2f perp = new Vector2f(pos1.y, -pos1.x);
    // enemyManager.spawnEnemy(EnemyType.ENEMY_WITH_RIFFLE, pos1, player.getPosition());
    // enemyManager.spawnEnemy(EnemyType.ENEMY_WITH_RIFFLE, perp, player.getPosition());

    //    Vector2f orgBuffer = new Vector2f();
    //    Vector2f perpBuffer = new Vector2f();
    //    float startAngle = (float) (-Math.PI / 4);
    //    float step = (float) (Math.PI / 4);
    //    for (int i = 0; i < 3; i++) {
    //      float ang = i * step + startAngle;
    //      System.out.println(ang);
    //      float sin = (float) Math.sin(ang);
    //      float cos = (float) Math.cos(ang);
    //
    //      pos1.mul(cos, orgBuffer);
    //      perp.mul(sin, perpBuffer);
    //      Vector2f between = new Vector2f();
    //      orgBuffer.add(perpBuffer, between);
    //      Utilities.printVector2(between, "Between: ");
    //      enemyManager.spawnEnemy(EnemyType.ENEMY_WITH_RIFFLE, between, player.getPosition());
    //    }
    /*
    WYPROWADZANIE WZORU NA X I Y PO OBROCIE
        x = l*cos(a)
        y = l*sin(a)
        x_new = l*cos(a+b)  cos(a)(cos(b) - sin(a)sin(b)
        y_new = l*sin(a+b)  sin(a)cos(b) + cos(a)sin(b)

        x_new = l * (cos(a)(cos(b) - sin(a)sin(b)) = l*cos(a)(cos(b) - l* sin(a)sin(b) = x* cos(b) - y*sin(b)
        y_new = l * ( sin(a)cos(b) + cos(a)sin(b)) = l* sin(a)cos(b) + l* cos(a)sin(b) = y*cos(b) + x*sin(b)

         */
    enemyManager.spawnWave(
        initialEnemyCount, (float) camera.getViewWidth() / 2, player.getPosition());
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
    enemyManager.update(deltaTime, player.getPosition());
    if (input.isMouseLeftDown() && player.tryShoot()) {
      projectileManager.createProjectile(
          player.getWeapon(), player.getAimDirection(), player.getPosition(), Team.PLAYER);
    }
    projectileManager.update(deltaTime);
    collisionManager.handleProjectileHits(player);
    if (player.wasHit()) {
      int damage = player.getLastHitDamage();
      camera.startShake(damage);
    }
    camera.update(deltaTime, player.getPosition());

    if (enemySpawnTimer >= newEnemySpawnCooldown) {
      enemySpawnTimer = 0;
      enemyManager.spawnWave(
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
    for (Enemy e : enemyManager.getEnemies()) {
      renderer.drawQuad(e.getPosition(), e.getAimDirection(), e.getSize(), null, e.getTexture());
    }
    renderer.end();
  }
}
