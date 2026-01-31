package pl.karatesan.engine.gameObjects.entity;

import org.joml.Vector2f;
import pl.karatesan.engine.context.World;
import pl.karatesan.engine.gameObjects.Team;
import pl.karatesan.engine.gameObjects.weapons.Weapon;
import pl.karatesan.engine.texture.Texture;

public class EnemyWithMeleeWeapon extends Enemy {
    private Vector2f aimBuffer;

    public EnemyWithMeleeWeapon(
            Vector2f position,
            float speed,
            Vector2f aimDirection,
            int health,
            Vector2f size,
            Weapon weapon,
            Texture texture) {
        super(position, speed, aimDirection, health, size, weapon, texture);
        this.aimBuffer = new Vector2f();
    }

    public void update(World world, double deltaTime) {
        this.weapon.update(deltaTime);
        Vector2f playerPosition = world.getPlayer().getPosition();
        float range = Vector2f.distance(playerPosition.x, playerPosition.y, position.x, position.y);
        inRange = range < weapon.getRange();
        playerPosition.sub(position, aimBuffer);
        aimBuffer.normalize();
        aimDirection.set(aimBuffer);
        move(deltaTime);
        if (inRange) {
            float random = world.getRandomService().randFloatInRange(0.1f, 0.2f);
            Vector2f perpendicularDir = new Vector2f(-aimDirection.y, aimDirection.x).mul(random);
            perpendicularDir.add(aimDirection);
            weapon.tryShoot(world, position, aimDirection, Team.ENEMY);
        }
    }

    public void move(double deltaTime) {
        if (!inRange) {
            aimBuffer.mul((float) (speed * deltaTime));
            position.add(aimBuffer);
        }
    }

    public Weapon getWeapon() {
        return weapon;
    }


}
