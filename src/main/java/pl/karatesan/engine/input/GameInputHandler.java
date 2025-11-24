package pl.karatesan.engine.input;

import pl.karatesan.engine.window.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class GameInputHandler {
    private GenericInputHandler inputHandler;

    public GameInputHandler(Window window) {
        inputHandler = new GenericInputHandler(window);
    }

    public boolean isMoveUpPressed() {
        return inputHandler.isKeyPressed(GLFW_KEY_W);
    }

    public boolean isMoveDownPressed() {
        return inputHandler.isKeyPressed(GLFW_KEY_S);
    }

    public boolean isMoveLeftPressed() {
        return inputHandler.isKeyPressed(GLFW_KEY_A);
    }

    public boolean isMoveRightPressed() {
        return inputHandler.isKeyPressed(GLFW_KEY_D);
    }

    public boolean isMouseLeftDown() {
        return inputHandler.isCurrentLeftMouseDown();
    }

    public boolean isMouseLeftHeldContinuous() {
        return inputHandler.isCurrentLeftMouseDown() && inputHandler.isPreviousLeftMouseDown();
    }

    public boolean isMouseLeftJustClicked() {
        return inputHandler.isCurrentLeftMouseDown()&& !inputHandler.isPreviousLeftMouseDown();
    }
}
