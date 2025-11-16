package pl.karatesan.t3d;

import org.joml.Matrix4f;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import pl.karatesan.engine.utils.Shader;
import pl.karatesan.engine.utils.Animation;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Matrix2D {
    private long window;
    //shader
    private int VAO;
    private int VBO;
    private Shader shader;
    Matrix4f model = new Matrix4f();
    Matrix4f rotate = new Matrix4f();
    Matrix4f view = new Matrix4f();
    boolean rotation = false;
    Animation animation = new Animation();

    int[] indices = {
            0, 1, 2,
            0, 2, 3
    };

    float[] colors = {
            // vertex 1 color
            1.0f, 0.0f, 0.0f,   // red
            // vertex 2 color
            0.0f, 1.0f, 0.0f,   // green
            // vertex 3 color
            0.0f, 0.0f, 1.0f,   // blue
            // vertex 4
            1.0f, 1.0f, 0.0f,   // yellow
            // vertex 5
            1.0f, 0.0f, 1.0f,   // magenta
            // vertex 6
            0.0f, 1.0f, 1.0f,   // cyan
            // vertex 7
            1.0f, 0.5f, 0.0f,   // orange
            // vertex 8
            0.5f, 0.0f, 0.5f,   // purple
            // vertex 9
            0.5f, 0.5f, 0.5f    // gray
    };

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

        window = glfwCreateWindow(800, 600, "2d", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Nie udało się utworzyć okna!");
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // vsync (jak działa)
        glfwShowWindow(window);

        GL.createCapabilities();

        //=======================================================================

        try {
            shader = new Shader("t2d/VertexShader.txt", "t2d/FragmentShader.txt");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        float vertices[] = {
                // positions          // colors
                0.5f, 0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f,
        };

        //transforms

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 12, 0);
        glEnableVertexAttribArray(0);

        int VBO2 = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO2);
        glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 12, 0);
        glEnableVertexAttribArray(1);

        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void loop() {

        float squareSize = 0.1f;
        float gap = 0.01f;
        float zPos = -1.0f;

        Vector3f[] squarePositions = {
                // Row 1 (top)
                new Vector3f(0.0f, 0.0f, zPos),
                new Vector3f(squareSize + gap, 0.0f, zPos),
                new Vector3f(2 * (squareSize + gap), 0.0f, zPos),

                // Row 2 (middle)
                new Vector3f(0.0f, -(squareSize + gap), zPos),
                new Vector3f(squareSize + gap, -(squareSize + gap), zPos),
                new Vector3f(2 * (squareSize + gap), -(squareSize + gap), zPos),

                // Row 3 (bottom)
                new Vector3f(0.0f, -2 * (squareSize + gap), zPos),
                new Vector3f(squareSize + gap, -2 * (squareSize + gap), zPos),
                new Vector3f(2 * (squareSize + gap), -2 * (squareSize + gap), zPos)
        };


        shader.use();
        float aspect = 800 / (float) 600;
        Matrix4f projection = new Matrix4f().ortho(-aspect, aspect, -1.0f, 1.0f, -1.0f, 1.0f);
        shader.setUniformM4("projection", projection);
        Matrix4f view = new Matrix4f();
        shader.setUniformM4("view", view);
        double lastTime = glfwGetTime();


        animation.setRotationAxis(0.0f, 0.0f, 1.0f);
        animation.setTargetAngle(90);
        animation.setTranslation(new Vector3f(1.0f,0.5f,0));

        //handleInput();
        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
                animation.startAnimation();
            }
        });

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glBindVertexArray(VAO);
            animation.animate(deltaTime);
            shader.setUniformM4("view", animation.getTransformation());

            for (int i = 0; i < squarePositions.length; ++i) {
                model.identity().translate(squarePositions[i]).scale(0.1f, 0.1f, 0.1f);
                shader.setUniformM4("model", model);
                shader.setUniform3f("color", colors[i * 3], colors[i * 3 + 1], colors[i * 3 + 2]);
                glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            }
            glBindVertexArray(0);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void handleInput() {
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            animation.startAnimation();
        }
    }

    static void main(String[] args) {
        new Matrix2D().run();
    }

    Matrix4f myLookAt(Vector3f cameraPos, Vector3f target, Vector3f worldUp) {
        // Direction FROM camera TO target (forward is -Z in OpenGL)
        Vector3f direction = new Vector3f();
        cameraPos.sub(target, direction).normalize();  // ✅ This is correct for lookAt

        // Right vector
        Vector3f right = new Vector3f();
        worldUp.cross(direction, right).normalize();

        // Up vector
        Vector3f up = new Vector3f();
        direction.cross(right, up);

        return new Matrix4f(
                right.x, up.x, direction.x, 0.0f,
                right.y, up.y, direction.y, 0.0f,
                right.z, up.z, direction.z, 0.0f,
                -right.dot(cameraPos),
                -up.dot(cameraPos),
                -direction.dot(cameraPos),
                1.0f
        );
    }
}
