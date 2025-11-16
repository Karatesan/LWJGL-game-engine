package pl.karatesan.dump;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {

    private int textureId;
    private final int type;

    public Texture(int type, String path, int wrapType, int filterType) {

        this.type = type;
        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);

            URL resource = getClass().getResource(path);
            if (resource == null) {
                throw new IllegalStateException("Texture not found in resources: /sprite.png");
            }
            Path imagePath = Paths.get(resource.toURI());
            ByteBuffer image = STBImage.stbi_load(imagePath.toString(), width, height, channels, 4);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            textureId = glGenTextures();

            glBindTexture(type, textureId);
            glTexParameteri(type, GL_TEXTURE_MIN_FILTER, filterType);
            glTexParameteri(type, GL_TEXTURE_MAG_FILTER, filterType);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapType);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapType);
            glTexImage2D(type, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            glGenerateMipmap(type);

            STBImage.stbi_image_free(image);
        } catch (Exception e) {
            System.err.println("Something went wrong during processing texture, " + e.getMessage());
        }

    }

    public void bindTexture() {
        glBindTexture(type, textureId);
    }

    public int getTextureId() {
        return textureId;
    }
}
