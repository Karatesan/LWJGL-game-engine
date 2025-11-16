package pl.karatesan.engine.utils;

import static org.lwjgl.opengl.GL20.*;

public final class ShaderUtils {

    private ShaderUtils() {} // blokuje tworzenie obiektu

    public static int createShader(int type, String source) {
        int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        checkCompileErrors(shader, shaderTypeName(type));
        return shader;
    }

    public static int createShaderProgram(int... shaders) {
        int program = glCreateProgram();
        for (int shader : shaders) {
            glAttachShader(program, shader);
        }
        glLinkProgram(program);
        checkLinkErrors(program);
        return program;
    }

    private static void checkLinkErrors(int program) {
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Program link error:\n" + glGetProgramInfoLog(program));
        }
    }

    private static void checkCompileErrors(int shader, String type) {
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compile error (" + type + "):\n" + glGetShaderInfoLog(shader));
        }
    }

    private static String shaderTypeName(int type) {
        return switch (type) {
            case GL_VERTEX_SHADER -> "VERTEX";
            case GL_FRAGMENT_SHADER -> "FRAGMENT";
            //case GL_GEOMETRY_SHADER -> "GEOMETRY";
            default -> "UNKNOWN";
        };
    }
}