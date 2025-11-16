package pl.karatesan.dump;

public class Vector2f {
    public float x, y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f() {
        this(0, 0);
    }

    public Vector2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector2f normalizeSelf() {
        float magnitude = (float) Math.sqrt(x * x + y * y);
        if (magnitude != 0) {
            x /= magnitude;
            y /= magnitude;
        }
        return this;
    }

    public Vector2f add(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }
}