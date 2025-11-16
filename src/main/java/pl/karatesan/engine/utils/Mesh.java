package pl.karatesan.engine.utils;

import org.lwjgl.opengl.GL15;

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
  private final float[] vertices;
  private int[] indices;
  private int strideCount;

  public Mesh(int[] dataSizes, float[] vertices, int[] indices) {

    if (dataSizes == null || dataSizes.length == 0 || vertices == null || vertices.length == 0) {
      throw new IllegalArgumentException("DataSizes and vertices can't be null or empty");
    }
    if (indices != null) {
      if (indices.length == 0) {
        throw new IllegalArgumentException(
            "Indices array can't be empty. Set it to null if not needed");
      }
      for (int i = 0; i < dataSizes.length; i++) {
        int dataSize = dataSizes[i];
        if (dataSize <= 0) {
          throw new IllegalArgumentException(
              "DataSizes have to be greater than 0. Value at index: " + i + " = " + dataSize);
        }
        strideCount += dataSize;
      }
      for (int i = 0; i < indices.length; i++) {
        int index = indices[i];
        if (index < 0 || index > vertices.length / strideCount) {
          throw new IllegalArgumentException(
              "Invalid indice value : index = " + i + " value=" + index);
        }
      }
      this.indices = indices;
    }
    if (vertices.length % strideCount != 0) {
      throw new IllegalArgumentException(
          String.format(
              "Wrong data layout. Stride does not match vertices count. Stride = %s, vertices.length = %s",
              strideCount, vertices.length));
    }

    this.vertices = vertices;

    vao = glGenVertexArrays();
    glBindVertexArray(vao);
    if (this.indices != null) {
      useEBO();
    }
    useVBO(dataSizes);
    glBindVertexArray(0);
  }

  private void useEBO() {
    ebo = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
  }

  private void useVBO(int[] dataSizes) {
    vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    GL15.glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

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

  public void draw() {
    glBindVertexArray(vao);
    if (indices != null) {
      glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    } else glDrawArrays(GL_TRIANGLES, 0, vertices.length / strideCount);
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
