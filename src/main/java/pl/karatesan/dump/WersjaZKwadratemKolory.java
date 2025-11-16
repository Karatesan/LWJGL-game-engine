package pl.karatesan.dump;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import pl.karatesan.engine.utils.MovementHandler;
import pl.karatesan.engine.utils.ShaderUtils;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WersjaZKwadratemKolory {

    private long window;
    private int[] VAOs = new int[2];
    private int[] VBOs = new int[2];
    private int EBO;
    private int[] shaderPrograms = new int[2];
    private Vector2f playerPosition = new Vector2f(0.0f, 0.0f);
    private float speed = 100.0f; // NDC units per second
    private MovementHandler movementHandler = new MovementHandler();
    private int textureId;

    public void run() {
        init();
        loop();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        // Obsługa błędów GLFW
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Nie udało się zainicjalizować GLFW!");
        }

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(800, 600, "VAO/VBO Example", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Nie udało się utworzyć okna!");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // vsync (jak działa)
        glfwShowWindow(window);

        GL.createCapabilities();

        //=======================================================================

        // --- Shadery ---
        String vertexShaderSource =
                """
                        #version 330 core
                        layout (location = 0) in vec2 aPos;
                        uniform vec2 offset;
                        uniform mat4 projection;
                        void main() {
                            gl_Position = projection * vec4(aPos + offset, 0.0, 1.0);
                        }
                        """;

        String fragmentShaderSource =
                """
                        #version 330 core
                        out vec4 FragColor;
                        void main() {
                            FragColor = vec4(1.0, 0.0, 0.0, 1.0);
                        }
                        """;
        String fragmentShaderSource2 =
                """
                        #version 330 core
                        out vec4 FragColor;
                        void main() {
                            FragColor = vec4(0.60, 0.5, 0.15, 1.0);
                        }
                        """;
        try {
            int vertexShader = ShaderUtils.createShader(GL_VERTEX_SHADER, vertexShaderSource);
            int fragmentShader = ShaderUtils.createShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
            int fragmentShader2 = ShaderUtils.createShader(GL_FRAGMENT_SHADER, fragmentShaderSource2);

            shaderPrograms[0] = ShaderUtils.createShaderProgram(vertexShader, fragmentShader);
            shaderPrograms[1] = ShaderUtils.createShaderProgram(vertexShader, fragmentShader2);

            glDeleteShader(vertexShader);
            glDeleteShader(fragmentShader);
            glDeleteShader(fragmentShader2);
        }catch (RuntimeException e){
            System.err.println(e.getMessage());
        }
        // --- Wierzchołki kwadratu ---
        float[] vertices = {
                150.0f, 150.0f, 0.0f,   // dolny lewy
                200.0f, 150.0f, 0.0f,   // dolny prawy
                200.0f, 100.0f, 0.0f,   // górny prawy
                150.0f, 100.0f, 0.0f,   // gorny lewy
                300.0f, 300.0f, 0.0f,
                350.0f, 300.0f, 0.0f,
                325.0f, 350.0f, 0.0f,
                400.0f, 400.0f, 0.0f,
                450.0f, 400.0f, 0.0f,
                425.0f, 450.0f, 0.0f,
        };
        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };
        VAOs[0] = glGenVertexArrays();
        glBindVertexArray(VAOs[0]);
        VBOs[0] = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBOs[0]);
        GL15.glBufferData(GL_ARRAY_BUFFER, Arrays.copyOfRange(vertices, 12, 21), GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        VAOs[1] = glGenVertexArrays();
        VBOs[1] = glGenBuffers();
        glBindVertexArray(VAOs[1]);
        glBindBuffer(GL_ARRAY_BUFFER, VBOs[1]);
        glBufferData(GL_ARRAY_BUFFER, Arrays.copyOfRange(vertices, 21, vertices.length), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glBindVertexArray(0);

        //project our model coords to gpu coords (-1,1)
        WindowUtils.createOrthoProjection(shaderPrograms);
    }

    private void loop() {
        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            double fps = 1 / deltaTime;
            lastTime = currentTime;

            glClear(GL_COLOR_BUFFER_BIT);
            movementHandler.handle(window, playerPosition, speed, deltaTime);
            glPolygonMode(GL_FRONT,GL_LINE);
            glUseProgram(shaderPrograms[0]);
            int offsetLocation = glGetUniformLocation(shaderPrograms[0], "offset");
            glUniform2f(offsetLocation, playerPosition.x, playerPosition.y);

            glBindVertexArray(VAOs[0]);
            glDrawArrays(GL_TRIANGLES, 0, 3);
            //glDrawElements(GL_TRIANGLES,6,GL_UNSIGNED_INT,0);
            glBindVertexArray(0);

            glUseProgram(shaderPrograms[1]);
            glUniform2f(offsetLocation, playerPosition.x, playerPosition.y);
            glBindVertexArray(VAOs[1]);
            glDrawArrays(GL_TRIANGLES, 0, 3);
            //glDrawElements(GL_TRIANGLES,6,GL_UNSIGNED_INT,0);
            glBindVertexArray(0);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new WersjaZKwadratemKolory().run();
    }
}


