package pl.karatesan.dump;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class WindowUtils {

    public static void createOrthoProjection(int... shaderPrograms) {
        Matrix4f projection = new Matrix4f().ortho(0, 800, 0, 600, -1, 1);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = projection.get(stack.mallocFloat(16));
            for (int program : shaderPrograms) {
                int projLoc = glGetUniformLocation(program, "projection");
                glUseProgram(program); // musimy mieć aktywny program
                glUniformMatrix4fv(projLoc, false, fb);
            }
        }
    }
}
