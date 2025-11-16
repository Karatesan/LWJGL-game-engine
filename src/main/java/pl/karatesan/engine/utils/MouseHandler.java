package pl.karatesan.engine.utils;

import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class MouseHandler {

  private double[] xpos = new double[1];
  private double[] ypos = new double[1];
  private double lastX;
  private double lastY;
  private boolean firstMouse = true;
  private float sensitivity = 0.005f;

  public MouseHandler(int screenWidth, int screenHeight) {
    this.lastX = (double) screenWidth / 2;
    this.lastY = (double) screenHeight / 2;
  }

  public Vector2d handleMouseRotation(long window) {
    glfwGetCursorPos(window, xpos, ypos);

    if (firstMouse) {
      lastX = xpos[0];
      lastY = ypos[0];
      firstMouse = false;
    }
    double xoffset = xpos[0] - lastX;
    double yoffset = lastY - ypos[0];
    lastX = xpos[0];
    lastY = ypos[0];

    return new Vector2d(xoffset * sensitivity, yoffset * sensitivity);
  }

  public float getSensitivity() {
    return sensitivity;
  }

  public void setSensitivity(float sensitivity) {
    this.sensitivity = sensitivity;
  }
}
