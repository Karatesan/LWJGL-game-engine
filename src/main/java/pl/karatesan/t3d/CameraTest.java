package pl.karatesan.t3d;

import org.joml.Matrix4f;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import pl.karatesan.engine.utils.Shader;
import pl.karatesan.dump.Texture;

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
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.NULL;

public class CameraTest {
    private long window;
    //shader
    private int[] VAOs = new int[2];
    private int[] VBOs = new int[2];
    private Texture[] textures = new Texture[2];
    private Shader shader;
    //mouse
    double lastX = 400;  // center of screen if 800 width
    double lastY = 300;
    boolean firstMouse = true;
    double[] xpos = new double[1];
    double[] ypos = new double[1];
    //camera
    Vector3f worldUp = new Vector3f(0, 1, 0);
    Vector3f cameraPosition;
    float yaw;
    float pitch;
    //movement
    Vector3f horizontalDirectionForMoving = new Vector3f();
    float cameraSpeed = 0.01f;

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
            shader = new Shader("t3d/VertexShader.txt", "t3d/FragmentShader.txt");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
        // --- Wierzchołki kwadratu ---
        float vertices3d[] = {
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
        };

        //Textures

        textures[0] = new Texture(GL_TEXTURE_2D, "/container.jpg", GL_CLAMP_TO_EDGE, GL_LINEAR);
        textures[1] = new Texture(GL_TEXTURE_2D, "/awesomeface.png", GL_REPEAT, GL_LINEAR);

        glEnable(GL_DEPTH_TEST);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        shader.use();
        shader.setUniform1i("ourTexture1", 0);
        shader.setUniform1i("ourTexture2", 1);

        //transforms

        VAOs[1] = glGenVertexArrays();
        glBindVertexArray(VAOs[1]);

