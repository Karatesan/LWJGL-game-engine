package pl.karatesan.dump;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import pl.karatesan.engine.utils.Shader;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class TeczowyTrojkat {
    private long window;
    private int[] VAOs = new int[2];
    private int[] VBOs = new int[2];
    private Texture[] textures = new Texture[2];
    private int EBO;
    private Shader shader;
    float opacity = 0;
    boolean upPressed = false;
    boolean downPressed = false;
    Matrix4f toRotate;
    FloatBuffer mat;

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

        try {
            shader = new Shader("VertexShader.txt", "FragmentShader.txt");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        // --- Wierzchołki kwadratu ---
        float vertices[] = {
                // positions          // colors           // texture coords (note that we changed them to 'zoom in' on our texture image)
                0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
                0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom left
                -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f  // top left
        };
        int[] indices = {
                0, 1, 2,
                0, 2, 3
        };
        float[] colors = {
                1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f
        };

        float[] texCoords = {
                0.0f, 0.0f,  // lower-left corner
                1.0f, 0.0f,  // lower-right corner
                0.5f, 1.0f   // top-center corner
        };

        //Textures

        textures[0] = new Texture(GL_TEXTURE_2D, "/container.jpg", GL_CLAMP_TO_EDGE, GL_LINEAR);
        textures[1] = new Texture(GL_TEXTURE_2D, "/awesomeface.png", GL_REPEAT, GL_LINEAR);

        shader.use();
        shader.setUniform1i("ourTexture1", 0);
        shader.setUniform1i("ourTexture2", 1);

        //transforms
        toRotate = new Matrix4f();
//        toRotate.rotate((float) Math.toRadians(90), new Vector3f(0.0f, 0.0f, 1.0f)).scale(0.5f, 0.5f, 0.5f);
//        mat = BufferUtils.createFloatBuffer(16);
//        toRotate.get(mat);

        VAOs[0] = glGenVertexArrays();
        glBindVertexArray(VAOs[0]);

        VBOs[0] = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBOs[0]);
        GL15.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 32, 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 32, 12);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 32, 24);

        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }


    private void loop() {
        double lastTime = glfwGetTime();
        float rotationsPerSec = 0.3f; //per sec
        float radiansPerSec = (float) Math.toRadians(360) * rotationsPerSec;

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            System.out.println(1 / deltaTime);
            lastTime = currentTime;
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textures[0].getTextureId());
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, textures[1].getTextureId());

            handleInput();
            Vector3f axis = new Vector3f(1.0f, 1.0f, 0.0f);
            Matrix4f mat = new Matrix4f();
            shader.use();
            glBindVertexArray(VAOs[0]);

            mat.identity().rotate((float) glfwGetTime(),0.0f,1.0f,0);
            shader.setUniformM4("transform", mat);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

            //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

            glBindVertexArray(0);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void handleInput() {
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS && !upPressed) {
            if (opacity < 1.0f) opacity += 0.1f;
            shader.setUniform1f("opacity", opacity);
            upPressed = true;
        } else if (upPressed && glfwGetKey(window, GLFW_KEY_UP) == GLFW_RELEASE) {
            upPressed = false;
        } else if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS && !downPressed) {
            if (opacity >= 0.1f) opacity -= 0.1f;
            shader.setUniform1f("opacity", opacity);
            downPressed = true;
        } else if (downPressed && glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_RELEASE) {
            downPressed = false;
        }
    }

    public static void main(String[] args) {
        new TeczowyTrojkat().run();
    }
}
