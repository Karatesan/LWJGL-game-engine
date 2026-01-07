package pl.karatesan.engine.shaders;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL15;
import pl.karatesan.engine.text.FontGlyph;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Mesh {
  private int vao;
  private int vbo;
  private int ebo;
  private float[] vertices;
  private int[] indices;
  private int strideCount;
  private final int maxGlyphs = 100;
  private final int floatsPerVertex = 5;
  private final int verticesPerGlyph = 6;
  private final int bytesPerGlyph = floatsPerVertex * verticesPerGlyph * Float.BYTES;
  private int capacityBytes = maxGlyphs * bytesPerGlyph;
  private int verticesToDraw = 0;

  public Mesh(int[] dataSizes, float[] vertices, int[] indices, boolean text) {
    this.initStandard(dataSizes, vertices, indices, text);
  }

  public Mesh(int[] dataSizes, boolean text) {
    this.initText(dataSizes, text);
  }

  private void initStandard(int[] dataSizes, float[] vertices, int[] indices, boolean text) {
    validate(dataSizes, vertices, indices, text);

    this.vertices = vertices;
    verticesToDraw = vertices.length;
    vao = glGenVertexArrays();
    glBindVertexArray(vao);
    if (this.indices != null) {
      useEBO();
    }
    useVBO(dataSizes, false);
    glBindVertexArray(0);
  }

  private void initText(int[] dataSizes, boolean text) {
    validate(dataSizes, null, null, text);

    vao = glGenVertexArrays();
    glBindVertexArray(vao);
    useVBO(dataSizes, true);
    glBindVertexArray(0);
  }

  private void validate(int[] dataSizes, float[] vertices, int[] indices, boolean text) {
    strideCount = 0;
    if (dataSizes == null
        || dataSizes.length == 0
        || ((vertices == null || vertices.length == 0) && !text)) {
      throw new IllegalArgumentException("DataSizes and vertices can't be null or empty");
    }
    for (int i = 0; i < dataSizes.length; i++) {
      int dataSize = dataSizes[i];
      if (dataSize <= 0) {
        throw new IllegalArgumentException(
            "DataSizes have to be greater than 0. Value at index: " + i + " = " + dataSize);
      }
      strideCount += dataSize;
    }
    if (indices != null) {
      if (indices.length == 0) {
        throw new IllegalArgumentException(
            "Indices array can't be empty. Set it to null if not needed");
      }
      int verticesCount = vertices.length / strideCount;
      for (int i = 0; i < indices.length; i++) {
        int index = indices[i];
        if (index < 0 || index >= verticesCount) {
          throw new IllegalArgumentException(
              "Invalid index value : index = " + i + " value=" + index);
        }
      }
      this.indices = indices;
    }
    if (!text && vertices.length % strideCount != 0) {
      throw new IllegalArgumentException(
          String.format(
              "Wrong data layout. Stride does not match vertices count. Stride = %s, vertices.length = %s",
              strideCount, vertices.length));
    }
  }

  private void useEBO() {
    ebo = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
  }

  private void useVBO(int[] dataSizes, boolean text) {
    vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    if (text) {
      GL15.glBufferData(GL_ARRAY_BUFFER, capacityBytes, GL_DYNAMIC_DRAW);
      vertices = new float[maxGlyphs * verticesPerGlyph * floatsPerVertex];
    } else GL15.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    int currentOffset = 0;
    int strideInBytes = strideCount * Float.BYTES;
    for (int i = 0; i < dataSizes.length; i++) {
      glVertexAttribPointer(
          i, dataSizes[i], GL_FLOAT, false, strideInBytes, (long) currentOffset * Float.BYTES);
      glEnableVertexAttribArray(i);
      currentOffset += dataSizes[i];
    }
    glBindBuffer(GL_ARRAY_BUFFER, 0);
  }

  public void updateVBO(
      Vector2f position, List<FontGlyph> glyphs, float scaleW, float scaleH, float baseline) {
    if (glyphs.size() > maxGlyphs)
      throw new RuntimeException(
          "Text to render exceeded max characters. Characters to render: "
              + glyphs.size()
              + ", max allowed: "
              + maxGlyphs);

    float penX = position.x;
    float penY = position.y;
    int arrPosition = 0;

    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    for (FontGlyph glyph : glyphs) {
      float left = penX + glyph.getXOffset();
      float right = left + glyph.getWidth();
      float top = penY;
      float bottom = top + glyph.getHeight();
      float uLeft = glyph.getX() / scaleW; // left
      float vTop = (glyph.getY() + glyph.getHeight()) / scaleH;
      float uRight = (glyph.getX() + glyph.getWidth()) / scaleW; // right
      float vBot = glyph.getY() / scaleH; // top

      vertices[arrPosition + 0] = left;
      vertices[arrPosition + 1] = top;
      vertices[arrPosition + 2] = 0;
      vertices[arrPosition + 3] = uLeft;
      vertices[arrPosition + 4] = vTop;

      vertices[arrPosition + 5] = right;
      vertices[arrPosition + 6] = top;
      vertices[arrPosition + 7] = 0;
      vertices[arrPosition + 8] = uRight;
      vertices[arrPosition + 9] = vTop;

      vertices[arrPosition + 10] = right;
      vertices[arrPosition + 11] = bottom;
      vertices[arrPosition + 12] = 0;
      vertices[arrPosition + 13] = uRight;
      vertices[arrPosition + 14] = vBot;

      vertices[arrPosition + 15] = right;
      vertices[arrPosition + 16] = bottom;
      vertices[arrPosition + 17] = 0;
      vertices[arrPosition + 18] = uRight;
      vertices[arrPosition + 19] = vBot;

      vertices[arrPosition + 20] = left;
      vertices[arrPosition + 21] = bottom;
      vertices[arrPosition + 22] = 0;
      vertices[arrPosition + 23] = uLeft;
      vertices[arrPosition + 24] = vBot;

      vertices[arrPosition + 25] = left;
      vertices[arrPosition + 26] = top;
      vertices[arrPosition + 27] = 0;
      vertices[arrPosition + 28] = uLeft;
      vertices[arrPosition + 29] = vTop;

      arrPosition += 30;
      penX += glyph.getXAdvance();
    }
    verticesToDraw = glyphs.size() * floatsPerVertex * verticesPerGlyph;
    glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
  }

  public void draw() {
    glBindVertexArray(vao);
    if (indices != null) {
      glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    } else glDrawArrays(GL_TRIANGLES, 0, verticesToDraw / strideCount);
    glBindVertexArray(0);
  }

  public void cleanup() {
    if (vbo != 0) {
      glDeleteBuffers(vbo);
      vbo = 0; // reset handle to prevent double-free
    }
    if (ebo != 0) {
      glDeleteBuffers(ebo);
      ebo = 0; // reset handle to prevent double-free
    }
    if (vao != 0) {
      glDeleteVertexArrays(vao);
      vao = 0; // reset handle to prevent double-delete
    }
  }
}