        VBOs[1] = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBOs[1]);
        GL15.glBufferData(GL_ARRAY_BUFFER, vertices3d, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 20, 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 12);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }


    private void loop() {
        double lastTime = glfwGetTime();
        float rotationsPerSec = 0.3f; //per sec
        float radiansPerSec = (float) Math.toRadians(360) * rotationsPerSec;

        Vector3f[] cubePositions = {
                // Original 40 cubes
                new Vector3f(0.0f, 0.0f, -1.0f),
                new Vector3f(0.0f, 0.0f, -5.0f),
                new Vector3f(8.0f, 12.0f, -35.0f),
                new Vector3f(-6.5f, -8.0f, -12.0f),
                new Vector3f(-12.0f, -5.0f, -28.0f),
                new Vector3f(9.5f, -3.0f, -15.0f),
                new Vector3f(-7.0f, 10.0f, -22.0f),
                new Vector3f(5.0f, -7.0f, -10.0f),
                new Vector3f(6.0f, 8.0f, -18.0f),
                new Vector3f(4.5f, 2.0f, -8.0f),
                new Vector3f(-5.0f, 4.0f, -14.0f),
                new Vector3f(15.0f, -6.0f, -25.0f),
                new Vector3f(-10.0f, 15.0f, -30.0f),
                new Vector3f(3.0f, -12.0f, -20.0f),
                new Vector3f(-14.5f, 6.0f, -16.0f),
                new Vector3f(11.0f, 9.0f, -38.0f),
                new Vector3f(-3.0f, -4.0f, -32.0f),
                new Vector3f(18.0f, 2.0f, -40.0f),
                new Vector3f(-13.0f, -9.0f, -11.0f),
                new Vector3f(7.5f, 14.0f, -24.0f),
                new Vector3f(-16.0f, 1.0f, -36.0f),
                new Vector3f(10.0f, -14.0f, -19.0f),
                new Vector3f(-4.5f, 18.0f, -34.0f),
                new Vector3f(14.0f, 5.0f, -13.0f),
                new Vector3f(-8.0f, -2.0f, -27.0f),
                new Vector3f(1.5f, 16.0f, -31.0f),
                new Vector3f(12.5f, -10.0f, -29.0f),
                new Vector3f(-11.5f, 8.0f, -21.0f),
                new Vector3f(4.0f, 3.0f, -37.0f),
                new Vector3f(-15.5f, -13.0f, -39.0f),
                new Vector3f(17.0f, 11.0f, -17.0f),
                new Vector3f(-2.0f, 7.0f, -23.0f),
                new Vector3f(9.0f, -8.0f, -42.0f),
                new Vector3f(-17.5f, 13.0f, -15.0f),
                new Vector3f(13.5f, -1.0f, -33.0f),
                new Vector3f(-7.5f, 10.0f, -26.0f),
                new Vector3f(2.5f, -11.0f, -22.0f),
                new Vector3f(16.0f, 17.0f, -20.0f),
                new Vector3f(-9.5f, 4.0f, -35.0f),
                new Vector3f(19.0f, -15.0f, -41.0f),
                new Vector3f(-12.5f, -5.0f, -28.0f),

                // 120 new cubes
                new Vector3f(20.0f, 5.0f, -9.0f),
                new Vector3f(-18.0f, -7.0f, -44.0f),
                new Vector3f(11.5f, 19.0f, -16.0f),
                new Vector3f(-5.5f, -16.0f, -23.0f),
                new Vector3f(7.0f, 6.0f, -30.0f),
                new Vector3f(-14.0f, 14.0f, -12.0f),
                new Vector3f(16.5f, -4.0f, -38.0f),
                new Vector3f(-3.5f, 11.0f, -19.0f),
                new Vector3f(22.0f, -11.0f, -27.0f),
                new Vector3f(-19.5f, 3.0f, -33.0f),
                new Vector3f(8.5f, -18.0f, -14.0f),
                new Vector3f(-11.0f, 16.0f, -40.0f),
                new Vector3f(14.5f, 1.0f, -21.0f),
                new Vector3f(-6.0f, -3.0f, -36.0f),
                new Vector3f(3.5f, 13.0f, -11.0f),
                new Vector3f(-16.5f, -10.0f, -25.0f),
                new Vector3f(19.5f, 8.0f, -43.0f),
                new Vector3f(-1.0f, 20.0f, -18.0f),
                new Vector3f(12.0f, -5.0f, -31.0f),
                new Vector3f(-20.0f, -14.0f, -8.0f),
                new Vector3f(5.5f, 15.0f, -39.0f),
                new Vector3f(-8.5f, 2.0f, -13.0f),
                new Vector3f(17.5f, -12.0f, -24.0f),
                new Vector3f(-4.0f, 9.0f, -42.0f),
                new Vector3f(21.0f, 7.0f, -17.0f),
                new Vector3f(-13.5f, -19.0f, -29.0f),
                new Vector3f(10.5f, 4.0f, -10.0f),
                new Vector3f(-7.0f, 18.0f, -35.0f),
                new Vector3f(15.5f, -8.0f, -20.0f),
                new Vector3f(-2.5f, -6.0f, -45.0f),
                new Vector3f(6.5f, 12.0f, -26.0f),
                new Vector3f(-15.0f, 5.0f, -15.0f),
                new Vector3f(18.5f, -17.0f, -32.0f),
                new Vector3f(-10.5f, 10.0f, -9.0f),
                new Vector3f(4.5f, -2.0f, -37.0f),
                new Vector3f(-19.0f, 17.0f, -22.0f),
                new Vector3f(13.0f, -9.0f, -41.0f),
                new Vector3f(-5.0f, 6.0f, -16.0f),
                new Vector3f(9.5f, 19.0f, -28.0f),
                new Vector3f(-12.0f, -4.0f, -34.0f),
                new Vector3f(21.5f, 11.0f, -12.0f),
                new Vector3f(-17.0f, -15.0f, -38.0f),
                new Vector3f(2.0f, 8.0f, -19.0f),
                new Vector3f(-9.0f, 13.0f, -44.0f),
                new Vector3f(16.0f, -6.0f, -11.0f),
                new Vector3f(-3.0f, 1.0f, -25.0f),
                new Vector3f(11.0f, 14.0f, -33.0f),
                new Vector3f(-20.5f, -11.0f, -17.0f),
                new Vector3f(7.5f, -13.0f, -40.0f),
                new Vector3f(-14.5f, 9.0f, -8.0f),
                new Vector3f(19.0f, 3.0f, -29.0f),
                new Vector3f(-6.5f, -17.0f, -21.0f),
                new Vector3f(14.0f, 16.0f, -36.0f),
                new Vector3f(-1.5f, -8.0f, -13.0f),
                new Vector3f(8.0f, 5.0f, -43.0f),
                new Vector3f(-18.5f, 12.0f, -24.0f),
                new Vector3f(22.5f, -3.0f, -15.0f),
                new Vector3f(-11.5f, -12.0f, -31.0f),
                new Vector3f(5.0f, 10.0f, -9.0f),
                new Vector3f(-16.0f, 7.0f, -42.0f),
                new Vector3f(12.5f, -16.0f, -18.0f),
                new Vector3f(-4.5f, 15.0f, -27.0f),
                new Vector3f(17.0f, 2.0f, -35.0f),
                new Vector3f(-8.0f, -5.0f, -10.0f),
                new Vector3f(10.0f, 18.0f, -39.0f),
                new Vector3f(-21.0f, 4.0f, -20.0f),
                new Vector3f(3.0f, -10.0f, -45.0f),
                new Vector3f(-13.0f, 11.0f, -14.0f),
                new Vector3f(18.0f, -7.0f, -26.0f),
                new Vector3f(-2.0f, 20.0f, -32.0f),
                new Vector3f(6.0f, -14.0f, -11.0f),
                new Vector3f(-15.5f, 6.0f, -37.0f),
                new Vector3f(20.5f, 13.0f, -23.0f),
                new Vector3f(-7.5f, -9.0f, -41.0f),
                new Vector3f(15.0f, 9.0f, -16.0f),
                new Vector3f(-10.0f, -18.0f, -28.0f),
                new Vector3f(4.0f, 17.0f, -34.0f),
                new Vector3f(-19.5f, 1.0f, -12.0f),
                new Vector3f(13.5f, -4.0f, -44.0f),
                new Vector3f(-5.5f, 14.0f, -19.0f),
                new Vector3f(9.0f, 7.0f, -30.0f),
                new Vector3f(-12.5f, -13.0f, -9.0f),
                new Vector3f(21.0f, -9.0f, -36.0f),
                new Vector3f(-1.0f, 12.0f, -22.0f),
                new Vector3f(7.0f, -19.0f, -38.0f),
                new Vector3f(-17.5f, 8.0f, -13.0f),
                new Vector3f(11.5f, 4.0f, -43.0f),
                new Vector3f(-4.0f, -7.0f, -17.0f),
                new Vector3f(16.5f, 15.0f, -25.0f),
                new Vector3f(-9.5f, -2.0f, -40.0f),
                new Vector3f(2.5f, 11.0f, -10.0f),
                new Vector3f(-14.0f, 19.0f, -29.0f),
                new Vector3f(19.5f, -13.0f, -14.0f),
                new Vector3f(-6.0f, 3.0f, -35.0f),
                new Vector3f(10.5f, -15.0f, -21.0f),
                new Vector3f(-20.0f, 10.0f, -42.0f),
                new Vector3f(5.5f, 6.0f, -18.0f),
                new Vector3f(-11.0f, 16.0f, -8.0f),
                new Vector3f(14.5f, -10.0f, -33.0f),
                new Vector3f(-3.5f, -16.0f, -24.0f),
                new Vector3f(8.5f, 13.0f, -39.0f),
                new Vector3f(-18.0f, -1.0f, -15.0f),
                new Vector3f(22.0f, 8.0f, -27.0f),
                new Vector3f(-7.0f, -11.0f, -44.0f),
                new Vector3f(12.0f, 20.0f, -12.0f),
                new Vector3f(-15.0f, 2.0f, -31.0f),
                new Vector3f(17.5f, -5.0f, -19.0f),
                new Vector3f(-2.5f, 9.0f, -37.0f),
                new Vector3f(6.5f, -12.0f, -9.0f),
                new Vector3f(-13.5f, 14.0f, -41.0f),
                new Vector3f(20.0f, -17.0f, -23.0f),
                new Vector3f(-9.0f, 5.0f, -16.0f),
                new Vector3f(4.5f, 18.0f, -32.0f),
                new Vector3f(-16.5f, -6.0f, -26.0f),
                new Vector3f(13.0f, 1.0f, -45.0f),
                new Vector3f(-5.0f, -14.0f, -11.0f),
                new Vector3f(18.5f, 12.0f, -34.0f),
                new Vector3f(-10.5f, -8.0f, -20.0f)
        };
        //set starting values for camera and rotations (yaw & pitch)
        cameraPosition = new Vector3f(0.0f, 0.0f, 3.0f);
        yaw = (float) Math.toRadians(-90.0f);
        pitch = 0.0f;
        //calculate starting direction, camera front is vector showing direction, but it lacks info "from where"
        Vector3f cameraFront = new Vector3f(
                (float) Math.cos(yaw) * (float) Math.cos(pitch),
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * (float) Math.cos(pitch)
        ).normalize();
        // viewPoint is point in space where we are looking
        Vector3f viewPoint = new Vector3f();
        //calculate cam
        Matrix4f model = new Matrix4f();
        Matrix4f view;
        Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(45.0f), (float) 800 / 600, 0.1f, 100.0f);
        shader.setUniformM4("projection", projection);

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            //set texture units
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textures[0].getTextureId());
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, textures[1].getTextureId());

            //input
            handleInput(cameraFront, cubePositions);
            //calculate current point we look at
            cameraPosition.add(cameraFront, viewPoint);
           // Vector3f tpp = new Vector3f(cameraPosition.x,cameraPosition.y+3,cameraPosition.z-3);
            view = myLookAt(cameraPosition, viewPoint, worldUp);
            shader.setUniformM4("view", view);

            shader.use();
            glBindVertexArray(VAOs[1]);

