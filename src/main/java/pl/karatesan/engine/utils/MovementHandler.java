package pl.karatesan.engine.utils;

import pl.karatesan.dump.Vector2f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class MovementHandler {

    // Reusable vector to avoid allocation each frame
    private final Vector2f movementDir = new Vector2f();

    public void handle(long window, Vector2f position, float speed, double deltaTime) {
        float dx = 0, dy = 0;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) dy += 1;
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) dy -= 1;
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) dx -= 1;
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) dx += 1;

        if (dx != 0 || dy != 0) {
            movementDir.set(dx, dy).normalizeSelf(); // no new object created
            position.add(
                    (float) (movementDir.x * speed * deltaTime),
                    (float) (movementDir.y * speed * deltaTime)
            );
        }
    }
}
