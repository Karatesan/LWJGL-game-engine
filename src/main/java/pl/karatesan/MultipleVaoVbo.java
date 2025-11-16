//package pl.karatesan;
//
//import org.lwjgl.glfw.GLFWErrorCallback;
//import org.lwjgl.opengl.GL;
//import org.lwjgl.opengl.GL15;
//import org.joml.Matrix4f;
//import org.lwjgl.system.MemoryStack;
//
//import java.nio.FloatBuffer;
//
//import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.opengl.GL11.*;
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL20.*;
//import static org.lwjgl.opengl.GL30.*;
//import static org.lwjgl.system.MemoryUtil.NULL;
//
//public class WersjaZKwadratemKolory {
//
//    private long window;
//    private int VAO;
//    private int VBO;
//    private int EBO;
//    private int shaderProgram;
//    private Vector2f playerPosition = new Vector2f(0.0f, 0.0f);
//    private float speed = 100.0f; // NDC units per second
//    private MovementHandler movementHandler = new MovementHandler();
//    private int textureId;
//
//    public void run() {
//        init();
//        loop();
//        glfwDestroyWindow(window);
//        glfwTerminate();
//    }
//
//    private void init() {
//        // Obsługa błędów GLFW
//        GLFWErrorCallback.createPrint(System.err).set();
//
//        if (!glfwInit()) {
//            throw new IllegalStateException("Nie udało się zainicjalizować GLFW!");
//        }
//
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
//        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
//
//        window = glfwCreateWindow(800, 600, "VAO/VBO Example", NULL, NULL);
//        if (window == NULL) {
//            throw new RuntimeException("Nie udało się utworzyć okna!");
//        }
//
//        glfwMakeContextCurrent(window);
//        glfwSwapInterval(1); // vsync (jak działa)
//        glfwShowWindow(window);
//
//        GL.createCapabilities();
//
//        //=======================================================================
//
//        // --- Shadery ---
//        String vertexShaderSource =
//                """
//                        #version 330 core
//                        layout (location = 0) in vec2 aPos;
//                        uniform vec2 offset;
//                        uniform mat4 projection;
//                        void main() {
//                            gl_Position = projection * vec4(aPos + offset, 0.0, 1.0);
//                        }
//                        """;
//
//        String fragmentShaderSource =
//                """
//                        #version 330 core
//                        out vec4 FragColor;
//                        void main() {
//                            FragColor = vec4(1.0, 0.0, 0.0, 1.0);
//                        }
//                        """;
//
//        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
//        glShaderSource(vertexShader, vertexShaderSource);
//        glCompileShader(vertexShader);
//        checkCompileErrors(vertexShader, "VERTEX");
//
//        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
//        glShaderSource(fragmentShader, fragmentShaderSource);
//        glCompileShader(fragmentShader);
//        checkCompileErrors(fragmentShader, "FRAGMENT");
//
//        shaderProgram = glCreateProgram();
//        glAttachShader(shaderProgram, vertexShader);
//        glAttachShader(shaderProgram, fragmentShader);
//        glLinkProgram(shaderProgram);
//        checkLinkErrors(shaderProgram);
//
//        // --- Wierzchołki kwadratu ---
//        float[] vertices = {
//                150.0f, 150.0f, 0.0f,   // dolny lewy
//                200.0f, 150.0f, 0.0f,   // dolny prawy
//                200.0f, 100.0f, 0.0f,   // górny prawy
//                150.0f, 100.0f, 0.0f,         // gorny lewy
//                300.0f,300.0f,0.0f,
//                350.0f,300.0f,0.0f,
//                325.0f,350.0f,0.0f,
//                400.0f,400.0f,0.0f,
//                450.0f,400.0f,0.0f,
//                425.0f,450.0f,0.0f,
//        };
//        int[] indices = {
//                0,1,2,
//                2,3,0
//        };
//        EBO = glGenBuffers();
//
//        VAO = glGenVertexArrays();
//        glBindVertexArray(VAO);
//
//        VBO = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, VBO);
//        GL15.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices,GL_STATIC_DRAW);
//
//        glVertexAttribPointer(0, 2, GL_FLOAT, false, 3 * Float.BYTES, 0);
//        glEnableVertexAttribArray(0);
//        glBindVertexArray(0);
//
//        Matrix4f projection = new Matrix4f().ortho(0, 800, 0, 600, -1, 1);
//
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            FloatBuffer fb = projection.get(stack.mallocFloat(16));
//            int projLoc = glGetUniformLocation(shaderProgram, "projection");
//            glUseProgram(shaderProgram); // musimy mieć aktywny program
//            glUniformMatrix4fv(projLoc, false, fb);
//        }
//
//        glDeleteShader(vertexShader);
//        glDeleteShader(fragmentShader);
//    }
//
//    private void loop() {
//        double lastTime = glfwGetTime();
//
//        while (!glfwWindowShouldClose(window)) {
//            double currentTime = glfwGetTime();
//            double deltaTime = currentTime - lastTime;
//            double fps = 1 / deltaTime;
//            lastTime = currentTime;
//
//            glClear(GL_COLOR_BUFFER_BIT);
//            movementHandler.handle(window, playerPosition, speed, deltaTime);
//
//            glUseProgram(shaderProgram);
//            int offsetLocation = glGetUniformLocation(shaderProgram, "offset");
//            glUniform2f(offsetLocation, playerPosition.x, playerPosition.y);
//
//            glBindVertexArray(VAO);
//            glDrawArrays(GL_TRIANGLES, 4, 6);
//            //glDrawElements(GL_TRIANGLES,6,GL_UNSIGNED_INT,0);
//            glBindVertexArray(0);
//            glfwSwapBuffers(window);
//            glfwPollEvents();
//        }
//    }
//
//    private void checkCompileErrors(int shader, String type) {
//        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
//            System.err.println("Błąd kompilacji shadera: " + type);
//            System.err.println(glGetShaderInfoLog(shader));
//        }
//    }
//
//    private void checkLinkErrors(int program) {
//        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
//            System.err.println("Błąd linkowania programu");
//            System.err.println(glGetProgramInfoLog(program));
//        }
//    }
//
//    public static void main(String[] args) {
//        new pl.karatesan.dump.WersjaZKwadratemKolory().run();
//    }
//}
//
//
