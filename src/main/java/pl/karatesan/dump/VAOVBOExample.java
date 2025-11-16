package pl.karatesan.dump;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.joml.Matrix4f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import pl.karatesan.engine.utils.MovementHandler;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class VAOVBOExample {

    private long window;
    private int VAO;
    private int VBO;
    private int shaderProgram;
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

        // --- Wierzchołki kwadratu ---
        float[] vertices = {
                0.0f, 0.0f, 0.0f, 0.0f,// dolny lewy
                10.0f, 0.0f, 1.0f, 0.0f, // dolny prawy
                10.0f, 10.0f, 1.0f, 1.0f,// górny prawy
                0.0f, 10.0f, 0.0f, 1.0f   // górny lewy
        };

        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);
        VBO = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, VBO);
        GL15.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // --- Opis atrybutu wierzchołka ---
        /*
        0 → numer atrybutu w shaderze (layout(location = 0) in vec2 aPos;).
        2 → ile liczb na jeden wierzchołek (tu x i y → 2 komponenty).
        GL_FLOAT → typ danych w tablicy.
        false → czy normalizować (tu nie, bo NDC są już w -1..+1).
        stride=0 → odstęp między kolejnymi wierzchołkami – 0 daje tu „ciągłe” floaty.
        offset=0 → zaczynaj od początku bufora.
        glEnableVertexAttribArray(0) włącza ten atrybut: mówi GPU,
        że przy rysowaniu ma brać wierzchołki z bieżącego VBO według tego opisu.
        Ten krok wiąże VBO z VAO:
            VAO pamięta „dla atrybutu 0, użyj danych z VBO podpiętego do GL_ARRAY_BUFFER w momencie konfiguracji”.
         */
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        //IMAGE/TEXTURE=======================================

        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
//          load image from classpath
            URL resource = getClass().getResource("/sprite.png");
            if (resource == null) {
                throw new IllegalStateException("Texture not found in resources: /sprite.png");
            }
            Path imagePath = Paths.get(resource.toURI());
            ByteBuffer image = STBImage.stbi_load(imagePath.toString(), width, height, channels, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }
            textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);


            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(GL_TEXTURE_2D);

            STBImage.stbi_image_free(image);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // --- Shadery ---
        String vertexShaderSource =
                """
                        #version 330 core
                        layout (location = 0) in vec2 aPos;
                        layout (location = 1) in vec2 aTexCoord;
                        
                        out vec2 TexCoord;
                        
                        uniform mat4 projection;
                        uniform vec2 offset;
                        void main() {
                            gl_Position = projection * vec4(aPos + offset, 0.0, 1.0);
                            TexCoord = aTexCoord;
                        }
                        """;

        String fragmentShaderSource =
                """
                        #version 330 core
                           in vec2 TexCoord;
                           out vec4 FragColor;
                        
                           uniform sampler2D ourTexture;
                        
                           void main() {
                               FragColor = texture(ourTexture, TexCoord);
                           }
                        """;

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkLinkErrors(shaderProgram);

        // Macierz ortho: lewy dół = (0,0), prawy góry = (800,600)
        Matrix4f projection = new Matrix4f().ortho(0, 800, 0, 600, -1, 1);

        // Wyślij macierz jako uniform do GPU
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = projection.get(stack.mallocFloat(16));
            int projLoc = glGetUniformLocation(shaderProgram, "projection");
            glUseProgram(shaderProgram); // musimy mieć aktywny program
            glUniformMatrix4fv(projLoc, false, fb);
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        glBindVertexArray(0);
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

            glUseProgram(shaderProgram);
            int offsetLocation = glGetUniformLocation(shaderProgram, "offset");
            glUniform2f(offsetLocation, playerPosition.x, playerPosition.y);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, textureId);
            glUniform1i(glGetUniformLocation(shaderProgram, "ourTexture"), 0);

            glBindVertexArray(VAO);
            glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
            glBindVertexArray(0);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void checkCompileErrors(int shader, String type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Błąd kompilacji shadera: " + type);
            System.err.println(glGetShaderInfoLog(shader));
        }
    }

    private void checkLinkErrors(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Błąd linkowania programu");
            System.err.println(glGetProgramInfoLog(program));
        }
    }

    public static void main(String[] args) {
        new VAOVBOExample().run();
    }
}
