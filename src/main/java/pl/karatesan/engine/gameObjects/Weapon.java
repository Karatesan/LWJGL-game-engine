package pl.karatesan.engine.gameObjects;

public class Weapon {
    private String name;
    private int maxDamage;
    private int minDamage;
    private float projectileVelocity;
    private float cooldown;
    private float range;

    public Weapon(String name, float cooldown, float projectileVelocity, int minDamage, int maxDamage, float range) {
        this.name = name;
        this.cooldown = cooldown;
        this.projectileVelocity = projectileVelocity;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public float getProjectileVelocity() {
        return projectileVelocity;
    }

    public void setProjectileVelocity(float projectileVelocity) {
        this.projectileVelocity = projectileVelocity;
    }

    public int calculateDamage(){
        return (int)(Math.random() * (maxDamage - minDamage) + minDamage);
    }

    public float getRange(){
        return range;
    }
}