//            model.identity().translate(cubePositions[0]);
//            shader.setUniformM4("model", model);
//            glDrawArrays(GL_TRIANGLES, 0, 36);


            for (int i = 0; i < cubePositions.length; ++i) {
                model.identity().translate(cubePositions[i]);
                model.rotate(
                        (float) i * 20,
                        new Vector3f(1.0f, 1.0f, 0.0f).normalize()
                );
                shader.setUniformM4("model", model);
                glDrawArrays(GL_TRIANGLES, 0, 36);
            }
            glBindVertexArray(0);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    private void handleInput(Vector3f cameraFront, Vector3f[] cubes) {
        Vector3f playerPos = new Vector3f(0,0,-1);
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            horizontalDirectionForMoving.set(cameraFront.x, 0, cameraFront.z).normalize();
            cameraPosition.add(horizontalDirectionForMoving.mul(cameraSpeed));
            //cubes[0].add(horizontalDirectionForMoving);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            horizontalDirectionForMoving.set(cameraFront.x, 0, cameraFront.z).normalize();
            cameraPosition.sub(horizontalDirectionForMoving.mul(cameraSpeed));
           // cubes[0].sub(horizontalDirectionForMoving);
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            cameraFront.cross(worldUp, horizontalDirectionForMoving).normalize();
            cameraPosition.sub(horizontalDirectionForMoving.mul(cameraSpeed, horizontalDirectionForMoving));
            //cubes[0].sub(horizontalDirectionForMoving);
        }

        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            cameraFront.cross(worldUp, horizontalDirectionForMoving).normalize();
            cameraPosition.add(horizontalDirectionForMoving.mul(cameraSpeed, horizontalDirectionForMoving));
           // cubes[0].add(horizontalDirectionForMoving);

        }

        glfwGetCursorPos(window, xpos, ypos);

        if (firstMouse) {
            lastX = xpos[0];
            lastY = ypos[0];
            firstMouse = false;
        }

        double xoffset = xpos[0] - lastX;
        double yoffset = lastY - ypos[0];  // ⚠️ Reversed: y-coordinates go from bottom to top
        lastX = xpos[0];
        lastY = ypos[0];

        float sensitivity = 0.15f;
        yaw += (float) Math.toRadians(xoffset * sensitivity);
        pitch += (float) Math.toRadians(yoffset * sensitivity);

        if (pitch > Math.toRadians(89.0f)) pitch = (float) Math.toRadians(89.0f);
        if (pitch < Math.toRadians(-89.0f)) pitch = (float) Math.toRadians(-89.0f);

        cameraFront.set(
                (float) Math.cos(yaw) * (float) Math.cos(pitch),
                (float) Math.sin(pitch),
                (float) Math.sin(yaw) * (float) Math.cos(pitch)
        ).normalize();
    }

    static void main(String[] args) {
        new CameraTest().run();
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
