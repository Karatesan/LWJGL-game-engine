package pl.karatesan.engine.utils;

import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

public class Shader {

    private int programId = 0;

    public Shader(String vertexShaderPath, String fragmentShaderPath) {
        String vertexShaderSource;
        String fragmentShaderSource;
        try {
            Path vPath = Paths.get(Shader.class.getClassLoader().getResource(vertexShaderPath).toURI());
            Path fPath = Paths.get(Shader.class.getClassLoader().getResource(fragmentShaderPath).toURI());
            vertexShaderSource = Files.readString(vPath, StandardCharsets.UTF_8);
            fragmentShaderSource = Files.readString(fPath, StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new RuntimeException("Error reading the shader file: " + exception.getMessage());
        }

        int vertexShader = ShaderUtils.createShader(GL20.GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = ShaderUtils.createShader(GL20.GL_FRAGMENT_SHADER, fragmentShaderSource);

        programId = ShaderUtils.createShaderProgram(vertexShader, fragmentShader);
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
    }

    public void use() {
        GL20.glUseProgram(programId);
    }

    public void delete() {
        GL20.glDeleteProgram(programId);
    }

    public void setUniform1i(String name, int value) {
        int location = GL20.glGetUniformLocation(programId, name);
        GL20.glUniform1i(location, value);
    }

    public void setUniformM4(String name, Matrix4f matrix) {
        int location = GL20.glGetUniformLocation(programId, name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buf = stack.mallocFloat(16);
            GL20.glUniformMatrix4fv(location, false, matrix.get(buf));
        }
    }

    public void setUniform1f(String name, float v0) {
        int location = GL20.glGetUniformLocation(programId, name);
        GL20.glUniform1f(location, v0);
    }

    public void setUniform2f(String name, float v0, float v1) {
        int location = GL20.glGetUniformLocation(programId, name);
        GL20.glUniform2f(location, v0, v1);
    }

    public void setUniform3f(String name, float v0, float v1, float v2) {
        int location = GL20.glGetUniformLocation(programId, name);
        GL20.glUniform3f(location, v0, v1, v2);
    }
}
